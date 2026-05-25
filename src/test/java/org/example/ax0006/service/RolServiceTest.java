package org.example.ax0006.service;

import org.example.ax0006.db.H2;
import org.example.ax0006.entity.Rol;
import org.example.ax0006.entity.Usuario;
import org.example.ax0006.repository.RolRepository;
import org.example.ax0006.repository.UsuarioRepository;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RolService")
class RolServiceTest {

    private H2 h2;
    private RolRepository rolRepository;
    private UsuarioRepository usuarioRepository;
    private RolService rolService;

    @BeforeEach
    void prepararEscenario() {
        h2 = new H2();

        try (Connection conn = h2.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
            stmt.execute("DROP ALL OBJECTS");
            stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Falló la limpieza de la base de datos antes de la prueba");
        }

        h2.inicializarDB();

        rolRepository = new RolRepository(h2);
        usuarioRepository = new UsuarioRepository(h2);
        rolService = new RolService(rolRepository, usuarioRepository);
    }

    @AfterAll
    static void borrarDB() {
        H2 h2 = new H2();
        try (Connection conn = h2.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
            stmt.execute("DROP ALL OBJECTS");
            stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Falló la limpieza de la base de datos después de las pruebas");
        } finally {
            h2.cerrarServidor();
        }
    }

    // obtenerRolesAsignables
    @Nested
    @DisplayName("obtenerRolesAsignables")
    class ObtenerRolesAsignables {

        @Test
        @DisplayName("retorna roles sin incluir Administrador ni Sin rol")
        void retornaRolesSinAdminNiSinRol() {
            //Se obtienen los roles asignables desde el servicio
            List<Rol> roles = rolService.obtenerRolesAsignables();

            //No debe retornar null ni estar vacía
            assertNotNull(roles, "La lista de roles no debe ser null.");
            assertFalse(roles.isEmpty(), "Debe haber al menos un rol asignable.");

            //No debe contener Administrador idRol=1 ni Sin rol idRol=0
            boolean contieneAdmin = roles.stream().anyMatch(r -> r.getIdRol() == 1);
            boolean contieneSinRol = roles.stream().anyMatch(r -> r.getIdRol() == 0);

            assertAll("roles no asignables excluidos",
                    () -> assertFalse(contieneAdmin, "No debe incluir el rol Administrador."),
                    () -> assertFalse(contieneSinRol, "No debe incluir el rol Sin rol."));
        }

        @Test
        @DisplayName("retorna roles Tecnico, Manager y Staff como asignables")
        void retornaRolesEsperados() {
            List<Rol> roles = rolService.obtenerRolesAsignables();

            //Verificar que los roles asignables esperados estén presentes
            boolean tieneTecnico = roles.stream().anyMatch(r -> r.getIdRol() == 2);
            boolean tieneManager = roles.stream().anyMatch(r -> r.getIdRol() == 3);
            boolean tieneStaff = roles.stream().anyMatch(r -> r.getIdRol() == 4);

            assertAll("roles asignables presentes",
                    () -> assertTrue(tieneTecnico, "Debe incluir el rol Tecnico."),
                    () -> assertTrue(tieneManager, "Debe incluir el rol Manager."),
                    () -> assertTrue(tieneStaff, "Debe incluir el rol Staff."));
        }
    }


    //obtenerUsuarios

    @Nested
    @DisplayName("obtenerUsuarios")
    class ObtenerUsuarios {

        @Test
        @DisplayName("retorna lista vacía cuando no hay usuarios")
        void retornaListaVaciaSinUsuarios() {
            List<Usuario> usuarios = rolService.obtenerUsuarios();

            assertNotNull(usuarios, "La lista no debe ser null.");
            assertTrue(usuarios.isEmpty(), "La lista debe estar vacía si no hay usuarios.");
        }

        @Test
        @DisplayName("retorna todos los usuarios registrados")
        void retornaUsuariosRegistrados() {
            //Registrar usuarios directamente en el repositorio
            Usuario u1 = new Usuario();
            u1.setNombre("usuario1");
            u1.setContrasena("pass1");
            u1.setGmail("u1@test.com");
            u1.setIdRol(0);
            usuarioRepository.guardar(u1);

            Usuario u2 = new Usuario();
            u2.setNombre("usuario2");
            u2.setContrasena("pass2");
            u2.setGmail("u2@test.com");
            u2.setIdRol(0);
            usuarioRepository.guardar(u2);

            List<Usuario> usuarios = rolService.obtenerUsuarios();

            assertNotNull(usuarios);
            assertEquals(2, usuarios.size(), "Debe retornar los 2 usuarios registrados.");
        }
    }


