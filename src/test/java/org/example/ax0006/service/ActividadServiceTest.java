package org.example.ax0006.service;

import org.example.ax0006.db.H2;
import org.example.ax0006.entity.Actividad;
import org.example.ax0006.entity.Usuario;
import org.example.ax0006.repository.ActividadRepository;
import org.example.ax0006.repository.UsuarioRepository;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ActividadService")
class ActividadServiceTest {

    private H2 h2;
    private ActividadRepository actividadRepository;
    private UsuarioRepository usuarioRepository;
    private ActividadService actividadService;

    @BeforeEach
    void prepararEscenario() {
        h2 = new H2();
        h2.inicializarDB();
        actividadRepository = new ActividadRepository(h2);
        usuarioRepository = new UsuarioRepository(h2);
        actividadService = new ActividadService(actividadRepository, usuarioRepository);
    }

    @AfterEach
    void BorrarDB() {
        H2 h2Final = new H2();
        try (Connection conn = h2Final.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
            stmt.execute("DROP ALL OBJECTS");
            stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Falló la limpieza de la base de datos al final de la prueba");
        }
    }

    private List<Actividad> obtenerTodasLasActividades() throws Exception {
        List<Actividad> lista = new ArrayList<>();
        String sql = "SELECT * FROM ActividadSistema ORDER BY idActividad DESC";
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Actividad act = new Actividad();
                act.setIdActividad(rs.getInt("idActividad"));
                act.setTipo(rs.getString("tipo"));
                act.setModulo(rs.getString("modulo"));
                act.setOrigen(rs.getString("origen"));
                act.setDescripcion(rs.getString("descripcion"));
                int idActor = rs.getInt("idUsuarioActor");
                act.setIdUsuarioActor(rs.wasNull() ? null : idActor);
                act.setRolDestino(rs.getString("rolDestino"));
                lista.add(act);
            }
        }
        return lista;
    }

    private void asignarRolAUsuario(int idUsuario, int idRol) throws Exception {
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO RolConciertoUsuario (idRol, idUsuario, idConcierto) VALUES (?, ?, ?)")) {
            stmt.setInt(1, idRol);
            stmt.setInt(2, idUsuario);
            stmt.setInt(3, 0);
            stmt.executeUpdate();
        }
    }

    private boolean isActividadRevisadaPorUsuario(int idActividad, int idUsuario) throws Exception {
        String sql = "SELECT revisado FROM EstadoActividadUsuario WHERE idActividad = ? AND idUsuario = ?";
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idActividad);
            stmt.setInt(2, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("revisado");
                }
            }
        }
        return false;
    }

    @Nested
    @DisplayName("registrarLogin")
    class RegistrarLogin {

        @Test
        @DisplayName("debe guardar actividad de acceso para un usuario válido")
        void registrarLoginExito() throws Exception {
            Usuario u = new Usuario();
            u.setNombre("Carlos");
            u.setContrasena("pass123");
            u.setGmail("carlos@example.com");
            u.setIdRol(4);
            assertTrue(usuarioRepository.guardar(u));

            Usuario savedUser = usuarioRepository.buscarPorNombre("Carlos");
            assertNotNull(savedUser);

            actividadService.registrarLogin(savedUser);

            List<Actividad> actividades = obtenerTodasLasActividades();
            assertEquals(1, actividades.size());

            Actividad loginAct = actividades.get(0);
            assertEquals("ACCESO", loginAct.getTipo());
            assertEquals("Inicio de sesión", loginAct.getModulo());
            assertEquals("Sistema", loginAct.getOrigen());
            assertTrue(loginAct.getDescripcion().contains("Carlos"));
            assertEquals(savedUser.getIdUsuario(), loginAct.getIdUsuarioActor());
            assertEquals("Administrador", loginAct.getRolDestino());
        }

        @Test
        @DisplayName("debe retornar sin registrar nada si el usuario es null")
        void registrarLoginUsuarioNull() throws Exception {
            actividadService.registrarLogin(null);
            List<Actividad> actividades = obtenerTodasLasActividades();
            assertTrue(actividades.isEmpty());
        }
    }

    @Nested
    @DisplayName("registrarLogout")
    class RegistrarLogout {

        @Test
        @DisplayName("debe guardar actividad de cierre de sesión para un usuario válido")
        void registrarLogoutExito() throws Exception {
            Usuario u = new Usuario();
            u.setNombre("Diana");
            u.setContrasena("pass123");
            u.setGmail("diana@example.com");
            u.setIdRol(2);
            assertTrue(usuarioRepository.guardar(u));

            Usuario savedUser = usuarioRepository.buscarPorNombre("Diana");
            assertNotNull(savedUser);

            actividadService.registrarLogout(savedUser);

            List<Actividad> actividades = obtenerTodasLasActividades();
            assertEquals(1, actividades.size());

            Actividad logoutAct = actividades.get(0);
            assertEquals("ACCESO", logoutAct.getTipo());
            assertEquals("Cierre de sesión", logoutAct.getModulo());
            assertEquals("Sistema", logoutAct.getOrigen());
            assertTrue(logoutAct.getDescripcion().contains("Diana"));
            assertEquals(savedUser.getIdUsuario(), logoutAct.getIdUsuarioActor());
            assertEquals("Administrador", logoutAct.getRolDestino());
        }

        @Test
        @DisplayName("debe retornar sin registrar nada si el usuario es null")
        void registrarLogoutUsuarioNull() throws Exception {
            actividadService.registrarLogout(null);
            List<Actividad> actividades = obtenerTodasLasActividades();
            assertTrue(actividades.isEmpty());
        }
    }

    @Nested
    @DisplayName("registrarAccion")
    class RegistrarAccion {

        @Test
        @DisplayName("debe registrar correctamente una acción con actor")
        void registrarAccionExitoConUsuario() throws Exception {
            Usuario u = new Usuario();
            u.setNombre("Enrique");
            u.setContrasena("pass123");
            u.setGmail("enrique@example.com");
            u.setIdRol(3);
            assertTrue(usuarioRepository.guardar(u));

            Usuario savedUser = usuarioRepository.buscarPorNombre("Enrique");
            assertNotNull(savedUser);

            actividadService.registrarAccion("CREAR", "CONCIERTO", "Creado nuevo concierto", savedUser, "Manager");

            List<Actividad> actividades = obtenerTodasLasActividades();
            assertEquals(1, actividades.size());

            Actividad act = actividades.get(0);
            assertEquals("CREAR", act.getTipo());
            assertEquals("CONCIERTO", act.getModulo());
            assertEquals("Sistema", act.getOrigen());
            assertEquals("Creado nuevo concierto", act.getDescripcion());
            assertEquals(savedUser.getIdUsuario(), act.getIdUsuarioActor());
            assertEquals("Manager", act.getRolDestino());
        }

        @Test
        @DisplayName("debe registrar correctamente una acción sin actor")
        void registrarAccionSinUsuario() throws Exception {
            actividadService.registrarAccion("SISTEMA_ERR", "DATABASE", "Falla de conexión", null, "Administrador");

            List<Actividad> actividades = obtenerTodasLasActividades();
            assertEquals(1, actividades.size());

            Actividad act = actividades.get(0);
            assertEquals("SISTEMA_ERR", act.getTipo());
            assertEquals("DATABASE", act.getModulo());
            assertEquals("Sistema", act.getOrigen());
            assertEquals("Falla de conexión", act.getDescripcion());
            assertNull(act.getIdUsuarioActor());
            assertEquals("Administrador", act.getRolDestino());
        }
    }

    @Nested
    @DisplayName("listarParaUsuario")
    class ListarParaUsuario {

        @Test
        @DisplayName("debe retornar lista vacía si el usuario es null")
        void listarParaUsuarioNull() {
            List<Actividad> result = actividadService.listarParaUsuario(null, "TODO");
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("debe listar actividades según los roles y actor")
        void listarParaUsuarioRolesYActor() throws Exception {
            Usuario u = new Usuario();
            u.setNombre("Laura");
            u.setContrasena("pass123");
            u.setGmail("laura@example.com");
            u.setIdRol(2);
            assertTrue(usuarioRepository.guardar(u));

            Usuario savedUser = usuarioRepository.buscarPorNombre("Laura");
            assertNotNull(savedUser);
            asignarRolAUsuario(savedUser.getIdUsuario(), 2);

            actividadRepository.registrarActividad("MODIFICAR", "CONCIERTO", "Sistema", "Cambio de aforo", null, "Tecnico");
            actividadRepository.registrarActividad("CREAR", "USUARIO", "Sistema", "Creado usuario nuevo", null, "Administrador");
            actividadRepository.registrarActividad("CREAR", "INVENTARIO", "Sistema", "Agregado micrófono", savedUser.getIdUsuario(), null);
            actividadRepository.registrarActividad("APROBAR", "CONTRATO", "Sistema", "Contrato firmado", null, "Manager");

            List<Actividad> list = actividadService.listarParaUsuario(savedUser, "TODO");
            assertEquals(2, list.size());

            boolean tieneModificarConcierto = list.stream().anyMatch(a -> "MODIFICAR".equals(a.getTipo()) && "CONCIERTO".equals(a.getModulo()));
            boolean tieneCrearInventario = list.stream().anyMatch(a -> "CREAR".equals(a.getTipo()) && "INVENTARIO".equals(a.getModulo()));
            assertTrue(tieneModificarConcierto);
            assertTrue(tieneCrearInventario);
        }

        @Test
        @DisplayName("debe filtrar correctamente por tipo")
        void listarParaUsuarioFiltroTipo() throws Exception {
            Usuario u = new Usuario();
            u.setNombre("Andres");
            u.setContrasena("pass123");
            u.setGmail("andres@example.com");
            u.setIdRol(1);
            assertTrue(usuarioRepository.guardar(u));

            Usuario savedUser = usuarioRepository.buscarPorNombre("Andres");
            assertNotNull(savedUser);
            asignarRolAUsuario(savedUser.getIdUsuario(), 1);

            actividadRepository.registrarActividad("CREAR", "INVENTARIO", "Sistema", "Item creado", null, "Administrador");
            actividadRepository.registrarActividad("ACCESO", "LOGIN", "Sistema", "Sesión iniciada", null, "Administrador");

            List<Actividad> todo = actividadService.listarParaUsuario(savedUser, "TODO");
            assertEquals(2, todo.size());

            List<Actividad> filtroCrear = actividadService.listarParaUsuario(savedUser, "CREAR");
            assertEquals(1, filtroCrear.size());
            assertEquals("CREAR", filtroCrear.get(0).getTipo());
        }
    }

    @Nested
    @DisplayName("contarPendientes")
    class ContarPendientes {

        @Test
        @DisplayName("debe retornar 0 si el usuario es null")
        void contarPendientesUsuarioNull() {
            assertEquals(0, actividadService.contarPendientes(null));
        }

        @Test
        @DisplayName("debe contar correctamente las actividades sin revisar")
        void contarPendientesCorrectamente() throws Exception {
            Usuario u = new Usuario();
            u.setNombre("Felipe");
            u.setContrasena("pass123");
            u.setGmail("felipe@example.com");
            u.setIdRol(4);
            assertTrue(usuarioRepository.guardar(u));

            Usuario savedUser = usuarioRepository.buscarPorNombre("Felipe");
            assertNotNull(savedUser);
            asignarRolAUsuario(savedUser.getIdUsuario(), 4);

            actividadRepository.registrarActividad("ALERTA", "SISTEMA", "Sistema", "Alerta 1", null, "Staff");
            actividadRepository.registrarActividad("ALERTA", "SISTEMA", "Sistema", "Alerta 2", null, "Staff");
            actividadRepository.registrarActividad("ALERTA", "SISTEMA", "Sistema", "Alerta 3", null, "Tecnico");

            assertEquals(2, actividadService.contarPendientes(savedUser));

            List<Actividad> list = actividadService.listarParaUsuario(savedUser, "TODO");
            assertEquals(2, list.size());
            int idARevisar = list.get(0).getIdActividad();
            actividadRepository.marcarRevisado(idARevisar, savedUser.getIdUsuario());

            assertEquals(1, actividadService.contarPendientes(savedUser));
        }
    }

    @Nested
    @DisplayName("marcarRevisado")
    class MarcarRevisado {

        @Test
        @DisplayName("debe retornar sin hacer nada si el usuario es null")
        void marcarRevisadoUsuarioNull() {
            assertDoesNotThrow(() -> actividadService.marcarRevisado(1, null));
        }

        @Test
        @DisplayName("debe marcar como revisada la actividad para un usuario válido")
        void marcarRevisadoExito() throws Exception {
            Usuario u = new Usuario();
            u.setNombre("Maria");
            u.setContrasena("pass123");
            u.setGmail("maria@example.com");
            u.setIdRol(1);
            assertTrue(usuarioRepository.guardar(u));

            Usuario savedUser = usuarioRepository.buscarPorNombre("Maria");
            assertNotNull(savedUser);
            asignarRolAUsuario(savedUser.getIdUsuario(), 1);

            actividadRepository.registrarActividad("TIPO", "MODULO", "Sistema", "Desc", null, "Administrador");
            List<Actividad> list = actividadService.listarParaUsuario(savedUser, "TODO");
            assertEquals(1, list.size());
            int idActividad = list.get(0).getIdActividad();

            assertFalse(isActividadRevisadaPorUsuario(idActividad, savedUser.getIdUsuario()));
            actividadService.marcarRevisado(idActividad, savedUser);
            assertTrue(isActividadRevisadaPorUsuario(idActividad, savedUser.getIdUsuario()));
        }
    }

    @Nested
    @DisplayName("Resolución de Rol Principal")
    class ResolucionRolPrincipal {

        @Test
        @DisplayName("debe resolver a Administrador para usuario con rol global Administrador")
        void resolverAdminPorId() throws Exception {
            Usuario u = new Usuario();
            u.setNombre("AdminGlobal");
            u.setContrasena("pass");
            u.setGmail("adminglobal@example.com");
            u.setIdRol(1);
            assertTrue(usuarioRepository.guardar(u));

            Usuario admin = usuarioRepository.buscarPorNombre("AdminGlobal");
            assertNotNull(admin);

            actividadRepository.registrarActividad("TIPO", "MODULO", "Sistema", "Solo Admin", null, "Administrador");

            List<Actividad> acts = actividadService.listarParaUsuario(admin, "TODO");
            assertFalse(acts.isEmpty());
        }

        @Test
        @DisplayName("debe resolver a Administrador si el nombre del usuario es admin en cualquier caso")
        void resolverAdminPorNombre() throws Exception {
            Usuario u = new Usuario();
            u.setNombre("admin");
            u.setContrasena("pass");
            u.setGmail("admin_test@example.com");
            u.setIdRol(0);
            assertTrue(usuarioRepository.guardar(u));

            Usuario savedUser = usuarioRepository.buscarPorNombre("admin");
            assertNotNull(savedUser);

            actividadRepository.registrarActividad("TIPO", "MODULO", "Sistema", "Solo Admin", null, "Administrador");

            List<Actividad> acts = actividadService.listarParaUsuario(savedUser, "TODO");
            assertFalse(acts.isEmpty());
        }

        @Test
        @DisplayName("debe retornar el primer rol asignado si tiene múltiples")
        void resolverPrimerRolDeMultiples() throws Exception {
            Usuario u = new Usuario();
            u.setNombre("Multiroles");
            u.setContrasena("pass");
            u.setGmail("multi@example.com");
            u.setIdRol(4);
            assertTrue(usuarioRepository.guardar(u));

            Usuario savedUser = usuarioRepository.buscarPorNombre("Multiroles");
            assertNotNull(savedUser);

            asignarRolAUsuario(savedUser.getIdUsuario(), 4);
            asignarRolAUsuario(savedUser.getIdUsuario(), 2);

            actividadRepository.registrarActividad("TIPO", "MODULO", "Sistema", "Alerta Staff", null, "Staff");
            actividadRepository.registrarActividad("TIPO", "MODULO", "Sistema", "Alerta Tecnico", null, "Tecnico");

            List<Actividad> acts = actividadService.listarParaUsuario(savedUser, "TODO");
            assertFalse(acts.isEmpty());
        }

        @Test
        @DisplayName("debe retornar Administrador si la lista de roles del usuario contiene Administrador")
        void resolverAdminSiEstaEnListaDeMultiples() throws Exception {
            Usuario u = new Usuario();
            u.setNombre("MultirolesAdmin");
            u.setContrasena("pass");
            u.setGmail("multiadmin@example.com");
            u.setIdRol(4);
            assertTrue(usuarioRepository.guardar(u));

            Usuario savedUser = usuarioRepository.buscarPorNombre("MultirolesAdmin");
            assertNotNull(savedUser);

            asignarRolAUsuario(savedUser.getIdUsuario(), 4);
            asignarRolAUsuario(savedUser.getIdUsuario(), 1);

            actividadRepository.registrarActividad("TIPO", "MODULO", "Sistema", "Alerta Admin", null, "Administrador");

            List<Actividad> acts = actividadService.listarParaUsuario(savedUser, "TODO");
            boolean tieneAlertaAdmin = acts.stream().anyMatch(a -> "Alerta Admin".equals(a.getDescripcion()));
            assertTrue(tieneAlertaAdmin);
        }

        @Test
        @DisplayName("debe resolver a Sin rol para un idRol=0 sin roles en concierto")
        void resolverSinRolParaIdDesconocido() throws Exception {
            Usuario u = new Usuario();
            u.setNombre("UsuarioSinRol");
            u.setContrasena("pass");
            u.setGmail("sinrol@example.com");
            u.setIdRol(0);
            assertTrue(usuarioRepository.guardar(u));

            Usuario savedUser = usuarioRepository.buscarPorNombre("UsuarioSinRol");
            assertNotNull(savedUser);

            List<Actividad> acts = actividadService.listarParaUsuario(savedUser, "TODO");
            assertNotNull(acts);
        }
    }
}