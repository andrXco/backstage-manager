package org.example.ax0006.service;

import org.example.ax0006.db.H2;
import org.example.ax0006.entity.AnalisisFinanciero;
import org.example.ax0006.entity.Boleteria;
import org.example.ax0006.repository.AnalisisFinancieroRepository;
import org.example.ax0006.repository.BoleteriaRepository;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoleteriaServiceTest {

    // =========================
    // ATRIBUTOS
    // =========================
    private H2 h2;
    private BoleteriaRepository boleteriaRepo;
    private BoleteriaService boleteriaService;
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
        boleteriaRepo = new BoleteriaRepository(h2);
        boleteriaService = new BoleteriaService(boleteriaRepo);

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
    // AGREGAR BOLETERIA
    // =========================
    @Nested
    @DisplayName("Agregar Boletería")
    class AgregarBoleteria {

        @Test
        void agregarBoleteria_datosValidos_seGuardaEnBD() {

            int id = boleteriaService.agregarBoleteria(
                    "VIP", 100, 50000, idAnalisisF
            );

            assertTrue(id > 0, "El id generado debe ser mayor a 0");

            List<Boleteria> lista =
                    boleteriaService.listarBoleteria(idAnalisisF);

            assertEquals(1, lista.size());
            assertEquals("VIP", lista.get(0).getSeccion());
            assertEquals(100, lista.get(0).getCantidad());
            assertEquals(50000, lista.get(0).getPrecioBoleta());
            assertEquals(5000000, lista.get(0).getIngresoTotal());
        }

        @Test
        void agregarBoleteria_seccionNula_noSeGuarda() {

            int id = boleteriaService.agregarBoleteria(
                    null, 100, 50000, idAnalisisF
            );

            assertEquals(0, id);
            assertTrue(
                    boleteriaService.listarBoleteria(idAnalisisF).isEmpty()
            );
        }

        @Test
        void agregarBoleteria_seccionVacia_noSeGuarda() {

            int id = boleteriaService.agregarBoleteria(
                    "   ", 100, 50000, idAnalisisF
            );

            assertEquals(0, id);
            assertTrue(
                    boleteriaService.listarBoleteria(idAnalisisF).isEmpty()
            );
        }

        @Test
        void agregarBoleteria_cantidadCero_noSeGuarda() {

            int id = boleteriaService.agregarBoleteria(
                    "General", 0, 50000, idAnalisisF
            );

            assertEquals(0, id);
            assertTrue(
                    boleteriaService.listarBoleteria(idAnalisisF).isEmpty()
            );
        }

        @Test
        void agregarBoleteria_cantidadNegativa_noSeGuarda() {

            int id = boleteriaService.agregarBoleteria(
                    "General", -10, 50000, idAnalisisF
            );

            assertEquals(0, id);
            assertTrue(
                    boleteriaService.listarBoleteria(idAnalisisF).isEmpty()
            );
        }

        @Test
        void agregarBoleteria_precioNegativo_noSeGuarda() {

            int id = boleteriaService.agregarBoleteria(
                    "General", 100, -1000, idAnalisisF
            );

            assertEquals(0, id);
            assertTrue(
                    boleteriaService.listarBoleteria(idAnalisisF).isEmpty()
            );
        }

        @Test
        void agregarBoleteria_ingresoTotalCalculadoCorrectamente() {

            boleteriaService.agregarBoleteria(
                    "Palco", 200, 75000, idAnalisisF
            );

            List<Boleteria> lista =
                    boleteriaService.listarBoleteria(idAnalisisF);

            assertEquals(1, lista.size());
            assertEquals(
                    200 * 75000,
                    lista.get(0).getIngresoTotal()
            );
        }
    }

    // =========================
    // ELIMINAR BOLETERIA
    // =========================
    @Nested
    @DisplayName("Eliminar Boletería")
    class EliminarBoleteria {

        @Test
        void eliminarBoleteria_existente_noExisteEnBD() {

            int id = boleteriaService.agregarBoleteria(
                    "General", 50, 30000, idAnalisisF
            );

            assertTrue(id > 0);

            boleteriaService.eliminarBoleteria(id);

            List<Boleteria> lista =
                    boleteriaService.listarBoleteria(idAnalisisF);

            assertTrue(lista.isEmpty(), "La boletería debe haberse eliminado");
        }
    }

    // =========================
    // LISTAR BOLETERIA
    // =========================
    @Nested
    @DisplayName("Listar Boletería")
    class ListarBoleteria {

        @Test
        void listarBoleteria_variosAgregados_retornaTodos() {

            boleteriaService.agregarBoleteria(
                    "VIP", 100, 50000, idAnalisisF
            );
            boleteriaService.agregarBoleteria(
                    "General", 200, 20000, idAnalisisF
            );
            boleteriaService.agregarBoleteria(
                    "Palco", 50, 100000, idAnalisisF
            );

            List<Boleteria> lista =
                    boleteriaService.listarBoleteria(idAnalisisF);

            assertEquals(3, lista.size());
        }

        @Test
        void listarBoleteria_sinDatos_retornaVacia() {

            List<Boleteria> lista =
                    boleteriaService.listarBoleteria(idAnalisisF);

            assertTrue(lista.isEmpty());
        }
    }

    // =========================
    // TOTAL BOLETERIA
    // =========================
    @Nested
    @DisplayName("Total Boletería")
    class TotalBoleteria {

        @Test
        void obtenerTotalBoleteria_variosRegistros_retornaSuma() {

            boleteriaService.agregarBoleteria(
                    "VIP", 100, 50000, idAnalisisF
            );
            boleteriaService.agregarBoleteria(
                    "General", 200, 20000, idAnalisisF
            );

            int total = boleteriaService.obtenerTotalBoleteria(idAnalisisF);

            assertEquals(
                    (100 * 50000) + (200 * 20000),
                    total
            );
        }

        @Test
        void obtenerTotalBoleteria_sinRegistros_retornaCero() {

            int total = boleteriaService.obtenerTotalBoleteria(idAnalisisF);

            assertEquals(0, total);
        }
    }
}