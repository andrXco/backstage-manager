package org.example.ax0006.service;

import org.example.ax0006.db.H2;
import org.example.ax0006.entity.Clausula;
import org.example.ax0006.entity.Contrato;
import org.example.ax0006.repository.*;
import org.example.ax0006.validator.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContratoServiceTest {

    private H2 h2;
    private ContratoRepository contratoRepo;
    private ContratoService contratoService;

    @BeforeEach
    void prepararEscenario() {
        h2 = new H2();
        h2.inicializarDB();
        contratoRepo = new ContratoRepository(h2);
        contratoService = new ContratoService(contratoRepo);
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
    void crearContrato() {
        Contrato contrato = new Contrato();
        LocalDate fecha = LocalDate.now();
        contrato.setFecha(fecha);
        Clausula clausula = new Clausula();
        clausula.setIdClausula(1);
        clausula.setClausula("aaa");
        List<Clausula> clausulas = new ArrayList<>();
        clausulas.add(clausula);
        contrato.setClausulas(clausulas);

        int IdContratro = contratoService.crearContrato(contrato);
        List<Contrato> contratos = contratoService.obtenerContratos();
        Contrato contratoRecuperado = null;

        for (Contrato c : contratos) {
            if (c.getIdContrato() == IdContratro) {
                contratoRecuperado = c;
            }
        }

        final Contrato res = contratoRecuperado;
        assertAll(
                () -> assertEquals(res.getIdContrato(), IdContratro),
                () -> assertEquals(res.getFecha(), LocalDate.now())
        );

        List<Clausula> Clausalas = contratoService.obtenerClausulas(res.getIdContrato());
        for (Clausula c : Clausalas) {
            assertEquals("aaa", c.getClausula());
        }
    }

    @Test
    void obtenerContratoCompleto() {
        Contrato contrato = new Contrato();
        contrato.setFecha(LocalDate.now());
        Clausula clausula = new Clausula();
        clausula.setIdClausula(1);
        clausula.setClausula("aaa");
        List<Clausula> clausulas = new ArrayList<>();
        clausulas.add(clausula);
        contrato.setClausulas(clausulas);

        int IdContratro = contratoService.crearContrato(contrato);
        Contrato contratoRecuperado = contratoService.obtenerContratoCompleto(IdContratro);

        final Contrato res = contratoRecuperado;
        assertAll(
                () -> assertEquals(res.getIdContrato(), IdContratro),
                () -> assertEquals(res.getFecha(), LocalDate.now())
        );

        List<Clausula> Clausalas = contratoService.obtenerClausulas(res.getIdContrato());
        for (Clausula c : Clausalas) {
            assertEquals("aaa", c.getClausula());
        }
    }

    @Test
    void obtenerContratos() {
        Contrato contrato = new Contrato();
        contrato.setFecha(LocalDate.now());
        Clausula clausula = new Clausula();
        clausula.setClausula("aaa");
        List<Clausula> clausulas = new ArrayList<>();
        clausulas.add(clausula);
        contrato.setClausulas(clausulas);
        contratoService.crearContrato(contrato);

        List<Contrato> contratos = contratoService.obtenerContratos();
        assertNotNull(contratos);
        for (Contrato c : contratos) {
            assertNotNull(c.getFecha(), "la fecha");
            assertNotEquals(0, c.getIdContrato());
        }
    }

    @Test
    void obtenerClausulas() {
        Contrato contrato = new Contrato();
        contrato.setFecha(LocalDate.now());
        Clausula clausula = new Clausula();
        clausula.setClausula("ccc");
        List<Clausula> clausulas = new ArrayList<>();
        clausulas.add(clausula);
        contrato.setClausulas(clausulas);

        int IdContratro = contratoService.crearContrato(contrato);
        List<Clausula> clausulasList = contratoService.obtenerClausulas(IdContratro);

        for (int i = 0; i < clausulasList.size(); i++) {
            switch (i) {
                case 1: assertEquals("aaa", clausulasList.get(i).getClausula()); break;
                case 2: assertEquals("bbb", clausulasList.get(i).getClausula()); break;
                case 3: assertEquals("ccc", clausulasList.get(i).getClausula()); break;
            }
        }
    }

    @Test
    void crearContratoNullRetornaCero() {
        int resultado = contratoService.crearContrato(null);
        assertEquals(0, resultado);
    }

    @Test
    void crearContratoSinFechaRetornaCero() {
        Contrato contrato = new Contrato();
        contrato.setFecha(null);
        int resultado = contratoService.crearContrato(contrato);
        assertEquals(0, resultado);
    }

    @Test
    void crearContratoSinClausulasRetornaCero() {
        Contrato contrato = new Contrato();
        contrato.setFecha(LocalDate.now());
        contrato.setClausulas(new ArrayList<>());
        int resultado = contratoService.crearContrato(contrato);
        assertEquals(0, resultado);
    }

    @Test
    void obtenerContratoCompletoInexistenteRetornaNull() {
        Contrato resultado = contratoService.obtenerContratoCompleto(9999);
        assertNull(resultado);
    }

    @Test
    void aprobarYRechazarFirmaNoLanzanExcepcion() {
        assertDoesNotThrow(() -> contratoService.aprobarFirma(1));
        assertDoesNotThrow(() -> contratoService.rechazarFirma(1));
    }
}
