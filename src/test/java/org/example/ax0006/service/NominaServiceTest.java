package org.example.ax0006.service;

import org.example.ax0006.db.H2;
import org.example.ax0006.entity.*;
import org.example.ax0006.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NominaServiceTest {

    private H2 h2;
    private NominaRepository nominaRepository;
    private NominaService nominaService;
    private AsignacionStaffRepository asignacionStaffRepository;
    private ConciertoRepository conciertoRepository;
    private HorarioRepository horarioRepository;
    private ContratoRepository contratoRepository;
    private AnalisisFinancieroRepository analisisRepo;

    @BeforeEach
    void prepararEscenario() {
        h2 = new H2();
        h2.inicializarDB();
        nominaRepository = new NominaRepository(h2);
        asignacionStaffRepository = new AsignacionStaffRepository(h2);
        horarioRepository = new HorarioRepository(h2);
        contratoRepository = new ContratoRepository(h2);
        analisisRepo = new AnalisisFinancieroRepository(h2);
        conciertoRepository = new ConciertoRepository(h2, analisisRepo);
        nominaService = new NominaService(nominaRepository, conciertoRepository, asignacionStaffRepository);
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
        } finally {
            h2Final.cerrarServidor();
        }
    }

    @Test
    void generarNominasParaConciertoConStaff() {
        Horario horario = new Horario();
        horario.setFechaInicio(LocalDate.of(2026, 6, 15));
        horario.setFechaFin(LocalDate.of(2026, 6, 15));
        horario.setHoraInicio(LocalTime.of(18, 0));
        horario.setHoraFin(LocalTime.of(22, 0));
        int idHorario = horarioRepository.guardar(horario);
        assertNotEquals(-1, idHorario, "Horario guardado correctamente");

        Contrato contrato = new Contrato();
        contrato.setFecha(LocalDate.now());
        Clausula clausula = new Clausula();
        clausula.setIdClausula(1);
        clausula.setClausula("Clausula de prueba");
        contrato.setClausulas(List.of(clausula));
        ContratoService contratoService = new ContratoService(contratoRepository);
        int idContrato = contratoService.crearContrato(contrato);
        assertNotEquals(0, idContrato, "Contrato creado");

        Concierto concierto = new Concierto();
        concierto.setNombreConcierto("Concierto Test Nomina");
        concierto.setAforo(1000);
        concierto.setProgramado(true);
        concierto.setIdContrato(idContrato);
        Horario hCompleto = horarioRepository.obtenerHorarioPorId(idHorario);
        concierto.setHorario(hCompleto);
        int idConcierto = conciertoRepository.guardar(concierto, idHorario);
        assertNotEquals(0, idConcierto, "Concierto guardado");

        Usuario usuario = new Usuario();
        usuario.setNombre("StaffTest");
        usuario.setContrasena("pass123");
        usuario.setGmail("staff@test.com");
        usuario.setIdRol(4);
        UsuarioRepository usuarioRepo = new UsuarioRepository(h2);
        usuarioRepo.guardar(usuario);
        Usuario usuarioGuardado = usuarioRepo.buscarPorNombre("StaffTest");
        assertNotNull(usuarioGuardado, "Usuario guardado");

        asignacionStaffRepository.asignarStaffAConcierto(
                usuarioGuardado.getIdUsuario(),
                idConcierto,
                4,
                ""
        );
        assertTrue(asignacionStaffRepository.existeAsignacion(
                usuarioGuardado.getIdUsuario(), idConcierto, 4
        ), "Staff asignado a concierto");

        List<Usuario> staffAntes = asignacionStaffRepository.obtenerUsuariosPorConcierto(idConcierto);
        assertEquals(1, staffAntes.size(), "Staff recuperado correctamente");

        nominaService.generarNominaParaConcierto(idConcierto);

        List<Nomina> nominas = nominaService.obtenerNominasPorConcierto(idConcierto);
        assertEquals(1, nominas.size(), "Se generó 1 nómina para el staff");

        Nomina n = nominas.get(0);
        assertEquals(usuarioGuardado.getIdUsuario(), n.getIdUsuario(), "ID de usuario coincide");
        assertEquals("staff", n.getRol().toLowerCase(), "Rol es staff");
        assertEquals(4.0, n.getHorasTrabajadas(), 0.001, "Horas = 4 (18:00 a 22:00)");
        assertEquals(15000.0, n.getTarifaPorHora(), 0.001, "Tarifa staff = 15000");
        assertEquals(60000.0, n.getTotal(), 0.001, "Total = 4 * 15000 = 60000");
        assertEquals("Pendiente", n.getEstado(), "Estado inicial es Pendiente");
        assertFalse(n.isPagado(), "No está pagado inicialmente");
    }

    @Test
    void actualizarHorasExtra() {
        Horario horario = new Horario();
        horario.setFechaInicio(LocalDate.of(2026, 6, 15));
        horario.setFechaFin(LocalDate.of(2026, 6, 15));
        horario.setHoraInicio(LocalTime.of(18, 0));
        horario.setHoraFin(LocalTime.of(22, 0));
        int idHorario = horarioRepository.guardar(horario);

        Contrato contrato = new Contrato();
        contrato.setFecha(LocalDate.now());
        Clausula clausula = new Clausula();
        clausula.setIdClausula(1);
        clausula.setClausula("test");
        contrato.setClausulas(List.of(clausula));
        ContratoService cs = new ContratoService(new ContratoRepository(h2));
        int idContrato = cs.crearContrato(contrato);

        Concierto concierto = new Concierto();
        concierto.setNombreConcierto("Test Horas Extra");
        concierto.setAforo(500);
        concierto.setProgramado(true);
        concierto.setIdContrato(idContrato);
        concierto.setHorario(horarioRepository.obtenerHorarioPorId(idHorario));
        int idConcierto = conciertoRepository.guardar(concierto, idHorario);

        Usuario usuario = new Usuario();
        usuario.setNombre("StaffExtra");
        usuario.setContrasena("pass");
        usuario.setGmail("extra@test.com");
        usuario.setIdRol(4);
        UsuarioRepository ur = new UsuarioRepository(h2);
        ur.guardar(usuario);
        Usuario u = ur.buscarPorNombre("StaffExtra");

        asignacionStaffRepository.asignarStaffAConcierto(u.getIdUsuario(), idConcierto, 4, "");
        nominaService.generarNominaParaConcierto(idConcierto);

        List<Nomina> nominas = nominaService.obtenerNominasPorConcierto(idConcierto);
        assertEquals(1, nominas.size());
        Nomina n = nominas.get(0);
        int idNomina = n.getIdNomina();

        nominaService.actualizarHorasExtra(idNomina, 2.0);

        Nomina nActualizada = nominaRepository.obtenerPorId(idNomina);
        assertNotNull(nActualizada);
        assertEquals(2.0, nActualizada.getHorasExtra(), 0.001, "Horas extra guardadas");

        double totalEsperado = (4.0 * 15000) + (2.0 * 15000 * 1.5);
        assertEquals(totalEsperado, nActualizada.getTotal(), 0.001, "Total incluye HE al 150%");
    }

    @Test
    void actualizarEstado() {
        Nomina n = new Nomina(
                9999,
                1,
                "staff",
                8.0,
                10000.0,
                0.0,
                80000.0,
                "Pendiente",
                false
        );
        nominaRepository.guardar(n);
        int idNomina = n.getIdNomina();
        assertNotEquals(0, idNomina);

        nominaService.actualizarEstado(idNomina, "Aprobado");

        Nomina recuperada = nominaRepository.obtenerPorId(idNomina);
        assertEquals("Aprobado", recuperada.getEstado());
    }

    @Test
    void obtenerNominaGeneralPorEvento() {
        Horario horario = new Horario();
        horario.setFechaInicio(LocalDate.of(2026, 6, 15));
        horario.setFechaFin(LocalDate.of(2026, 6, 15));
        horario.setHoraInicio(LocalTime.of(18, 0));
        horario.setHoraFin(LocalTime.of(22, 0));
        int idHorario = horarioRepository.guardar(horario);

        Contrato contrato = new Contrato();
        contrato.setFecha(LocalDate.now());
        Clausula clausula = new Clausula();
        clausula.setIdClausula(1);
        clausula.setClausula("test");
        contrato.setClausulas(List.of(clausula));
        ContratoService cs = new ContratoService(new ContratoRepository(h2));
        int idContrato = cs.crearContrato(contrato);

        Concierto concierto = new Concierto();
        concierto.setNombreConcierto("Test Resumen");
        concierto.setAforo(500);
        concierto.setProgramado(true);
        concierto.setIdContrato(idContrato);
        concierto.setHorario(horarioRepository.obtenerHorarioPorId(idHorario));
        int idConcierto = conciertoRepository.guardar(concierto, idHorario);

        UsuarioRepository ur = new UsuarioRepository(h2);
        Usuario staff1 = new Usuario(); staff1.setNombre("S1"); staff1.setContrasena("p"); staff1.setGmail("s1@t"); staff1.setIdRol(4); ur.guardar(staff1);
        Usuario staff2 = new Usuario(); staff2.setNombre("S2"); staff2.setContrasena("p"); staff2.setGmail("s2@t"); staff2.setIdRol(4); ur.guardar(staff2);
        Usuario tecnico = new Usuario(); tecnico.setNombre("T1"); tecnico.setContrasena("p"); tecnico.setGmail("t1@t"); tecnico.setIdRol(2); ur.guardar(tecnico);

        Usuario s1 = ur.buscarPorNombre("S1");
        Usuario s2 = ur.buscarPorNombre("S2");
        Usuario t1 = ur.buscarPorNombre("T1");

        asignacionStaffRepository.asignarStaffAConcierto(s1.getIdUsuario(), idConcierto, 4, "");
        asignacionStaffRepository.asignarStaffAConcierto(s2.getIdUsuario(), idConcierto, 4, "");
        asignacionStaffRepository.asignarStaffAConcierto(t1.getIdUsuario(), idConcierto, 2, "");

        nominaService.generarNominaParaConcierto(idConcierto);

        var resumen = nominaService.obtenerNominaGeneralPorEvento(idConcierto);
        assertFalse(resumen.isEmpty(), "Hay resumen");

        double totalGeneral = nominaService.obtenerTotalGeneralEvento(idConcierto);
        double totalEsperado = (2 * 4.0 * 15000) + (1 * 4.0 * 35000);
        assertEquals(totalEsperado, totalGeneral, 0.001, "Total general coincide");
    }

    @Test
    void noGeneraNominasDuplicadas() {
        Horario horario = new Horario();
        horario.setFechaInicio(LocalDate.of(2026, 6, 15));
        horario.setFechaFin(LocalDate.of(2026, 6, 15));
        horario.setHoraInicio(LocalTime.of(18, 0));
        horario.setHoraFin(LocalTime.of(22, 0));
        int idHorario = horarioRepository.guardar(horario);

        Contrato contrato = new Contrato();
        contrato.setFecha(LocalDate.now());
        Clausula clausula = new Clausula(); clausula.setIdClausula(1); clausula.setClausula("t");
        contrato.setClausulas(List.of(clausula));
        int idContrato = new ContratoService(new ContratoRepository(h2)).crearContrato(contrato);

        Concierto concierto = new Concierto();
        concierto.setNombreConcierto("Test Duplicado");
        concierto.setAforo(100);
        concierto.setProgramado(true);
        concierto.setIdContrato(idContrato);
        concierto.setHorario(horarioRepository.obtenerHorarioPorId(idHorario));
        int idConcierto = conciertoRepository.guardar(concierto, idHorario);

        Usuario u = new Usuario(); u.setNombre("DUP"); u.setContrasena("p"); u.setGmail("d@d"); u.setIdRol(4);
        new UsuarioRepository(h2).guardar(u);
        Usuario guardado = new UsuarioRepository(h2).buscarPorNombre("DUP");

        asignacionStaffRepository.asignarStaffAConcierto(guardado.getIdUsuario(), idConcierto, 4, "");

        nominaService.generarNominaParaConcierto(idConcierto);
        nominaService.generarNominaParaConcierto(idConcierto);
        nominaService.generarNominaParaConcierto(idConcierto);

        List<Nomina> nominas = nominaService.obtenerNominasPorConcierto(idConcierto);
        assertEquals(1, nominas.size(), "Solo 1 nómina, no duplicadas");
    }
}
