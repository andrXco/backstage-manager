package org.example.ax0006.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import org.example.ax0006.entity.Usuario;
import org.example.ax0006.manager.SceneManager;
import org.example.ax0006.manager.SesionManager;
import org.example.ax0006.service.ProfileService;

import java.io.IOException;

public class ChangePasswordController {

    private final SceneManager sceneManager;
    private final SesionManager sesion;
    private final ProfileService profileService;

    public ChangePasswordController(SceneManager sceneManager, SesionManager sesion, ProfileService profileService) {
        this.sceneManager = sceneManager;
        this.sesion = sesion;
        this.profileService = profileService;
    }

    @FXML
    private PasswordField ft_ContrasenaActual;

    @FXML
    private PasswordField ft_NuevaContrasena;

    @FXML
    private PasswordField ft_ConfirmarContrasena;

    @FXML
    private Button bt_GuardarCambio;

    @FXML
    private Button bt_CancelarCambio;

    @FXML
    void On_GuardarCambio(ActionEvent event) {
        Usuario usuarioSesion = sesion.getUsuarioActual();

        if (usuarioSesion == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No hay un usuario en sesión.");
            return;
        }

        String actual = ft_ContrasenaActual.getText();
        String nueva = ft_NuevaContrasena.getText();
        String confirmacion = ft_ConfirmarContrasena.getText();

        if (actual == null || actual.isBlank() || nueva == null || nueva.isBlank() || confirmacion == null || confirmacion.isBlank()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Campos obligatorios", "Debe completar todos los campos.");
            return;
        }

        if (!usuarioSesion.getContrasena().equals(actual)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Contraseña incorrecta", "La contraseña actual no coincide.");
            return;
        }

        if (nueva.length() < 8) {
            mostrarAlerta(Alert.AlertType.ERROR, "Contraseña inválida", "La nueva contraseña debe tener al menos 8 caracteres.");
            return;
        }

        if (nueva.equals(actual)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Contraseña inválida", "La nueva contraseña debe ser diferente a la actual.");
            return;
        }

        if (!nueva.equals(confirmacion)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Confirmación inválida", "La confirmación no coincide con la nueva contraseña.");
            return;
        }

        boolean actualizada = profileService.cambiarContrasena(usuarioSesion.getIdUsuario(), nueva);

        if (!actualizada) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo actualizar la contraseña.");
            return;
        }

        Usuario recargado = profileService.obtenerPerfilCompleto(usuarioSesion.getIdUsuario());
        sesion.setUsuarioActual(recargado);

        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "La contraseña fue actualizada correctamente.");

        try {
            sceneManager.showProfile();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Navegación", "La contraseña se actualizó, pero no se pudo volver al perfil.");
        }
    }

    @FXML
    void On_CancelarCambio(ActionEvent event) {
        try {
            sceneManager.showProfile();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Navegación", "No se pudo volver al perfil.");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
