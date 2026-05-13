package org.example.ax0006.service;

import org.example.ax0006.db.H2;
import org.example.ax0006.entity.Concierto;
import org.example.ax0006.entity.Horario;
import org.example.ax0006.entity.Usuario;
import org.example.ax0006.repository.*;
import org.example.ax0006.validator.ConciertoValidator;
import org.example.ax0006.validator.HorarioValidator;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ConciertoServiceTest {

    private H2 h2;
    private ConciertoRepository conciertoRepo;
    private HorarioRepository horarioRepo;
    private ContratoRepository contratoRepo;

    private ContratoService contratoService;
    private ConciertoValidator conciertoValidator;
    private HorarioValidator horarioValidator;
    private AsignacionStaffRepository asignacionStaffRepo;
    private UsuarioRepository usuarioRepo;

    private ConciertoService conciertoService;

    @BeforeEach
    void prepararEscenario() {
        // Se crea una base de datos nueva para que cada prueba empiece aislada.
        h2 = new H2();

        try (Connection conn = h2.getConnection();
             Statement stmt = conn.createStatement()) {

            // Desactiva la integridad referencial momentáneamente y borra todo
            // Se desactiva la integridad referencial para que sea posible borrar la base de datos con facilidad ya que con esto no se podria
            stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
            stmt.execute("DROP ALL OBJECTS");
            stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Falló la limpieza de la base de datos antes de la prueba");
        }

        // Se inicializan las tablas necesarias antes de usar el repositorio.
        h2.inicializarDB();

        // Se crea el repositorio real conectado a la base de datos de prueba.
        conciertoRepo = new ConciertoRepository(h2);
        horarioRepo = new HorarioRepository(h2);
        contratoRepo = new ContratoRepository(h2);
        usuarioRepo = new UsuarioRepository(h2);

        //Se crean los validadores
        horarioValidator = new HorarioValidator();
        conciertoValidator = new ConciertoValidator(horarioValidator);

        //Se crea un servicio que es parte del serivico a probar
        contratoService = new ContratoService(contratoRepo);


        // Se crea el servicio real que será probado.
        conciertoService = new ConciertoService(conciertoRepo, horarioRepo, conciertoValidator, contratoService, asignacionStaffRepo);
    }

    //Se vuelve a borrar la base de datos para que no queden datos de las pruebas
    @AfterAll
    static void BorrarDB(){
        H2 h2 = new H2();
        try (Connection conn = h2.getConnection();
             Statement stmt = conn.createStatement()) {

            // Desactiva la integridad referencial momentáneamente y borra todo
            // Se desactiva la integridad referencial para que sea posible borrar la base de datos con facilidad ya que con esto no se podria
            stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
            stmt.execute("DROP ALL OBJECTS");
            stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Falló la limpieza de la base de datos antes de la prueba");
        }
    }


    @Nested
    @DisplayName("Crear Concierto")
    class CrearConcierto{
        @Test
        @DisplayName("Crear concierto Exito")
        void crearConcierto() {
            Concierto concierto = new Concierto();

            //Crear el concierto de prueba
            concierto.setIdConcierto(1);
            concierto.setNombreConcierto("Tour mundial");

                //Se crea el horario para el concierto
                Horario horario = new Horario();
                horario.setIdHorario(1);
                LocalDate fecha = LocalDate.of(2026, 12, 29);
                horario.setFechaInicio(fecha);
                fecha = fecha.plusDays(1);
                horario.setFechaFin(fecha);
                LocalTime hora = LocalTime.of(11, 00);
                horario.setHoraInicio(hora);
                hora = LocalTime.of(12, 00);
                horario.setHoraFin(hora);
                horarioRepo.guardar(horario);

            concierto.setHorario(horario);
            concierto.setAforo(25600);

                //Se crea el artista para el concierto a provar
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(1);
                usuario.setNombre("Juan Luis Guerra");
                usuario.setIdRol(3);
                usuarioRepo.guardar(usuario);

//            concierto.setArtista(usuario);
//
//            conciertoService.crearConcierto(concierto);
//
//            conciertoService.obtenerConciertosSolos();

        }
    }


    @Test
    void obtenerConciertosSolos() {
    }

    @Test
    void obtenerConciertos() {
    }

    @Test
    void aprobarConcierto() {
    }

    @Test
    void eliminarConcierto() {
    }
}