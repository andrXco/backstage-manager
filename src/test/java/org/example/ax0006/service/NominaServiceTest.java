package org.example.ax0006.service;

import org.example.ax0006.db.H2;
import org.example.ax0006.entity.*;
import org.example.ax0006.repository.*;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NominaServiceTest {

    private H2 h2;
    private NominaRepository nominaRepo;
    private ConciertoRepository conciertoRepo;
    private AsignacionStaffRepository asignacionStaffRepo;
    private UsuarioRepository usuarioRepo;
    private HorarioRepository horarioRepo;
    private ContratoRepository contratoRepo;
    private NominaService nominaService;

    @BeforeEach
    void prepararEscenario() {
        h2 = new H2();
        h2.inicializarDB();
        nominaRepo = new NominaRepository(h2);
        conciertoRepo = new ConciertoRepository(h2, new AnalisisFinancieroRepository(h2));
        asignacionStaffRepo = new AsignacionStaffRepository(h2);
        usuarioRepo = new UsuarioRepository(h2);
        horarioRepo = new HorarioRepository(h2);
        contratoRepo = new ContratoRepository(h2);
        nominaService = new NominaService(nominaRepo, conciertoRepo, asignacionStaffRepo);
    }

    @AfterEach
    void limpiar() {
        try (Connection conn = h2.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
            stmt.execute("DROP ALL OBJECTS");
            stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
        } catch (Exception e) {
            fail("Falló la limpieza de la base de datos");
        } finally {
            h2.cerrarServidor();
        }
    }

    @Test
    void generarNominaConConciertoInexistenteNoLanza() {
        assertDoesNotThrow(() -> nominaService.generarNominaParaConcierto(9999));
    }

    @Test
    void flujoCompletoNominaYResumen() {
        Usuario tecnico = new Usuario();
        tecnico.setNombre("Tecnico1");
        tecnico.setGmail("tec1@test.com");
        tecnico.setContrasena("x");
        tecnico.setIdRol(4);
        usuarioRepo.guardar(tecnico);
        tecnico = usuarioRepo.buscarPorNombre("Tecnico1");

        Usuario custom = new Usuario();
        custom.setNombre("Custom1");
        custom.setGmail("custom1@test.com");
        custom.setContrasena("x");
        custom.setIdRol(4);
        usuarioRepo.guardar(custom);
        custom = usuarioRepo.buscarPorNombre("Custom1");

        Horario h = new Horario();
        h.setFechaInicio(LocalDate.of(2026, 10, 1));
        h.setFechaFin(LocalDate.of(2026, 10, 1));
        h.setHoraInicio(LocalTime.of(10, 0));
        h.setHoraFin(LocalTime.of(11, 30));
        int idHorario = horarioRepo.guardar(h);
        h.setIdHorario(idHorario);

        Contrato contrato = new Contrato();
        contrato.setFecha(LocalDate.of(2026, 9, 20));
        Clausula clausula = new Clausula();
        clausula.setClausula("clausula");
        contrato.setClausulas(List.of(clausula));
        int idContrato = new ContratoService(contratoRepo).crearContrato(contrato);

        Concierto c = new Concierto();
        c.setNombreConcierto("Nomina Test");
        c.setAforo(100);
        c.setHorario(h);
        c.setIdContrato(idContrato);
        c.setArtista(tecnico);
        int idConcierto = conciertoRepo.guardar(c, idHorario);

        asignacionStaffRepo.asignarStaffAConcierto(tecnico.getIdUsuario(), idConcierto, 4, "Sonido");
        asignacionStaffRepo.asignarStaffAConcierto(custom.getIdUsuario(), idConcierto, 4, "NoExiste");

        nominaService.generarNominaParaConcierto(idConcierto);
        nominaService.generarNominaParaConcierto(idConcierto);

        List<Nomina> nominas = nominaService.obtenerNominasPorConcierto(idConcierto);
        assertEquals(2, nominas.size());
        assertTrue(nominas.stream().anyMatch(n -> n.getRol().toLowerCase().contains("staff")));
        assertTrue(nominas.stream().anyMatch(n -> n.getTotal() > 0));

        Nomina n = nominas.get(0);
        nominaService.actualizarHorasExtra(n.getIdNomina(), 2);
        nominaService.actualizarEstado(n.getIdNomina(), "Pagado");
        nominaService.actualizarHorasYTarifa(n.getIdNomina(), 4, 20000, 1);
        nominaService.actualizarHorasExtra(123456, 1);

        double totalGeneral = nominaService.calcularTotalGeneral(idConcierto);
        assertTrue(totalGeneral > 0);
        assertEquals(totalGeneral, nominaService.obtenerTotalGeneralEvento(idConcierto));
        assertFalse(nominaService.obtenerTodas().isEmpty());
        assertFalse(nominaService.obtenerNominaGeneralPorEvento(idConcierto).isEmpty());
    }
}
