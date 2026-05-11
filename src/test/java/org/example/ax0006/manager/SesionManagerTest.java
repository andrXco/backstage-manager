package org.example.ax0006.manager;

import org.example.ax0006.entity.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Session")
class SesionManagerTest {
    @Test
    @DisplayName("guarda el usuario actual en sesión")
    void sesionGuardaUsuario() {
        // Se crea un usuario de prueba con datos conocidos.
        Usuario usuario = new Usuario(
                1,
                "a",
                "password_hash",
                "a@example.com");

        // Se crea una sesión inicialmente vacía.
        SesionManager sesion = new SesionManager();

        // Se asigna el usuario como usuario actual de la sesión.
        sesion.setUsuarioActual(usuario);

        // Se obtiene el usuario guardado en la sesión.
        Usuario usuarioActual = sesion.getUsuarioActual();

        // Se verifica que la sesión conserve el usuario asignado.
        assertAll("usuario en sesión",
                () -> assertNotNull(usuarioActual),
                () -> assertEquals(1, usuarioActual.getIdUsuario()),
                () -> assertEquals("a", usuarioActual.getNombre()),
                () -> assertEquals("a@example.com", usuarioActual.getGmail()));
    }

    @Test
    @DisplayName("inicia sin usuario actual")
    void sesionIniciaSinUsuarioActual() {
        // Se crea una sesión nueva sin asignarle ningún usuario.
        SesionManager sesion = new SesionManager();

        // Se consulta el usuario actual de la sesión recién creada.
        Usuario usuarioActual = sesion.getUsuarioActual();

        // Se comprueba que una sesión nueva no tenga usuario autenticado.
        assertNull(usuarioActual, "Una sesión nueva debe iniciar sin usuario actual.");
    }
}