package org.example.ax0006.service;

import org.example.ax0006.db.H2;
import org.example.ax0006.entity.TipoObjeto;
import org.example.ax0006.repository.ObjetoRepository;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ObjetoServiceTest {

    private H2 h2;
    private ObjetoService objetoService;

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
            stmt.execute("INSERT INTO TIPO_OBJETO (id_tipo_objeto, nombre) VALUES (1, 'Instrumento')");
        } catch (SQLException ignored) {}

        ObjetoRepository objetoRepo = new ObjetoRepository(h2);
        objetoService = new ObjetoService(objetoRepo);
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
    void obtenerTipos() throws SQLException {
        List<TipoObjeto> tipos = objetoService.obtenerTipos();
        assertNotNull(tipos);
        assertFalse(tipos.isEmpty());
    }

    @Test
    void obtenerListaObjetosFormateada() throws SQLException {
        objetoService.crearObjetoConReferencia("REF-AAA", 1);
        objetoService.crearObjetoConReferencia("REF-BBB", 1);
        List<String> lista = objetoService.obtenerListaObjetosFormateada();
        assertNotNull(lista);
        assertTrue(lista.size() >= 2);
        assertTrue(lista.toString().contains("REF-AAA"));
        assertTrue(lista.toString().contains("REF-BBB"));
    }

    @Test
    void crearObjetoConReferencia() throws SQLException {
        objetoService.crearObjetoConReferencia("REF-123", 1);
        List<String> lista = objetoService.obtenerListaObjetosFormateada();
        assertTrue(lista.toString().contains("REF-123"));
    }
}