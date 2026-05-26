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

    // Gestor de BD
    private H2 h2;

    // Repositorios
    private ContratoRepository contratoRepo;

    // Servicio Principal a Probar
    private ContratoService contratoService;


    @BeforeEach
    void prepararEscenario() {
        h2 = new H2();

        // 1. Inicializar BD (Ahora solo ejecuta el DDL puro de schema.sql sin datos basura)
        h2.inicializarDB();

        // 2. Inicializar repositorios reales conectados a la BD de prueba
        contratoRepo = new ContratoRepository(h2);

        // 4. Inicializar servicios secundarios
        contratoService = new ContratoService(contratoRepo);

        // 5. Inicializar el servicio real que será probado
        contratoService = new ContratoService(contratoRepo);
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


    @Test
    void crearContrato() {
        ////////////////////////////////////////
        /// 1. Se crea el contrato
        //////////////////////////////////////////
        Contrato contrato = new Contrato();
        LocalDate fecha = LocalDate.now();
        contrato.setFecha(fecha);
            Clausula clausula = new Clausula();
            clausula.setIdClausula(1);
            clausula.setClausula("aaa");
            List<Clausula> clausulas = new ArrayList<>();
            clausulas.add(clausula);
        contrato.setClausulas(clausulas);
        ////////////////////////////////////////
        /// 2. Se guarda el contrato
        //////////////////////////////////////////
        int IdContratro = contratoService.crearContrato(contrato);
        List<Contrato> contratos = contratoService.obtenerContratos();
        Contrato contratoRecuperado = null;

        ////////////////////////////////////////
        /// 3. Se recupera el contrato de la db
        //////////////////////////////////////////
        for(Contrato c : contratos){
            if(c.getIdContrato() == IdContratro){
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
        ////////////////////////////////////////
        /// 1. Se crea el contrato
        //////////////////////////////////////////
        Contrato contrato = new Contrato();
        LocalDate fecha = LocalDate.now();
        contrato.setFecha(fecha);
        Clausula clausula = new Clausula();
        clausula.setIdClausula(1);
        clausula.setClausula("aaa");
        List<Clausula> clausulas = new ArrayList<>();
        clausulas.add(clausula);
        contrato.setClausulas(clausulas);
        ////////////////////////////////////////
        /// 2. Se guarda el contrato
        //////////////////////////////////////////
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
        ////////////////////////////////////////
        /// 1. Se crea el contrato
        //////////////////////////////////////////
        Contrato contrato = new Contrato();
        LocalDate fecha = LocalDate.now();
        contrato.setFecha(fecha);
        Clausula clausula = new Clausula();
        clausula.setIdClausula(1);
        clausula.setClausula("aaa");
        List<Clausula> clausulas = new ArrayList<>();
        clausulas.add(clausula);
        contrato.setClausulas(clausulas);
        ////////////////////////////////////////
        /// 2. Se guarda el contrato
        //////////////////////////////////////////
        int IdContratro = contratoService.crearContrato(contrato);

        ////////////////////////////////////////
        /// 1. Se crea el contrato
        //////////////////////////////////////////
        Contrato contrato1 = new Contrato();
        LocalDate fecha1 = LocalDate.now();
        contrato.setFecha(fecha);
        Clausula clausula1 = new Clausula();
        clausula.setIdClausula(2);
        clausula.setClausula("ccc");
        List<Clausula> clausulas1 = new ArrayList<>();
        clausulas.add(clausula);
        contrato.setClausulas(clausulas);
        ////////////////////////////////////////
        /// 2. Se guarda el contrato
        //////////////////////////////////////////
        int IdContratro1 = contratoService.crearContrato(contrato);

        ////////////////////////////////////////
        /// 1. Se crea el contrato
        //////////////////////////////////////////
        Contrato contrato3 = new Contrato();
        LocalDate fecha3 = LocalDate.now();
        contrato.setFecha(fecha);
        Clausula clausula3 = new Clausula();
        clausula.setIdClausula(3);
        clausula.setClausula("bbb");
        List<Clausula> clausulas3 = new ArrayList<>();
        clausulas.add(clausula);
        contrato.setClausulas(clausulas);
        ////////////////////////////////////////
        /// 2. Se guarda el contrato
        //////////////////////////////////////////

        int IdContratro3 = contratoService.crearContrato(contrato);

        //////////////////////////////////////////
        /// SE USA EL METODO
        /////////////////////////////////////////

        List<Contrato> contratos = contratoService.obtenerContratos();

        assertNotNull(contratos);

        for (Contrato c : contratos){
            assertNotNull(c.getFecha(), "la fecha");
            assertNotEquals(0, c.getIdContrato());
        }
    }

    @Test
    void obtenerClausulas() {
        ////////////////////////////////////////
        /// 1. Se crea el contrato
        //////////////////////////////////////////
        Contrato contrato = new Contrato();
        LocalDate fecha = LocalDate.now();
        contrato.setFecha(fecha);
        Clausula clausula = new Clausula();
        clausula.setIdClausula(1);
        clausula.setClausula("aaa");
        clausula.setIdClausula(2);
        clausula.setClausula("bbb");
        clausula.setIdClausula(3);
        clausula.setClausula("ccc");
        List<Clausula> clausulas = new ArrayList<>();
        clausulas.add(clausula);
        contrato.setClausulas(clausulas);
        ////////////////////////////////////////
        /// 2. Se guarda el contrato
        //////////////////////////////////////////
        int IdContratro = contratoService.crearContrato(contrato);

        //////////////////////////////////////////
        /// SE USA EL METODO
        /////////////////////////////////////////

        List<Clausula> clausulasList = contratoService.obtenerClausulas(IdContratro);

        for (int i  = 0; i < clausulasList.size(); i++) {
            switch (i){
                case 1:
                    assertEquals("aaa", clausulasList.get(i).getClausula());
                    break;
                case 2:
                    assertEquals("bbb", clausulasList.get(i).getClausula());
                    break;
                case 3:
                    assertEquals("ccc", clausulasList.get(i).getClausula());
                    break;
            }
        }

    }
    @Test
    void crearContratoNullRetornaCero() {
        // contrato null → debe retornar 0
        int resultado = contratoService.crearContrato(null);
        assertEquals(0, resultado);
    }

    @Test
    void crearContratoSinFechaRetornaCero() {
        // contrato sin fecha → debe retornar 0
        Contrato contrato = new Contrato();
        contrato.setFecha(null);
        int resultado = contratoService.crearContrato(contrato);
        assertEquals(0, resultado);
    }

    @Test
    void crearContratoSinClausulasRetornaCero() {
        // contrato sin cláusulas → debe retornar 0
        Contrato contrato = new Contrato();
        contrato.setFecha(LocalDate.now());
        contrato.setClausulas(new ArrayList<>());
        int resultado = contratoService.crearContrato(contrato);
        assertEquals(0, resultado);
    }

    @Test
    void obtenerContratoCompletoInexistenteRetornaNull() {
        // id que no existe → debe retornar null (línea 57: if contrato == null return null)
        Contrato resultado = contratoService.obtenerContratoCompleto(9999);
        assertNull(resultado);
    }
}