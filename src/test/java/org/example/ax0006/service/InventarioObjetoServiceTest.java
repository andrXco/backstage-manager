package org.example.ax0006.service;

import org.example.ax0006.db.H2;
import org.example.ax0006.entity.Horario;
import org.example.ax0006.repository.HorarioRepository;
import org.example.ax0006.repository.InventarioObjetoRepository;
import org.example.ax0006.repository.InventarioRepository;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventarioObjetoServiceTest {

    private H2 h2;
    private InventarioObjetoService inventarioObjetoService;
    private InventarioService inventarioService;

    @BeforeEach
    void prepararEscenario() throws Exception {
        h2 = new H2();

        try (Connection conn = h2.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
            stmt.execute("DROP ALL OBJECTS");
            stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }

        h2.inicializarDB();

        try (Connection conn = h2.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO TipoObjeto (tipo) VALUES ('TipoTest')");
            stmt.execute("INSERT INTO ReferenciaDeObjeto (referencia) VALUES ('RefTest')");
            stmt.execute("INSERT INTO Objeto (idTipoObjeto, idReferenciaObjeto) VALUES (1, 1)");
            stmt.execute("INSERT INTO DocumentoInventario (idDocumentoInventario) VALUES (1)");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        InventarioObjetoRepository inventarioObjetoRepo = new InventarioObjetoRepository(h2);
        InventarioRepository inventarioRepo = new InventarioRepository(h2);
        inventarioObjetoService = new InventarioObjetoService(inventarioObjetoRepo);
        inventarioService = new InventarioService(inventarioRepo);
    }

    @AfterAll
    static void BorrarDB() throws Exception {
        H2 h2 = new H2();
        try (Connection conn = h2.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
            stmt.execute("DROP ALL OBJECTS");
            stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }
    }

    @Test
    void guardarObjetoEnInventario() {
        Horario horario = new Horario();
        horario.setIdHorario(1);
        LocalDate fechaInc = LocalDate.parse("2026-05-01");
        LocalDate fechaFin = LocalDate.parse("2026-05-01");
        LocalTime horaInc = LocalTime.parse("20:00:00");
        LocalTime horaFin = LocalTime.parse("22:00:00");
        horario.setFechaInicio(fechaInc);
        horario.setFechaFin(fechaFin);
        horario.setHoraInicio(horaInc);
        horario.setHoraFin(horaFin);
        int resultado = inventarioObjetoService.guardarObjetoEnInventario(1, 1, horario);
        assertTrue(resultado >= 0);
    }

    @Test
    void objetoEnUsoEnRangoDetectaConflicto() {
        InventarioObjetoRepository repository = new InventarioObjetoRepository(h2);
        HorarioRepository horarioRepository = new HorarioRepository(h2);

        Horario horarioExistente = new Horario();
        horarioExistente.setFechaInicio(LocalDate.of(2026, 5, 1));
        horarioExistente.setFechaFin(LocalDate.of(2026, 5, 1));
        horarioExistente.setHoraInicio(LocalTime.of(20, 0));
        horarioExistente.setHoraFin(LocalTime.of(22, 0));

        int idHorario = horarioRepository.guardar(horarioExistente);

        try (Connection conn = h2.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO ObjetoDocumentoInventario (idInventario, idObjeto) VALUES (1, 1)");
            stmt.execute("INSERT INTO DocumentoInventarioHorario (idDocumentoInventario, idHorario) VALUES (1, "+ idHorario +")");
        } catch (SQLException e) {
            fail(e);
        }

        Horario nuevoHorario = new Horario();
        nuevoHorario.setFechaInicio(LocalDate.of(2026, 5, 1));
        nuevoHorario.setFechaFin(LocalDate.of(2026, 5, 1));
        nuevoHorario.setHoraInicio(LocalTime.of(21, 0));
        nuevoHorario.setHoraFin(LocalTime.of(23, 0));

        boolean resultado = repository.objetoEnUsoEnRango(1, nuevoHorario);

        assertTrue(resultado);
    }

    @Test
    void objetoNoEnUsoEnRango() {
        HorarioRepository horarioRepository = new HorarioRepository(h2);
        
        Horario horarioExistente = new Horario();
        horarioExistente.setFechaInicio(LocalDate.of(2026, 5, 1));
        horarioExistente.setFechaFin(LocalDate.of(2026, 5, 1));
        horarioExistente.setHoraInicio(LocalTime.of(20, 0));
        horarioExistente.setHoraFin(LocalTime.of(22, 0));

        int idHorario = horarioRepository.guardar(horarioExistente);

        try (Connection conn = h2.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO DocumentoInventarioHorario (idDocumentoInventario, idHorario) VALUES (1, " + idHorario + ")");

        } catch (SQLException e) {
            fail(e);
        }

        Horario nuevoHorario = new Horario();
        nuevoHorario.setFechaInicio(LocalDate.of(2026, 5, 1));
        nuevoHorario.setFechaFin(LocalDate.of(2026, 5, 1));
        nuevoHorario.setHoraInicio(LocalTime.of(23, 0));
        nuevoHorario.setHoraFin(LocalTime.of(23, 59));

        boolean enUso = inventarioObjetoService.objetoEnUsoEnRango(1, nuevoHorario);

        assertFalse(enUso);
    }
}