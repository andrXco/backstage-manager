package org.example.ax0006.service;

import org.example.ax0006.db.H2;
import org.example.ax0006.entity.Objeto;
import org.example.ax0006.repository.InventarioRepository;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventarioServiceTest {

    private H2 h2;
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
            stmt.execute("INSERT INTO Horario (fechaInc, fechaFin, horaInc, horaFin) VALUES ('2026-05-01', '2026-05-01', '20:00:00', '01:00:00')");
            stmt.execute("INSERT INTO Concierto (nombreConcierto, idHorario, aforo, programado) VALUES ('Test', 1, 100, FALSE)");
            stmt.execute("INSERT INTO TipoObjeto (tipo) VALUES ('TipoTest')");
            stmt.execute("INSERT INTO ReferenciaDeObjeto (referencia) VALUES ('RefTest')");
            stmt.execute("INSERT INTO Objeto (idTipoObjeto, idReferenciaObjeto) VALUES (1, 1)");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        InventarioRepository inventarioRepo = new InventarioRepository(h2);
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
    void crearDocumentoInventario() {

        List<Integer> Objetos = new ArrayList<>();
        Objetos.add(1);
        int idInventario = inventarioService.crearDocumentoInventario(1, 1, Objetos);

        assertTrue(idInventario > 0);
    }

    @Test
    void obtenerObjetosPorConcierto() {
        List<Integer> Objetos = new ArrayList<>();
        Objetos.add(1);

        inventarioService.crearDocumentoInventario(1, 1, Objetos);

        List<String> Objetos1 = inventarioService.obtenerObjetosPorConcierto(1);

        assertNotNull(Objetos1);
        assertFalse(Objetos1.isEmpty());
    }

    @Test
    void eliminarDocumentoInventario() {
        List<Integer> Objetos = new ArrayList<>();
        Objetos.add(1);

        int idInventario = inventarioService.crearDocumentoInventario(1, 1, Objetos);

        inventarioService.eliminarDocumentoInventario(idInventario, 1, 1, Objetos);

        List<String> Objetos1 = inventarioService.obtenerObjetosPorConcierto(1);
        assertTrue(Objetos1.isEmpty());
    }

    @Test
    void obtenerDocumentoInventarioPorConcierto() {
        List<Integer> Objetos = new ArrayList<>();
        Objetos.add(1);

        inventarioService.crearDocumentoInventario(1, 1, Objetos);

        int idInventario = inventarioService.obtenerDocumentoInventarioPorConcierto(1);

        assertTrue(idInventario > 0);
    }

    @Test
    void obtenerObjetoObjetosPorConcierto() {
        List<Integer> Objetos = new ArrayList<>();
        Objetos.add(1);
        inventarioService.crearDocumentoInventario(1, 1, Objetos);

        List<Objeto> objetos = inventarioService.obtenerObjetoObjetosPorConcierto(1);

        assertNotNull(objetos);
        assertFalse(objetos.isEmpty());
    }

    @Test
    void obtenerObjetosPorInventario() {
        List<Integer> Objetos = new ArrayList<>();
        Objetos.add(1);
        int idInventario = inventarioService.crearDocumentoInventario(1, 1, Objetos);

        List<Objeto> objetos = inventarioService.obtenerObjetosPorInventario(idInventario);

        assertNotNull(objetos);
        assertFalse(objetos.isEmpty());
    }

    @Test
    void obtenerInventariosSinConcierto() {
        List<Integer> inventarios = inventarioService.obtenerInventariosSinConcierto();

        assertNotNull(inventarios);
    }

    @Test
    void obtenerIdHorarioPorInventario() {
        List<Integer> Objetos = new ArrayList<>();
        Objetos.add(1);
        int idInventario = inventarioService.crearDocumentoInventario(1, 1, Objetos);

        int idHorario = inventarioService.obtenerIdHorarioPorInventario(idInventario);

        assertEquals(1, idHorario);
    }

    @Test
    void eliminarHorarioInventario() {
        List<Integer> Objetos = new ArrayList<>();
        Objetos.add(1);
        int idInventario = inventarioService.crearDocumentoInventario(1, 1, Objetos);

        inventarioService.EliminarHorarioInventario(1);

        int idHorario = inventarioService.obtenerIdHorarioPorInventario(idInventario);
        assertEquals(-1, idHorario);
    }
}