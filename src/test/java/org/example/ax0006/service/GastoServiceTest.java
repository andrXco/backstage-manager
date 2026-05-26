package org.example.ax0006.service;

import org.example.ax0006.db.H2;
import org.example.ax0006.entity.AnalisisFinanciero;
import org.example.ax0006.entity.Gasto;
import org.example.ax0006.repository.AnalisisFinancieroRepository;
import org.example.ax0006.repository.GastoRepository;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GastoServiceTest {

    // =========================
    // ATRIBUTOS
    // =========================
    private H2 h2;
    private GastoRepository gastoRepo;
    private GastoService gastoService;
    private AnalisisFinancieroRepository analisisRepo;
    private int idAnalisisF;

    // =========================
    // SETUP
    // =========================
    @BeforeEach
    void prepararEscenario() {

        h2 = new H2();
        h2.inicializarDB();

        analisisRepo = new AnalisisFinancieroRepository(h2);
        gastoRepo = new GastoRepository(h2);
        gastoService = new GastoService(gastoRepo);

        // CREAR ANALISIS BASE para la FK
        AnalisisFinanciero af = new AnalisisFinanciero();
        af.setPresupuesto(10000);
        af.setAprobado(false);
        idAnalisisF = analisisRepo.guardar(af);
    }

    // =========================
    // TEARDOWN
    // =========================
    @AfterEach
    void borrarDB() {
        H2 h2Final = new H2();
        try (
                Connection conn = h2Final.getConnection();
                Statement stmt = conn.createStatement()
        ) {
            stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
            stmt.execute("DROP ALL OBJECTS");
            stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Falló la limpieza de la base de datos.");
        } finally {
            h2Final.cerrarServidor();
        }
    }

    // =========================
    // AGREGAR GASTO
    // =========================
    @Nested
    @DisplayName("Agregar Gasto")
    class AgregarGasto {

        @Test
        void agregarGasto_datosValidos_seGuardaEnBD() {

            int id = gastoService.agregarGasto(
                    "Sonido", 300000, idAnalisisF
            );

            assertTrue(id > 0, "El id generado debe ser mayor a 0");

            List<Gasto> lista =
                    gastoService.listarGastos(idAnalisisF);

            assertEquals(1, lista.size());
            assertEquals("Sonido", lista.get(0).getDescripcion());
            assertEquals(300000, lista.get(0).getValor());
        }

        @Test
        void agregarGasto_descripcionNula_noSeGuarda() {

            int id = gastoService.agregarGasto(
                    null, 300000, idAnalisisF
            );

            assertEquals(0, id);
            assertTrue(
                    gastoService.listarGastos(idAnalisisF).isEmpty()
            );
        }

        @Test
        void agregarGasto_descripcionVacia_noSeGuarda() {

            int id = gastoService.agregarGasto(
                    "   ", 300000, idAnalisisF
            );

            assertEquals(0, id);
            assertTrue(
                    gastoService.listarGastos(idAnalisisF).isEmpty()
            );
        }

        @Test
        void agregarGasto_valorCero_noSeGuarda() {

            int id = gastoService.agregarGasto(
                    "Luces", 0, idAnalisisF
            );

            assertEquals(0, id);
            assertTrue(
                    gastoService.listarGastos(idAnalisisF).isEmpty()
            );
        }

        @Test
        void agregarGasto_valorNegativo_noSeGuarda() {

            int id = gastoService.agregarGasto(
                    "Luces", -5000, idAnalisisF
            );

            assertEquals(0, id);
            assertTrue(
                    gastoService.listarGastos(idAnalisisF).isEmpty()
            );
        }
    }

    // =========================
    // ELIMINAR GASTO
    // =========================
    @Nested
    @DisplayName("Eliminar Gasto")
    class EliminarGasto {

        @Test
        void eliminarGasto_existente_noExisteEnBD() {

            int id = gastoService.agregarGasto(
                    "Transporte", 150000, idAnalisisF
            );

            assertTrue(id > 0);

            gastoService.eliminarGasto(id);

            List<Gasto> lista =
                    gastoService.listarGastos(idAnalisisF);

            assertTrue(lista.isEmpty(), "El gasto debe haberse eliminado");
        }
    }

    // =========================
    // LISTAR GASTOS
    // =========================
    @Nested
    @DisplayName("Listar Gastos")
    class ListarGastos {

        @Test
        void listarGastos_variosAgregados_retornaTodos() {

            gastoService.agregarGasto(
                    "Sonido", 300000, idAnalisisF
            );
            gastoService.agregarGasto(
                    "Luces", 200000, idAnalisisF
            );
            gastoService.agregarGasto(
                    "Transporte", 150000, idAnalisisF
            );

            List<Gasto> lista =
                    gastoService.listarGastos(idAnalisisF);

            assertEquals(3, lista.size());
        }

        @Test
        void listarGastos_sinDatos_retornaVacia() {

            List<Gasto> lista =
                    gastoService.listarGastos(idAnalisisF);

            assertTrue(lista.isEmpty());
        }
    }

    // =========================
    // TOTAL GASTOS
    // =========================
    @Nested
    @DisplayName("Total Gastos")
    class TotalGastos {

        @Test
        void obtenerTotalGastos_variosRegistros_retornaSuma() {

            gastoService.agregarGasto(
                    "Sonido", 300000, idAnalisisF
            );
            gastoService.agregarGasto(
                    "Luces", 200000, idAnalisisF
            );

            int total = gastoService.obtenerTotalGastos(idAnalisisF);

            assertEquals(500000, total);
        }

        @Test
        void obtenerTotalGastos_sinRegistros_retornaCero() {

            int total = gastoService.obtenerTotalGastos(idAnalisisF);

            assertEquals(0, total);
        }
    }
}