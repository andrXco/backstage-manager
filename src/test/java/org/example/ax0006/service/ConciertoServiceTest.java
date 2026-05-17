package org.example.ax0006.service;

import org.example.ax0006.db.H2;
import org.example.ax0006.entity.*;
import org.example.ax0006.repository.*;
import org.example.ax0006.validator.ConciertoValidator;
import org.example.ax0006.validator.HorarioValidator;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConciertoServiceTest {

    // Gestor de BD
    private H2 h2;

    // Repositorios
    private ConciertoRepository conciertoRepo;
    private HorarioRepository horarioRepo;
    private ContratoRepository contratoRepo;
    private UsuarioRepository usuarioRepo;
    private AsignacionStaffRepository asignacionStaffRepo;

    // Servicios y Validadores
    private ContratoService contratoService;
    private ConciertoValidator conciertoValidator;
    private HorarioValidator horarioValidator;

    // Servicio Principal a Probar
    private ConciertoService conciertoService;

    @BeforeEach
    void prepararEscenario() {
        h2 = new H2();

        // 1. Inicializar BD (Ahora solo ejecuta el DDL puro de schema.sql sin datos basura)
        h2.inicializarDB();

        // 2. Inicializar repositorios reales conectados a la BD de prueba
        conciertoRepo = new ConciertoRepository(h2);
        horarioRepo = new HorarioRepository(h2);
        contratoRepo = new ContratoRepository(h2);
        usuarioRepo = new UsuarioRepository(h2);
        asignacionStaffRepo = new AsignacionStaffRepository(h2);

        // 3. Inicializar validadores
        horarioValidator = new HorarioValidator();
        conciertoValidator = new ConciertoValidator(horarioValidator);

        // 4. Inicializar servicios secundarios
        contratoService = new ContratoService(contratoRepo);

        // 5. Inicializar el servicio real que será probado
        conciertoService = new ConciertoService(conciertoRepo, horarioRepo, conciertoValidator, contratoService, asignacionStaffRepo);
    }

    @AfterEach
    void BorrarDB() {
        H2 h2Final = new H2();
        try (Connection conn = h2Final.getConnection();
             Statement stmt = conn.createStatement()) {

            // Desactiva la integridad referencial y destruye todas las tablas
            stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
            stmt.execute("DROP ALL OBJECTS");
            stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Falló la limpieza de la base de datos al final de la prueba");
        } finally {
            // Garantiza que el puerto 8082 del servidor web se libere pase lo que pase
            h2Final.cerrarServidor();
        }
    }

    @Nested
    @DisplayName("Crear Concierto")
    class CrearConciertoMETODO{
        @Test
        void crearConcierto() {
            Concierto concierto = new Concierto();
            concierto.setNombreConcierto("Tour mundial");
            concierto.setAforo(25600);

            // ==========================================
            // 1. HORARIO
            // ==========================================
            Horario horario = new Horario();
            LocalDate fechaConcierto = LocalDate.of(2026, 12, 29);
            horario.setFechaInicio(fechaConcierto);
            horario.setFechaFin(fechaConcierto.plusDays(1));
            horario.setHoraInicio(LocalTime.of(11, 0));
            horario.setHoraFin(LocalTime.of(12, 0));

            horarioRepo.guardar(horario);
            horario.setIdHorario(1); // Garantizado al estar la BD limpia
            concierto.setHorario(horario);

            // ==========================================
            // 2. ARTISTA
            // ==========================================
            Usuario usuario = new Usuario();
            usuario.setNombre("Juan Luis Guerra");
            usuario.setGmail("juanluis@guerra.com");
            usuario.setContrasena("password123");
            usuario.setIdRol(3); // Rol de Manager/Artista

            boolean artistaGuardado = usuarioRepo.guardar(usuario);
            assertTrue(artistaGuardado, "El repositorio falló al guardar al artista.");

            Usuario artistaReal = usuarioRepo.buscarPorNombre("Juan Luis Guerra");
            assertNotNull(artistaReal, "No se pudo recuperar el artista de la base de datos.");

            usuario.setIdUsuario(artistaReal.getIdUsuario());
            concierto.setArtista(usuario);

            // ==========================================
            // 3. CONTRATO
            // ==========================================
            Contrato contrato = new Contrato();
            contrato.setFecha(fechaConcierto.minusDays(10));

            Clausula clausula = new Clausula();
            clausula.setClausula("pago en efectivo");
            List<Clausula> clausulas = new ArrayList<>();
            clausulas.add(clausula);
            contrato.setClausulas(clausulas);

            int idContratoReal = contratoService.crearContrato(contrato);
            assertTrue(idContratoReal > 0, "El servicio falló al crear el contrato.");

            contrato.setIdContrato(idContratoReal);
            concierto.setContrato(contrato);
            concierto.setIdContrato(idContratoReal);

            // ==========================================
            // 4. EJECUTAR SERVICIO
            // ==========================================
            conciertoService.crearConcierto(concierto);

            List<Concierto> conciertosGuardados = conciertoService.obtenerConciertosSolos();

            // ==========================================
            // 5. SE BUSCA EL CONCIERTO EN LA DB
            // ==========================================
            Concierto conciertoRecuperado = null;
            for (Concierto c : conciertosGuardados) {
                if ("Tour mundial".equals(c.getNombreConcierto())) {
                    conciertoRecuperado = c;
                    break;
                }
            }

            // ==========================================
            // 6. ASSERTS
            // ==========================================

            assertNotNull(conciertoRecuperado, "El concierto no se persistió en la BD.");

            final Concierto res = conciertoRecuperado;
            assertAll("Integridad del Concierto Persistido",
                    () -> assertEquals("Tour mundial", res.getNombreConcierto()),
                    () -> assertEquals(25600, res.getAforo()),
                    () -> assertNotNull(res.getHorario())
            );

        }

        @Test
        void crearConciertoMalo() {
//            assertThrows();
        }

        @Test
        void aprobarConcierto() {
        }

        @Test
        void eliminarConcierto() {
        }
        @Test
        void obtenerConciertosSolos(){}
    }

    @Nested
    @DisplayName("aprobarConcierto")
    class aprobarConciertoMETODO{
        @Test
        void aprobarConcierto() {
            Concierto concierto = new Concierto();
            concierto.setNombreConcierto("Tour mundial");
            concierto.setAforo(25600);

            // ==========================================
            // 1. HORARIO
            // ==========================================
            Horario horario = new Horario();
            LocalDate fechaConcierto = LocalDate.of(2026, 12, 29);
            horario.setFechaInicio(fechaConcierto);
            horario.setFechaFin(fechaConcierto.plusDays(1));
            horario.setHoraInicio(LocalTime.of(11, 0));
            horario.setHoraFin(LocalTime.of(12, 0));

            horarioRepo.guardar(horario);
            horario.setIdHorario(1); // Garantizado al estar la BD limpia
            concierto.setHorario(horario);

            // ==========================================
            // 2. ARTISTA
            // ==========================================
            Usuario usuario = new Usuario();
            usuario.setNombre("Juan Luis Guerra");
            usuario.setGmail("juanluis@guerra.com");
            usuario.setContrasena("password123");
            usuario.setIdRol(3); // Rol de Manager/Artista

            boolean artistaGuardado = usuarioRepo.guardar(usuario);
            assertTrue(artistaGuardado, "El repositorio falló al guardar al artista.");

            Usuario artistaReal = usuarioRepo.buscarPorNombre("Juan Luis Guerra");
            assertNotNull(artistaReal, "No se pudo recuperar el artista de la base de datos.");

            usuario.setIdUsuario(artistaReal.getIdUsuario());
            concierto.setArtista(usuario);

            // ==========================================
            // 3. CONTRATO
            // ==========================================
            Contrato contrato = new Contrato();
            contrato.setFecha(fechaConcierto.minusDays(10));

            Clausula clausula = new Clausula();
            clausula.setClausula("pago en efectivo");
            List<Clausula> clausulas = new ArrayList<>();
            clausulas.add(clausula);
            contrato.setClausulas(clausulas);

            int idContratoReal = contratoService.crearContrato(contrato);
            assertTrue(idContratoReal > 0, "El servicio falló al crear el contrato.");

            contrato.setIdContrato(idContratoReal);
            concierto.setContrato(contrato);
            concierto.setIdContrato(idContratoReal);

            // ==========================================
            // 4. EJECUTAR METOODO DE CREAR CONCIERTO
            // ==========================================
            conciertoService.crearConcierto(concierto);

            List<Concierto> conciertosGuardados = conciertoService.obtenerConciertosSolos();

            // ==========================================
            // 5. SE BUSCA EL CONCIERTO
            // ==========================================
            Concierto conciertoRecuperado = null;
            for (Concierto c : conciertosGuardados) {
                if ("Tour mundial".equals(c.getNombreConcierto())) {
                    conciertoRecuperado = c;
                    break;
                }
            }

            // ==========================================
            // 6. ASSERTS ANTES DE PROGRAMAR EL CONCIERTO
            // ==========================================

            assertNotNull(conciertoRecuperado, "se pudo recuperar el concierto.");

            final Concierto res = conciertoRecuperado;
            assertAll("Integridad del Concierto Persistido",
                    () -> assertEquals("Tour mundial", res.getNombreConcierto()),
                    () -> assertEquals(25600, res.getAforo()),
                    () -> assertNotNull(res.getHorario()),
                    () -> assertFalse(res.isProgramado())
            );

            conciertoService.aprobarConcierto(res.getIdConcierto());

            // ==============================================
            // 7. RECUPERAR EL CONCIERTO DE LA BASE DE DATOS
            // ===============================================

            conciertosGuardados = conciertoService.obtenerConciertosSolos();

            // ==========================================
            // 8. SE BUSCA EL CONCIERTO
            // ==========================================
            conciertoRecuperado = null;
            for (Concierto c : conciertosGuardados) {
                if ("Tour mundial".equals(c.getNombreConcierto())) {
                    conciertoRecuperado = c;
                    break;
                }
            }

            // ==========================================
            // 9. ASSERTS CON EL CONCIERTO PROGRAMADO
            // ==========================================
            final Concierto res1 = conciertoRecuperado;

            assertNotNull(res1, "se tiene que recuperar el concierto.");

            assertAll("Integridad del Concierto Persistido",
                    () -> assertEquals("Tour mundial", res1.getNombreConcierto()),
                    () -> assertEquals(25600, res1.getAforo()),
                    () -> assertNotNull(res1.getHorario()),
                    () -> assertTrue(res1.isProgramado())
            );
        }
    }

    @Nested
    @DisplayName("ObtenerConciertosSolos")
    class ObtenerConciertosSolosMETODO {
        @Test
        void obtenerConciertos() {

        }
    }
}