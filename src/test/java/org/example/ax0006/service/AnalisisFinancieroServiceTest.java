package org.example.ax0006.service;

import org.example.ax0006.db.H2;
import org.example.ax0006.entity.AnalisisFinanciero;
import org.example.ax0006.repository.AnalisisFinancieroRepository;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnalisisFinancieroServiceTest {

    // =========================
    // ATRIBUTOS
    // =========================
    private H2 h2;
    private AnalisisFinancieroRepository analisisRepo;
    private AnalisisFinancieroService analisisService;

    // =========================
    // SETUP
    // =========================
    @BeforeEach
    void prepararEscenario() {
        h2 = new H2();
        h2.inicializarDB();
        analisisRepo = new AnalisisFinancieroRepository(h2);
        analisisService = new AnalisisFinancieroService(analisisRepo);
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
    // CREAR PRESUPUESTO
    // =========================
    @Nested
    @DisplayName("Crear Presupuesto")
    class CrearPresupuesto {

        @Test
        void crearPresupuesto_valorValido_seGuardaEnBD() {

            int id = analisisService.crearPresupuesto(5000);

            assertTrue(id > 0, "El id generado debe ser mayor a 0");

            AnalisisFinanciero af = analisisService.obtenerAnalisis(id);

            assertNotNull(af, "El análisis debe existir en BD");
            assertEquals(5000, af.getPresupuesto());
            assertFalse(af.isAprobado());
        }

        @Test
        void crearPresupuesto_valorCero_noSeGuarda() {

            int id = analisisService.crearPresupuesto(0);

            assertEquals(0, id, "No debe crearse un análisis con presupuesto 0");
        }

        @Test
        void crearPresupuesto_valorNegativo_noSeGuarda() {

            int id = analisisService.crearPresupuesto(-100);

            assertEquals(0, id, "No debe crearse un análisis con presupuesto negativo");
        }
    }

    // =========================
    // EDITAR PRESUPUESTO
    // =========================
    @Nested
    @DisplayName("Editar Presupuesto")
    class EditarPresupuesto {

        @Test
        void editarPresupuesto_valorValido_seActualizaEnBD() {

            int id = analisisService.crearPresupuesto(5000);

            analisisService.editarPresupuesto(id, 10000);

            AnalisisFinanciero af = analisisService.obtenerAnalisis(id);

            assertNotNull(af);
            assertEquals(10000, af.getPresupuesto());
        }

        @Test
        void editarPresupuesto_valorCero_noActualiza() {

            int id = analisisService.crearPresupuesto(5000);

            analisisService.editarPresupuesto(id, 0);

            AnalisisFinanciero af = analisisService.obtenerAnalisis(id);

            assertNotNull(af);
            assertEquals(5000, af.getPresupuesto(), "El presupuesto no debe cambiar");
        }

        @Test
        void editarPresupuesto_valorNegativo_noActualiza() {

            int id = analisisService.crearPresupuesto(5000);

            analisisService.editarPresupuesto(id, -300);

            AnalisisFinanciero af = analisisService.obtenerAnalisis(id);

            assertNotNull(af);
            assertEquals(5000, af.getPresupuesto(), "El presupuesto no debe cambiar");
        }
    }

    // =========================
    // APROBAR / DESAPROBAR
    // =========================
    @Nested
    @DisplayName("Aprobar y Desaprobar Presupuesto")
    class AprobarDesaprobar {

        @Test
        void aprobarPresupuesto_cambiaAprobadoATrue() {

            int id = analisisService.crearPresupuesto(5000);

            analisisService.aprobarPresupuesto(id);

            AnalisisFinanciero af = analisisService.obtenerAnalisis(id);

            assertNotNull(af);
            assertTrue(af.isAprobado());
        }

        @Test
        void desaprobarPresupuesto_cambiaAprobadoAFalse() {

            int id = analisisService.crearPresupuesto(5000);

            analisisService.aprobarPresupuesto(id);
            analisisService.desaprobarPresupuesto(id);

            AnalisisFinanciero af = analisisService.obtenerAnalisis(id);

            assertNotNull(af);
            assertFalse(af.isAprobado());
        }
    }

    // =========================
    // OBTENER ANALISIS
    // =========================
    @Nested
    @DisplayName("Obtener Análisis")
    class ObtenerAnalisis {

        @Test
        void obtenerAnalisis_existente_retornaAnalisis() {

            int id = analisisService.crearPresupuesto(8000);

            AnalisisFinanciero af = analisisService.obtenerAnalisis(id);

            assertNotNull(af);
            assertEquals(id, af.getIdAnalisisF());
            assertEquals(8000, af.getPresupuesto());
        }

        @Test
        void obtenerAnalisis_noExistente_retornaNull() {

            AnalisisFinanciero af = analisisService.obtenerAnalisis(999);

            assertNull(af, "Debe retornar null si no existe");
        }
    }

    // =========================
    // ELIMINAR ANALISIS
    // =========================
    @Nested
    @DisplayName("Eliminar Análisis")
    class EliminarAnalisis {

        @Test
        void eliminarAnalisis_existente_noExisteEnBD() {

            int id = analisisService.crearPresupuesto(5000);

            assertNotNull(analisisService.obtenerAnalisis(id));

            analisisService.eliminarAnalisis(id);

            assertNull(
                    analisisService.obtenerAnalisis(id),
                    "El análisis debe haberse eliminado de BD"
            );
        }
    }

    // =========================
    // LISTAR ANALISIS
    // =========================
    @Nested
    @DisplayName("Listar Análisis")
    class ListarAnalisis {

        @Test
        void listarAnalisis_variosCreados_retornaTodos() {

            analisisService.crearPresupuesto(1000);
            analisisService.crearPresupuesto(2000);
            analisisService.crearPresupuesto(3000);

            List<AnalisisFinanciero> lista =
                    analisisService.listarAnalisis();

            assertEquals(3, lista.size());
        }

        @Test
        void listarAnalisis_sinDatos_retornaVacia() {

            List<AnalisisFinanciero> lista =
                    analisisService.listarAnalisis();

            assertTrue(lista.isEmpty());
        }
    }
}