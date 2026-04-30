package org.example.ax0006.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.example.ax0006.Entity.Usuario;
import org.example.ax0006.Manager.SesionManager;
import org.example.ax0006.Repository.UsuarioRepository;
import org.example.ax0006.Service.AutenticacionService;
import org.example.ax0006.db.H2;

class StartControllerTest {

    @Test
    void testLoginExitoso() {
        // Configurar la base de datos
        H2 h2 = new H2();
        h2.inicializarDB();

        // Crear repositorio y servicio
        UsuarioRepository usuarioRepo = new UsuarioRepository(h2);
        AutenticacionService autenService = new AutenticacionService(usuarioRepo);

        // Crear un usuario de prueba
        boolean signUpExitoso = autenService.signUp("testuser", "password123", "test@example.com");
        assertTrue(signUpExitoso, "El sign up debería ser exitoso");

        // Intentar login con credenciales correctas
        Usuario usuarioLogin = autenService.login("testuser", "password123");
        assertNotNull(usuarioLogin, "El usuario no debería ser nulo tras login exitoso");
        assertEquals("testuser", usuarioLogin.getNombre(), "El nombre debe ser testuser");
        assertEquals("test@example.com", usuarioLogin.getGmail(), "El email debe coincidir");
    }

    @Test
    void testLoginFallido() {
        // Configurar la base de datos
        H2 h2 = new H2();
        h2.inicializarDB();

        // Crear repositorio y servicio
        UsuarioRepository usuarioRepo = new UsuarioRepository(h2);
        AutenticacionService autenService = new AutenticacionService(usuarioRepo);

        // Intentar login con usuario inexistente
        Usuario usuarioLogin = autenService.login("usuarioInexistente", "wrongPassword");
        assertNull(usuarioLogin, "El usuario debería ser nulo con credenciales inválidas");
    }

    @Test
    void testSesionGuardaUsuario() {
        // Crear un usuario de prueba
        Usuario usuario = new Usuario(1, "Felipe", "password_hash", "felipe@example.com");

        // Crear la sesión
        SesionManager sesion = new SesionManager();
        sesion.setUsuarioActual(usuario);

        // Verificar que la sesión guardó el usuario correctamente
        assertNotNull(sesion.getUsuarioActual(), "El usuario actual no debería ser nulo");
        assertEquals("Felipe", sesion.getUsuarioActual().getNombre(), "El nombre debe ser Felipe");
        assertEquals(1, sesion.getUsuarioActual().getIdUsuario(), "El ID debe ser 1");
    }

    @Test
    void testCrearMultiplesUsuarios() {
        // Configurar la base de datos
        H2 h2 = new H2();
        h2.inicializarDB();

        // Crear repositorio y servicio
        UsuarioRepository usuarioRepo = new UsuarioRepository(h2);
        AutenticacionService autenService = new AutenticacionService(usuarioRepo);

        // Crear múltiples usuarios
        boolean usuario1 = autenService.signUp("usuario1", "pass1", "usuario1@example.com");
        boolean usuario2 = autenService.signUp("usuario2", "pass2", "usuario2@example.com");

        assertTrue(usuario1, "El primer usuario debería crearse exitosamente");
        assertTrue(usuario2, "El segundo usuario debería crearse exitosamente");

        // Intentar crear un usuario duplicado
        boolean usuarioDuplicado = autenService.signUp("usuario1", "pass3", "otro@example.com");
        assertFalse(usuarioDuplicado, "No debería permitir crear un usuario con nombre duplicado");
    }

    @Test
    void testDatosUsuarioCompletos() {
        // Crear un usuario con todos los datos
        Usuario usuario = new Usuario(5, "Juan Pérez", "password_hash", "juan@example.com");
        usuario.setTelefono("1234567890");
        usuario.setDireccion("Calle Principal 123");
        usuario.setContactoEmergenciaNombre("Maria Pérez");
        usuario.setContactoEmergenciaTelefono("9876543210");
        usuario.setContactoEmergenciaRelacion("Hermana");

        // Verificar que todos los datos se guardaron correctamente
        assertEquals("Juan Pérez", usuario.getNombre());
        assertEquals("juan@example.com", usuario.getGmail());
        assertEquals("1234567890", usuario.getTelefono());
        assertEquals("Calle Principal 123", usuario.getDireccion());
        assertEquals("Maria Pérez", usuario.getContactoEmergenciaNombre());
        assertEquals("9876543210", usuario.getContactoEmergenciaTelefono());
        assertEquals("Hermana", usuario.getContactoEmergenciaRelacion());
    }
}