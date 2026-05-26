package org.example.ax0006.service;

import org.example.ax0006.db.H2;
import org.example.ax0006.entity.Usuario;
import org.example.ax0006.repository.AsignacionStaffRepository;
import org.example.ax0006.repository.ConciertoRepository;
import org.example.ax0006.repository.UsuarioRepository;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StaffServiceTest {

    private H2 h2;
    private UsuarioRepository usuarioRepo;
    private AsignacionStaffRepository asignacionStaffRepo;
    private ConciertoRepository conciertoRepo;
    private StaffService staffService;

    @BeforeEach
    void prepararEscenario() {
        h2 = new H2();
        h2.inicializarDB();

        usuarioRepo = new UsuarioRepository(h2);
        asignacionStaffRepo = new AsignacionStaffRepository(h2);
        conciertoRepo = new ConciertoRepository(h2, null);

        staffService = new StaffService(usuarioRepo, asignacionStaffRepo, conciertoRepo);
    }

    @AfterEach
    void limpiarBaseDeDatos() {
        try (Connection conn = h2.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
            stmt.execute("DROP ALL OBJECTS");
            stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nested
    @DisplayName("Crear Empleado")
    class CrearEmpleadoMETODO {

        @Test
        void crearEmpleadoExitoso() {
            boolean resultado = staffService.crearEmpleado("JuanPerez", "password123", "juanperez@test.com");
            assertTrue(resultado);
        }

        @Test
        void crearEmpleadoNombreDuplicado() {
            staffService.crearEmpleado("Carlos", "pass123", "carlos@test.com");
            boolean resultado = staffService.crearEmpleado("Carlos", "pass456", "otro@test.com");
            assertFalse(resultado);
        }
    }

    @Nested
    @DisplayName("Listar Empleados")
    class ListarEmpleadosMETODO {

        @Test
        void listarEmpleados() {
            staffService.crearEmpleado("Empleado1", "pass1", "e1@test.com");
            staffService.crearEmpleado("Empleado2", "pass2", "e2@test.com");

            List<Usuario> empleados = staffService.listarEmpleados();
            assertTrue(empleados.size() >= 2);
        }
    }

    @Nested
    @DisplayName("Asignar Staff a Concierto")
    class AsignarStaffMETODO {

        @Test
        void asignarStaffAConciertoExitoso() {
            staffService.crearEmpleado("Staff1", "pass", "staff1@test.com");
            Usuario staff = usuarioRepo.buscarPorNombre("Staff1");

            boolean asignado = staffService.asignarStaffAConcierto(staff.getIdUsuario(), 0, 4, "Sonido"); // idConcierto = 0
            assertTrue(asignado);
        }
    }

    @Nested
    @DisplayName("Obtener Subrol")
    class ObtenerSubrolMETODO {

        @Test
        void obtenerSubrolStaffEnConcierto() {
            staffService.crearEmpleado("StaffSubrol", "pass", "subrol@test.com");
            Usuario staff = usuarioRepo.buscarPorNombre("StaffSubrol");

            staffService.asignarStaffAConcierto(staff.getIdUsuario(), 0, 4, "Luces"); // idConcierto = 0

            String subrol = staffService.obtenerSubrolStaffEnConcierto(staff.getIdUsuario(), 0);
            assertNotEquals("Sin subrol", subrol);
        }
    }

    @Nested
    @DisplayName("Actualizar Subrol")
    class ActualizarSubrolMETODO {

        @Test
        void actualizarSubrolStaffEnConcierto() {
            staffService.crearEmpleado("StaffActualizar", "pass", "actualizar@test.com");
            Usuario staff = usuarioRepo.buscarPorNombre("StaffActualizar");

            staffService.asignarStaffAConcierto(staff.getIdUsuario(), 0, 4, "Sonido"); // idConcierto = 0

            boolean actualizado = staffService.actualizarSubrolStaffEnConcierto(staff.getIdUsuario(), 0, "Seguridad");
            assertTrue(actualizado);
        }
    }
    @Nested
    @DisplayName("Eliminar Asignacion")
    class EliminarAsignacionMETODO {

        @Test
        void eliminarAsignacion() {
            staffService.crearEmpleado("StaffEliminar", "pass", "eliminar@test.com");
            Usuario staff = usuarioRepo.buscarPorNombre("StaffEliminar");
            staffService.asignarStaffAConcierto(staff.getIdUsuario(), 0, 4, "Sonido");

            List<Usuario> antes = staffService.obtenerStaffPorConcierto(0);
            assertTrue(antes.stream().anyMatch(u -> u.getIdUsuario() == staff.getIdUsuario()));

            assertDoesNotThrow(() ->
                    staffService.eliminarAsignacion(staff.getIdUsuario(), 0, 4)
            );
        }
    }

    // ← BORRA DESDE AQUÍ: el segundo bloque @Nested que empieza en línea 146
    // @Nested
    // @DisplayName("Eliminar Asignacion")
    // class EliminarAsignacionMETODO { ... }
    // ← HASTA AQUÍ (líneas 146-161 aproximadamente)


    @Nested
    @DisplayName("Generar Nomina")
    class GenerarNominaMETODO {

        @Test
        void generarNomina() {
            double nomina = staffService.generarNomina(1);
            assertEquals(0.0, nomina);
        }
    }

    @Nested
    @DisplayName("Obtener Ids Usuarios Asignados")
    class ObtenerIdsUsuariosAsignadosMETODO {

        @Test
        void obtenerIdsUsuariosAsignados() {
            List<Integer> ids = staffService.obtenerIdsUsuariosAsignados();
            assertNotNull(ids);
        }
    }

    @Nested
    @DisplayName("Obtener Usuarios Por Concierto")
    class ObtenerUsuariosPorConciertoMETODO {

        @Test
        void obtenerUsuariosPorConcierto() {
            List<Usuario> usuarios = staffService.obtenerUsuariosPorConcierto(0);
            assertNotNull(usuarios);
        }
    }

    @Nested
    @DisplayName("Obtener Nombre Rol En Concierto")
    class ObtenerNombreRolEnConciertoMETODO {

        @Test
        void obtenerNombreRolEnConcierto() {
            staffService.crearEmpleado("StaffRol", "pass", "rol@test.com");
            Usuario staff = usuarioRepo.buscarPorNombre("StaffRol");
            staffService.asignarStaffAConcierto(staff.getIdUsuario(), 0, 4, "Luces");

            String nombreRol = staffService.obtenerNombreRolEnConcierto(staff.getIdUsuario(), 0);
            assertNotNull(nombreRol);
        }
    }

    @Nested
    @DisplayName("Obtener Subroles Disponibles")
    class ObtenerSubrolesDisponiblesMETODO {

        @Test
        void obtenerSubrolesDisponibles() {
            List<String> subroles = staffService.obtenerSubrolesDisponibles();
            assertNotNull(subroles);
        }
    }

    @Nested
    @DisplayName("Obtener Id Concierto Del Usuario")
    class ObtenerIdConciertoDelUsuarioMETODO {

        @Test
        void obtenerIdConciertoDelUsuario() {
            staffService.crearEmpleado("StaffConcierto", "pass", "concierto@test.com");
            Usuario staff = usuarioRepo.buscarPorNombre("StaffConcierto");
            staffService.asignarStaffAConcierto(staff.getIdUsuario(), 0, 4, "Audio");

            int idConcierto = staffService.obtenerIdConciertoDelUsuario(staff.getIdUsuario());
            assertEquals(0, idConcierto);
        }
    }

    @Nested
    @DisplayName("Actualizar Subrol invalido")
    class ActualizarSubrolInvalidoMETODO {

        @Test
        void actualizarSubrolNullRetornaFalse() {
            assertFalse(staffService.actualizarSubrolStaffEnConcierto(1, 0, null));
        }

        @Test
        void actualizarSubrolVacioRetornaFalse() {
            assertFalse(staffService.actualizarSubrolStaffEnConcierto(1, 0, "  "));
        }
    }
}