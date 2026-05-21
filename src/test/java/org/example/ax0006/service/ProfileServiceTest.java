package org.example.ax0006.service;

import org.example.ax0006.db.H2;
import org.example.ax0006.entity.Usuario;
import org.example.ax0006.repository.AsignacionStaffRepository;
import org.example.ax0006.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProfileService")
class ProfileServiceTest {

    private H2 h2;
    private UsuarioRepository usuarioRepository;
    private ProfileService profileService;
    AutenticacionService autenticacionService;
    AsignacionStaffRepository asignacionStaffRepository;

    @BeforeEach
    void setUp() {
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
        usuarioRepository = new UsuarioRepository(h2);

        //Se crean los serivicios necesarios
        autenticacionService = new AutenticacionService(usuarioRepository, asignacionStaffRepository);

        //Se crea el servicio que se va probar
        profileService = new ProfileService(usuarioRepository);

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
        }finally {
            h2.cerrarServidor();
        }
    }

    @Test
    @DisplayName("obtener el perfil completo y actualizar perfil")
    void obtenerPerfilCompleto() {
        // Se registra un usuario válido para que exista en la base de datos.
        boolean usuarioCreado = autenticacionService.signUp(
                "testuser_login_exitoso",
                "password123",
                "test_login_exitoso@example.com");

        final Usuario usuario = profileService.obtenerPerfilCompleto(4);

        assertAll("verificacion de usuario completo",
                ()-> assertTrue(usuarioCreado),
                ()-> assertEquals("testuser_login_exitoso", usuario.getNombre()),
                () -> assertTrue(BCrypt.checkpw("password123", usuario.getContrasena())),
                ()-> assertEquals("test_login_exitoso@example.com", usuario.getGmail()),
                ()-> assertNull(usuario.getContactoEmergenciaNombre()),
                () -> assertNull(usuario.getContactoEmergenciaRelacion()),
                () -> assertNull(usuario.getContactoEmergenciaTelefono()),
                () -> assertNull(usuario.getDireccion()),
                ()-> assertNull(usuario.getTelefono())
        );

        usuario.setContactoEmergenciaNombre("test");
        usuario.setContactoEmergenciaRelacion("primo");
        usuario.setContactoEmergenciaTelefono("1");
        usuario.setDireccion("calle 1");
        usuario.setTelefono("2");

        profileService.actualizarPerfil(usuario);

        final Usuario usuario1 =  profileService.obtenerPerfilCompleto(4);

        assertAll("verificacion de usuario completo",
                ()-> assertTrue(usuarioCreado),
                ()-> assertEquals("testuser_login_exitoso", usuario1.getNombre()),
                () -> assertTrue(BCrypt.checkpw("password123", usuario1.getContrasena())),
                ()-> assertEquals("test_login_exitoso@example.com", usuario1.getGmail()),
                () -> assertEquals("test", usuario1.getContactoEmergenciaNombre()),
                () -> assertEquals("primo", usuario1.getContactoEmergenciaRelacion()),
                () -> assertEquals("1", usuario1.getContactoEmergenciaTelefono()),
                () -> assertEquals("calle 1", usuario1.getDireccion()),
                () -> assertEquals("2", usuario1.getTelefono())
        );


    }

    //PENDIENTE
    @Test
    void obtenerRolesDelUsuario() {
    }

    @Test
    void cambiarContrasena() {
        boolean usuarioCreado = autenticacionService.signUp(
                "testuser_login_exitoso",
                "password123",
                "test_login_exitoso@example.com");

        final Usuario usuario = profileService.obtenerPerfilCompleto(4);

        assertAll("verificacion de usuario completo",
                ()-> assertTrue(usuarioCreado),
                ()-> assertEquals("testuser_login_exitoso", usuario.getNombre()),
                () -> assertTrue(BCrypt.checkpw("password123", usuario.getContrasena())),
                ()-> assertEquals("test_login_exitoso@example.com", usuario.getGmail()),
                ()-> assertNull(usuario.getContactoEmergenciaNombre()),
                () -> assertNull(usuario.getContactoEmergenciaRelacion()),
                () -> assertNull(usuario.getContactoEmergenciaTelefono()),
                () -> assertNull(usuario.getDireccion()),
                ()-> assertNull(usuario.getTelefono())
        );

        profileService.cambiarContrasena(4, "123password");

        final Usuario usuario1 = profileService.obtenerPerfilCompleto(4);

        assertAll("verificacion de usuario completo",
                ()-> assertTrue(usuarioCreado),
                ()-> assertEquals("testuser_login_exitoso", usuario1.getNombre()),
                () -> assertTrue(BCrypt.checkpw("123password", usuario1.getContrasena())),
                ()-> assertEquals("test_login_exitoso@example.com", usuario1.getGmail()),
                ()-> assertNull(usuario1.getContactoEmergenciaNombre()),
                () -> assertNull(usuario1.getContactoEmergenciaRelacion()),
                () -> assertNull(usuario1.getContactoEmergenciaTelefono()),
                () -> assertNull(usuario1.getDireccion()),
                ()-> assertNull(usuario1.getTelefono())
        );
    }
}