    //obtenerNombreRol
    @Nested
    @DisplayName("obtenerNombreRol")
    class ObtenerNombreRol {

        @Test
        @DisplayName("retorna el nombre correcto para un idRol existente")
        void retornaNombreRolExistente() {

            String nombreRol = rolService.obtenerNombreRol(1);

            assertNotNull(nombreRol, "El nombre del rol no debe ser null.");
            assertEquals("Administrador", nombreRol, "El rol con idRol=1 debe ser Administrador.");
        }

        @Test
        @DisplayName("retorna el nombre correcto para Manager")
        void retornaNombreManager() {
            String nombreRol = rolService.obtenerNombreRol(3);

            assertEquals("Manager", nombreRol, "El rol con idRol=3 debe ser Manager.");
        }

        @Test
        @DisplayName("retorna Sin rol para un idRol inexistente")
        void retornaSinRolParaIdInexistente() {

            String nombreRol = rolService.obtenerNombreRol(99);

            assertEquals("Sin rol", nombreRol, "Un idRol inexistente debe retornar 'Sin rol'.");
        }
    }


    //actualizarRolGlobal
    @Nested
    @DisplayName("actualizarRolGlobal")
    class ActualizarRolGlobal {

        @Test
        @DisplayName("actualiza correctamente el rol global de un usuario")
        void actualizaRolGlobalExitosamente() {
            //Crear usuario con idRol=0 sin rol.
            Usuario u = new Usuario();
            u.setNombre("usuario_rol_global");
            u.setContrasena("pass123");
            u.setGmail("rolGlobal@test.com");
            u.setIdRol(0);
            usuarioRepository.guardar(u);


            Usuario guardado = usuarioRepository.buscarPorNombre("usuario_rol_global");
            assertNotNull(guardado, "El usuario debe existir.");
            assertEquals(0, guardado.getIdRol(), "El rol inicial debe ser 0.");


            rolService.actualizarRolGlobal(guardado.getIdUsuario(), 3);


            Usuario actualizado = usuarioRepository.buscarPorNombre("usuario_rol_global");
            assertEquals(3, actualizado.getIdRol(), "El rol debe haberse actualizado a Manager (3).");
        }

        @Test
        @DisplayName("actualiza el rol global a Administrador")
        void actualizaRolGlobalAAdministrador() {
            Usuario u = new Usuario();
            u.setNombre("usuario_a_admin");
            u.setContrasena("pass123");
            u.setGmail("aAdmin@test.com");
            u.setIdRol(0);
            usuarioRepository.guardar(u);

            Usuario guardado = usuarioRepository.buscarPorNombre("usuario_a_admin");


            rolService.actualizarRolGlobal(guardado.getIdUsuario(), 1);

            Usuario actualizado = usuarioRepository.buscarPorNombre("usuario_a_admin");
            assertEquals(1, actualizado.getIdRol(), "El rol debe haberse actualizado a Administrador (1).");
        }

        @Test
        @DisplayName("actualiza el rol global múltiples veces correctamente")
        void actualizaRolGlobalMultiplesVeces() {
            Usuario u = new Usuario();
            u.setNombre("usuario_multiples_roles");
            u.setContrasena("pass123");
            u.setGmail("multiples@test.com");
            u.setIdRol(0);
            usuarioRepository.guardar(u);

            Usuario guardado = usuarioRepository.buscarPorNombre("usuario_multiples_roles");


            rolService.actualizarRolGlobal(guardado.getIdUsuario(), 2);
            Usuario trasTecnico = usuarioRepository.buscarPorNombre("usuario_multiples_roles");
            assertEquals(2, trasTecnico.getIdRol(), "Debe ser Tecnico.");


            rolService.actualizarRolGlobal(guardado.getIdUsuario(), 4);
            Usuario trasStaff = usuarioRepository.buscarPorNombre("usuario_multiples_roles");
            assertEquals(4, trasStaff.getIdRol(), "Debe ser Staff.");
        }
    }
}