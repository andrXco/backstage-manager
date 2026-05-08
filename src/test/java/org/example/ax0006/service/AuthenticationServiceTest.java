package org.example.ax0006.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.example.ax0006.db.H2;
import org.example.ax0006.entity.Usuario;
import org.example.ax0006.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("AuthenticationService")
class AuthenticationServiceTest {

    private H2 h2;
    private UsuarioRepository usuarioRepository;
    private AutenticacionService autenticacionService;

    @BeforeEach
    void prepararEscenario() {
        // Se crea una base de datos nueva para que cada prueba empiece aislada.
        h2 = new H2();

        // Se inicializan las tablas necesarias antes de usar el repositorio.
        h2.inicializarDB();

        // Se crea el repositorio real conectado a la base de datos de prueba.
        usuarioRepository = new UsuarioRepository(h2);

        // Se crea el servicio real que será probado.
        autenticacionService = new AutenticacionService(usuarioRepository);
    }

    @Nested
    @DisplayName("login")
    class Login {

        @Test
        @DisplayName("retorna el usuario cuando las credenciales son correctas")
        void loginExitoso() {
            // Se registra un usuario válido para que exista en la base de datos.
            boolean usuarioCreado = autenticacionService.signUp(
                    "testuser_login_exitoso",
                    "password123",
                    "test_login_exitoso@example.com");

            // Se confirma que el registro previo fue exitoso antes de probar el login.
            assertTrue(usuarioCreado, "El usuario de prueba debe crearse correctamente.");

            // Se intenta iniciar sesión con el mismo usuario y la misma contraseña registrados.
            Usuario usuarioLogin = autenticacionService.login(
                    "testuser_login_exitoso",
                    "password123");

            // Se verifica que el login retorne un usuario y que sus datos coincidan.
            assertAll("usuario autenticado",
                    () -> assertNotNull(usuarioLogin, "El login correcto debe retornar un usuario."),
                    () -> assertEquals("testuser_login_exitoso", usuarioLogin.getNombre()),
                    () -> assertEquals("test_login_exitoso@example.com", usuarioLogin.getGmail()));
        }

        @Test
        @DisplayName("retorna null cuando el usuario no existe")
        void loginConUsuarioInexistente() {
            // Se usa un nombre de usuario que no fue registrado en la base de datos.
            String nombreUsuario = "usuario_inexistente";

            // Se intenta iniciar sesión con un usuario inexistente.
            Usuario usuarioLogin = autenticacionService.login(nombreUsuario, "wrongPassword");

            // Se comprueba que el servicio no autentique usuarios que no existen.
            assertNull(usuarioLogin, "El login con usuario inexistente debe retornar null.");
        }

        @Test
        @DisplayName("retorna null cuando la contraseña es incorrecta")
        void loginConPasswordIncorrecto() {
            // Se registra un usuario válido con una contraseña conocida.
            boolean usuarioCreado = autenticacionService.signUp(
                    "testuser_password_incorrecto",
                    "password123",
                    "password_incorrecto@example.com");

            // Se confirma que el usuario exista antes de probar una contraseña inválida.
            assertTrue(usuarioCreado, "El usuario de prueba debe crearse correctamente.");

            // Se intenta iniciar sesión con el usuario correcto, pero con otra contraseña.
            Usuario usuarioLogin = autenticacionService.login(
                    "testuser_password_incorrecto",
                    "otraPassword");

            // Se verifica que el servicio rechace la autenticación.
            assertNull(usuarioLogin, "El login con contraseña incorrecta debe retornar null.");
        }
    }

    @Nested
    @DisplayName("signUp")
    class SignUp {

        @Test
        @DisplayName("crea un usuario nuevo cuando los datos son válidos")
        void creaUsuarioNuevo() {
            // Se definen datos válidos para un usuario que todavía no existe.
            String nombre = "usuario_signup_valido";
            String password = "pass123";
            String gmail = "usuario_signup_valido@example.com";

            // Se registra el usuario mediante el servicio de autenticación.
            boolean usuarioCreado = autenticacionService.signUp(nombre, password, gmail);

            // Se verifica que el servicio confirme la creación del usuario.
            assertTrue(usuarioCreado, "El usuario nuevo debe crearse exitosamente.");

            // Se inicia sesión con el usuario recién creado para confirmar que quedó guardado.
            Usuario usuarioLogin = autenticacionService.login(nombre, password);

            // Se comprueba que el usuario recuperado tenga los datos esperados.
            assertAll("usuario creado",
                    () -> assertNotNull(usuarioLogin),
                    () -> assertEquals(nombre, usuarioLogin.getNombre()),
                    () -> assertEquals(gmail, usuarioLogin.getGmail()));
        }

        @Test
        @DisplayName("rechaza un usuario con nombre duplicado")
        void rechazaUsuarioDuplicado() {
            // Se registra un primer usuario con un nombre determinado.
            boolean primerRegistro = autenticacionService.signUp(
                    "usuario_duplicado",
                    "pass1",
                    "usuario1@example.com");

            // Se intenta registrar otro usuario usando el mismo nombre.
            boolean segundoRegistro = autenticacionService.signUp(
                    "usuario_duplicado",
                    "pass2",
                    "usuario2@example.com");

            // Se comprueba que el primer registro sea aceptado y el duplicado sea rechazado.
            assertAll("usuario duplicado",
                    () -> assertTrue(primerRegistro, "El primer usuario debe crearse."),
                    () -> assertFalse(segundoRegistro, "No debe permitirse un nombre de usuario duplicado."));
        }

        @Test
        @DisplayName("permite crear varios usuarios con nombres diferentes")
        void creaMultiplesUsuarios() {
            // Se registra un primer usuario con nombre y correo propios.
            boolean usuario1 = autenticacionService.signUp(
                    "usuario_multiple_1",
                    "pass1",
                    "usuario_multiple_1@example.com");

            // Se registra un segundo usuario con nombre y correo diferentes.
            boolean usuario2 = autenticacionService.signUp(
                    "usuario_multiple_2",
                    "pass2",
                    "usuario_multiple_2@example.com");

            // Se verifica que ambos registros sean aceptados por el servicio.
            assertAll("múltiples usuarios",
                    () -> assertTrue(usuario1, "El primer usuario debe crearse correctamente."),
                    () -> assertTrue(usuario2, "El segundo usuario debe crearse correctamente."));

            // Se inicia sesión con el primer usuario para confirmar que fue persistido.
            Usuario loginUsuario1 = autenticacionService.login(
                    "usuario_multiple_1",
                    "pass1");

            // Se inicia sesión con el segundo usuario para confirmar que fue persistido.
            Usuario loginUsuario2 = autenticacionService.login(
                    "usuario_multiple_2",
                    "pass2");

            // Se valida que cada login retorne el usuario correspondiente.
            assertAll("usuarios autenticables",
                    () -> assertNotNull(loginUsuario1),
                    () -> assertEquals("usuario_multiple_1", loginUsuario1.getNombre()),
                    () -> assertNotNull(loginUsuario2),
                    () -> assertEquals("usuario_multiple_2", loginUsuario2.getNombre()));
        }
    }
}