package org.example.ax0006.service;

import org.example.ax0006.db.H2;
import org.example.ax0006.entity.*;
import org.example.ax0006.dto.ReporteDashboardDTO;
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

class ReporteServiceTest {

    private H2 h2;
    private ConciertoRepository conciertoRepo;
    private HorarioRepository horarioRepo;
    private ContratoRepository contratoRepo;
    private InventarioRepository inventarioRepo;
    private AsignacionStaffRepository asignacionStaffRepo;
    private UsuarioRepository usuarioRepo;
    private AnalisisFinancieroRepository analisisRepo;
    private GastoRepository gastoRepo;
    private IngresoRepository ingresoRepo;
    private BoleteriaRepository boleteriaRepo;
    private ReporteRepository reporteRepo;
    private NominaRepository nominaRepo;

    private ContratoService contratoService;
    private InventarioService inventarioService;
    private ConciertoService conciertoService;
    private GastoService gastoService;
    private IngresoService ingresoService;
    private BoleteriaService boleteriaService;
    private AnalisisFinancieroService analisisService;
    private NominaService nominaService;

    private ReporteService reporteService;

    @BeforeEach
    void prepararEscenario() {
        h2 = new H2();
        h2.inicializarDB();

        analisisRepo = new AnalisisFinancieroRepository(h2);
        conciertoRepo = new ConciertoRepository(h2, analisisRepo);
        horarioRepo = new HorarioRepository(h2);
        contratoRepo = new ContratoRepository(h2);
        usuarioRepo = new UsuarioRepository(h2);
        inventarioRepo = new InventarioRepository(h2);
        asignacionStaffRepo = new AsignacionStaffRepository(h2);
        gastoRepo = new GastoRepository(h2);
        ingresoRepo = new IngresoRepository(h2);
        boleteriaRepo = new BoleteriaRepository(h2);
        reporteRepo = new ReporteRepository(h2);
        nominaRepo = new NominaRepository(h2);

        contratoService = new ContratoService(contratoRepo);
        inventarioService = new InventarioService(inventarioRepo);
        gastoService = new GastoService(gastoRepo);
        ingresoService = new IngresoService(ingresoRepo);
        boleteriaService = new BoleteriaService(boleteriaRepo);
        analisisService = new AnalisisFinancieroService(analisisRepo);
        nominaService = new NominaService(nominaRepo, conciertoRepo, asignacionStaffRepo);

        HorarioValidator hv = new HorarioValidator();
        ConciertoValidator cv = new ConciertoValidator(hv);
        conciertoService = new ConciertoService(conciertoRepo, inventarioService, horarioRepo, cv, contratoService, asignacionStaffRepo);

        reporteService = new ReporteService(conciertoService, ingresoService, boleteriaService, gastoService, nominaService, reporteRepo);
    }

    @AfterEach
    void BorrarDB() {
        try (Connection conn = h2.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
            stmt.execute("DROP ALL OBJECTS");
            stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Falló la limpieza de la base de datos");
        } finally {
            h2.cerrarServidor();
        }
    }

    @Test
    void testGenerarDashboardYReporte() {
        int ingresosBase = reporteService.generarDashboard().getIngresosTotales();

        // 1. Crear Artista
        Usuario artista = new Usuario();
        artista.setNombre("Artista Test");
        artista.setGmail("test@artista.com");
        artista.setContrasena("pass");
        artista.setIdRol(3);
        usuarioRepo.guardar(artista);
        artista = usuarioRepo.buscarPorNombre("Artista Test");

        // 2. Crear Horario
        Horario horario = new Horario();
        horario.setFechaInicio(LocalDate.of(2026, 12, 1));
        horario.setFechaFin(LocalDate.of(2026, 12, 1));
        horario.setHoraInicio(LocalTime.of(20, 0));
        horario.setHoraFin(LocalTime.of(23, 0));
        int idHorario = horarioRepo.guardar(horario);
        horario.setIdHorario(idHorario);

        // 3. Crear Contrato
        Contrato contrato = new Contrato();
        contrato.setFecha(LocalDate.of(2026, 11, 1));
        Clausula clausula = new Clausula();
        clausula.setClausula("Pago en dolares");
        List<Clausula> clausulas = new ArrayList<>();
        clausulas.add(clausula);
        contrato.setClausulas(clausulas);
        
        int idContrato = contratoService.crearContrato(contrato);
        contrato.setIdContrato(idContrato);

        // 4. Crear Concierto
        Concierto concierto = new Concierto();
        concierto.setNombreConcierto("Concierto Test");
        concierto.setAforo(5000);
        concierto.setHorario(horario);
        concierto.setArtista(artista);
        concierto.setContrato(contrato);
        concierto.setIdContrato(idContrato);
        conciertoService.crearConcierto(concierto);

        // Recuperar concierto con ID
        List<Concierto> list = conciertoService.obtenerConciertosSolos();
        Concierto conciertoGuardado = list.stream()
                .filter(c -> c.getNombreConcierto().equals("Concierto Test"))
                .findFirst().orElseThrow();

        // 5. Crear Analisis Financiero
        int idAnalisis = analisisService.crearPresupuesto(10000);
        conciertoService.asignarPresupuesto(conciertoGuardado.getIdConcierto(), idAnalisis);

        // Volver a cargar para que tenga analisis asociado
        list = conciertoService.obtenerConciertosSolos();
        conciertoGuardado = list.stream()
                .filter(c -> c.getNombreConcierto().equals("Concierto Test"))
                .findFirst().orElseThrow();

        // 6. Agregar Finanzas
        ingresoService.agregarIngreso("Patrocinio", 20000, idAnalisis);
        gastoService.agregarGasto("Luces", 5000, idAnalisis);
        boleteriaService.agregarBoleteria("VIP", 100, 50, idAnalisis); // 100 * 50 = 5000

        // 7. Test KPIs / Dashboard
        ReporteDashboardDTO dashboard = reporteService.generarDashboard();
        int incrementoIngresos = dashboard.getIngresosTotales() - ingresosBase;
        assertEquals(25000, incrementoIngresos, "El incremento de ingresos debe ser 20000 + 5000 = 25000");
        assertEquals("Artista Test", dashboard.getArtistaMasRentable());

        // 8. Test Rendimiento String
        String rendimiento = reporteService.obtenerRendimientoConcierto(conciertoGuardado);
        assertTrue(rendimiento.contains("Concierto Test"));
        assertTrue(rendimiento.contains("Artista Test"));
        assertTrue(rendimiento.contains("25.000")); // format currency check
        assertTrue(rendimiento.contains("5.000")); // expenses check

        // 9. Test Generar Reporte
        String reporteStr = reporteService.generarYGuardarReporte(conciertoGuardado, "Manager Test");
        System.out.println("DEBUG - REPORTE STR IS: " + reporteStr);
        assertTrue(reporteStr.contains("REPORTE GERENCIAL OFICIAL"));
        assertTrue(reporteStr.contains("Manager Test"));

        // 10. Test Historial
        String historial = reporteService.obtenerHistorialReportes();
        assertTrue(historial.contains("Concierto Test"));
        assertEquals(1, reporteRepo.obtenerTotalReportes());

        // 11. Test Artistas Rentabilidad
        String rentabilidad = reporteService.obtenerArtistasRentabilidad();
        assertTrue(rentabilidad.contains("Artista Test"));
        assertTrue(rentabilidad.contains("20.000")); // Net profit: 25000 - 5000 = 20000
    }
}
