package org.example.ax0006.service;

import org.example.ax0006.db.H2;
import org.example.ax0006.entity.AnalisisFinanciero;
import org.example.ax0006.entity.Ingreso;
import org.example.ax0006.repository.AnalisisFinancieroRepository;
import org.example.ax0006.repository.IngresoRepository;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IngresoServiceTest {

    // =========================
    // ATRIBUTOS
    // =========================
    private H2 h2;
    private IngresoRepository ingresoRepo;
    private IngresoService ingresoService;
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
        ingresoRepo = new IngresoRepository(h2);
        ingresoService = new IngresoService(ingresoRepo);

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
    // AGREGAR INGRESO
    // =========================
    @Nested
    @DisplayName("Agregar Ingreso")
    class AgregarIngreso {

        @Test
        void agregarIngreso_datosValidos_seGuardaEnBD() {

            int id = ingresoService.agregarIngreso(
                    "Patrocinio Nike", 500000, idAnalisisF
            );

            assertTrue(id > 0, "El id generado debe ser mayor a 0");

            List<Ingreso> lista =
                    ingresoService.listarIngresos(idAnalisisF);

            assertEquals(1, lista.size());
            assertEquals("Patrocinio Nike", lista.get(0).getDescripcion());
            assertEquals(500000, lista.get(0).getValor());
        }

        @Test
        void agregarIngreso_descripcionNula_noSeGuarda() {

            int id = ingresoService.agregarIngreso(
                    null, 500000, idAnalisisF
            );

            assertEquals(0, id);
            assertTrue(
                    ingresoService.listarIngresos(idAnalisisF).isEmpty()
            );
        }

        @Test
        void agregarIngreso_descripcionVacia_noSeGuarda() {

            int id = ingresoService.agregarIngreso(
                    "   ", 500000, idAnalisisF
            );

            assertEquals(0, id);
            assertTrue(
                    ingresoService.listarIngresos(idAnalisisF).isEmpty()
            );
        }

        @Test
        void agregarIngreso_valorCero_noSeGuarda() {

            int id = ingresoService.agregarIngreso(
                    "Patrocinio", 0, idAnalisisF
            );

            assertEquals(0, id);
            assertTrue(
                    ingresoService.listarIngresos(idAnalisisF).isEmpty()
            );
        }

        @Test
        void agregarIngreso_valorNegativo_noSeGuarda() {

            int id = ingresoService.agregarIngreso(
                    "Patrocinio", -1000, idAnalisisF
            );

            assertEquals(0, id);
            assertTrue(
                    ingresoService.listarIngresos(idAnalisisF).isEmpty()
            );
        }
    }

    // =========================
    // ELIMINAR INGRESO
    // =========================
    @Nested
    @DisplayName("Eliminar Ingreso")
    class EliminarIngreso {

        @Test
        void eliminarIngreso_existente_noExisteEnBD() {

            int id = ingresoService.agregarIngreso(
                    "Taquilla", 1000000, idAnalisisF
            );

            assertTrue(id > 0);

            ingresoService.eliminarIngreso(id);

            List<Ingreso> lista =
                    ingresoService.listarIngresos(idAnalisisF);

            assertTrue(lista.isEmpty(), "El ingreso debe haberse eliminado");
        }
    }

    // =========================
    // LISTAR INGRESOS
    // =========================
    @Nested
    @DisplayName("Listar Ingresos")
    class ListarIngresos {

        @Test
        void listarIngresos_variosAgregados_retornaTodos() {

            ingresoService.agregarIngreso(
                    "Taquilla", 1000000, idAnalisisF
            );
            ingresoService.agregarIngreso(
                    "Patrocinio", 500000, idAnalisisF
            );
            ingresoService.agregarIngreso(
                    "Merchandising", 200000, idAnalisisF
            );

            List<Ingreso> lista =
                    ingresoService.listarIngresos(idAnalisisF);

            assertEquals(3, lista.size());
        }

        @Test
        void listarIngresos_sinDatos_retornaVacia() {

            List<Ingreso> lista =
                    ingresoService.listarIngresos(idAnalisisF);

            assertTrue(lista.isEmpty());
        }
    }

    // =========================
    // TOTAL INGRESOS
    // =========================
    @Nested
    @DisplayName("Total Ingresos")
    class TotalIngresos {

        @Test
        void obtenerTotalIngresos_variosRegistros_retornaSuma() {

            ingresoService.agregarIngreso(
                    "Taquilla", 1000000, idAnalisisF
            );
            ingresoService.agregarIngreso(
                    "Patrocinio", 500000, idAnalisisF
            );

            int total = ingresoService.obtenerTotalIngresos(idAnalisisF);

            assertEquals(1500000, total);
        }

        @Test
        void obtenerTotalIngresos_sinRegistros_retornaCero() {

            int total = ingresoService.obtenerTotalIngresos(idAnalisisF);

            assertEquals(0, total);
        }
    }
}