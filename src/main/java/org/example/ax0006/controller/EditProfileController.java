package org.example.ax0006.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.example.ax0006.entity.Usuario;
import org.example.ax0006.manager.SceneManager;
import org.example.ax0006.manager.SesionManager;
import org.example.ax0006.service.ProfileService;

import java.io.IOException;

public class EditProfileController {

    private SceneManager sceneManager;
    private SesionManager sesion;
    private ProfileService profileService;

    public EditProfileController(SceneManager sceneManager, SesionManager sesion, ProfileService profileService) {
        this.sceneManager = sceneManager;
        this.sesion = sesion;
        this.profileService = profileService;
    }

    @FXML
    private Button bt_Guardar;

    @FXML
    private Button bt_cancelar;

    @FXML
    private ComboBox<String> cb_RelacionEmergencia;

    @FXML
    private TextField ii_Correo;

    @FXML
    private TextField ii_Nombre;

    @FXML
    private TextField ii_NombreEmergencia;

    @FXML
    private TextField ii_TelefonoEmergencia;

    @FXML
    private TextArea ii_direccion;

    @FXML
    private TextField ii_telefono;

    @FXML
    private Label lblRol;

    @FXML
    public void initialize() {
        cb_RelacionEmergencia.getItems().addAll(
                "Padre",
                "Madre",
                "Hermano/a",
                "Pareja",
                "Amigo/a",
                "Otro"
        );

        cargarDatos();
    }

    private void cargarDatos() {
        Usuario usuarioSesion = sesion.getUsuarioActual();

        if (usuarioSesion == null) {
            mostrarError("No hay un usuario en sesión.");
            return;
        }

        Usuario usuarioCompleto = profileService.obtenerPerfilCompleto(usuarioSesion.getIdUsuario());

        if (usuarioCompleto == null) {
            mostrarError("No se pudieron cargar los datos del perfil.");
            return;
        }

        ii_Nombre.setText(valorSeguro(usuarioCompleto.getNombre()));
        ii_Correo.setText(valorSeguro(usuarioCompleto.getGmail()));
        ii_telefono.setText(valorSeguro(usuarioCompleto.getTelefono()));
        ii_direccion.setText(valorSeguro(usuarioCompleto.getDireccion()));
        ii_NombreEmergencia.setText(valorSeguro(usuarioCompleto.getContactoEmergenciaNombre()));
        ii_TelefonoEmergencia.setText(valorSeguro(usuarioCompleto.getContactoEmergenciaTelefono()));
        cb_RelacionEmergencia.setValue(valorSeguro(usuarioCompleto.getContactoEmergenciaRelacion()));

        lblRol.setText(profileService.obtenerRolesDelUsuario(usuarioCompleto.getIdUsuario()));
    }

    @FXML
    void On_Cancelar(ActionEvent event) {
        try {
            sceneManager.showProfile();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("No se pudo volver al perfil.");
        }
    }

    @FXML
    void On_Guardar(ActionEvent event) {
        Usuario usuarioSesion = sesion.getUsuarioActual();

        if (usuarioSesion == null) {
            mostrarError("No hay un usuario en sesión.");
            return;
        }

        Usuario actualizado = new Usuario();
        actualizado.setIdUsuario(usuarioSesion.getIdUsuario());
        actualizado.setContrasena(usuarioSesion.getContrasena());

        actualizado.setNombre(ii_Nombre.getText());
        actualizado.setGmail(ii_Correo.getText());
        actualizado.setTelefono(ii_telefono.getText());
        actualizado.setDireccion(ii_direccion.getText());
        actualizado.setContactoEmergenciaNombre(ii_NombreEmergencia.getText());
        actualizado.setContactoEmergenciaTelefono(ii_TelefonoEmergencia.getText());
        actualizado.setContactoEmergenciaRelacion(cb_RelacionEmergencia.getValue());

        profileService.actualizarPerfil(actualizado);

        Usuario recargado = profileService.obtenerPerfilCompleto(usuarioSesion.getIdUsuario());
        sesion.setUsuarioActual(recargado);

        try {
            sceneManager.showProfile();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Se guardó el perfil, pero no se pudo volver a la vista perfil.");
        }
    }

    private String valorSeguro(String valor) {
        return valor == null ? "" : valor;
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Ocurrió un problema");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}