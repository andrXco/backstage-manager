package org.example.ax0006.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.example.ax0006.entity.Usuario;
import org.example.ax0006.manager.SceneManager;
import org.example.ax0006.manager.SesionManager;
import org.example.ax0006.service.ProfileService;


import java.io.IOException;

public class ProfileController {

    /* ATRIBUTOS */
    private SceneManager sceneManager;
    private SesionManager sesion;
    private ProfileService profileService;

    /* CONSTRUCTOR DE LA CLASE */
    public ProfileController(SceneManager sceneManager, SesionManager sesion, ProfileService profileService) {
        this.sceneManager = sceneManager;
        this.sesion = sesion;
        this.profileService = profileService;
    }

    @FXML
    private Button bt_Cerrar;

    @FXML
    private Button bt_EditProfile;

    @FXML
    private Button bt_ChangePassword;

    @FXML
    private Label lbl_Correo;

    @FXML
    private Label lbl_Direccion;

    @FXML
    private Label lbl_Nombre;

    @FXML
    private Label lbl_NombreEmergencia;

    @FXML
    private Label lbl_RelacionEmergencia;

    @FXML
    private Label lbl_Rol;

    @FXML
    private Label lbl_Telefono;

    @FXML
    private Label lbl_TelefonoEmergencia;

    @FXML
    public void initialize() {
        cargarDatosUsuario();
    }

    private void mostrarAlerta(String causaError) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de Perfil");
        alert.setHeaderText("No se pudo cargar la información del perfil");
        alert.setContentText(causaError);
        alert.showAndWait();
    }

    private void cargarDatosUsuario() {
        Usuario usuarioSesion = sesion.getUsuarioActual();

        if (usuarioSesion == null) {
            mostrarAlerta("No hay un usuario en sesión.");
            return;
        }

        Usuario usuarioActual = profileService.obtenerPerfilCompleto(usuarioSesion.getIdUsuario());

        if (usuarioActual == null) {
            mostrarAlerta("No se pudieron cargar los datos del usuario.");
            return;
        }

        try {
            lbl_Nombre.setText(valorSeguro(usuarioActual.getNombre()));
            lbl_Correo.setText(valorSeguro(usuarioActual.getGmail()));
            lbl_Rol.setText(profileService.obtenerRolesDelUsuario(usuarioActual.getIdUsuario()));

            lbl_Telefono.setText(valorSeguro(usuarioActual.getTelefono()));
            lbl_Direccion.setText(valorSeguro(usuarioActual.getDireccion()));
            lbl_NombreEmergencia.setText(valorSeguro(usuarioActual.getContactoEmergenciaNombre()));
            lbl_TelefonoEmergencia.setText(valorSeguro(usuarioActual.getContactoEmergenciaTelefono()));
            lbl_RelacionEmergencia.setText(valorSeguro(usuarioActual.getContactoEmergenciaRelacion()));

            sesion.setUsuarioActual(usuarioActual);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Ocurrió un error al cargar los datos del usuario.");
        }
    }

    private String valorSeguro(String valor) {
        return valor == null ? "" : valor;
    }

    @FXML
    void On_Cerrar(ActionEvent event) throws IOException {
        sceneManager.showMenu();
    }

    @FXML
    void On_EditProfile(ActionEvent event) {
        try {
            sceneManager.showEditProfile();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("No se pudo abrir la ventana de edición.");
        }
    }

    @FXML
    void On_ChangePassword(ActionEvent event) {
        try {
            sceneManager.showChangePassword();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("No se pudo abrir la ventana de cambio de contraseña.");
        }
    }
}