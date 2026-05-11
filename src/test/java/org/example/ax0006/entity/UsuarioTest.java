package org.example.ax0006.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Usuario")
class UsuarioTest {

    @Test
    @DisplayName("guarda correctamente los datos básicos del usuario")
    void guardaDatosBasicosUsuario() {
        // Se crea un usuario con id, nombre, contraseña y correo.
        Usuario usuario = new Usuario(
                5,
                "Juan Pérez",
                "password_hash",
                "juan@example.com");

        // Se verifica que el constructor haya guardado correctamente los datos básicos.
        assertAll("datos básicos",
                () -> assertEquals(5, usuario.getIdUsuario()),
                () -> assertEquals("Juan Pérez", usuario.getNombre()),
                () -> assertEquals("password_hash", usuario.getContrasena()),
                () -> assertEquals("juan@example.com", usuario.getGmail()));
    }

    @Test
    @DisplayName("guarda correctamente los datos complementarios del usuario")
    void guardaDatosComplementariosUsuario() {
        // Se crea un usuario base para completar después su información adicional.
        Usuario usuario = new Usuario(
                5,
                "Juan Pérez",
                "password_hash",
                "juan@example.com");

        // Se asigna el teléfono del usuario.
        usuario.setTelefono("1234567890");

        // Se asigna la dirección del usuario.
        usuario.setDireccion("Calle Principal 123");

        // Se asigna el nombre del contacto de emergencia.
        usuario.setContactoEmergenciaNombre("Maria Pérez");

        // Se asigna el teléfono del contacto de emergencia.
        usuario.setContactoEmergenciaTelefono("9876543210");

        // Se asigna la relación del contacto de emergencia con el usuario.
        usuario.setContactoEmergenciaRelacion("Hermana");

        // Se verifica que todos los datos complementarios hayan quedado guardados.
        assertAll("datos complementarios",
                () -> assertEquals("1234567890", usuario.getTelefono()),
                () -> assertEquals("Calle Principal 123", usuario.getDireccion()),
                () -> assertEquals("Maria Pérez", usuario.getContactoEmergenciaNombre()),
                () -> assertEquals("9876543210", usuario.getContactoEmergenciaTelefono()),
                () -> assertEquals("Hermana", usuario.getContactoEmergenciaRelacion()));
    }

}