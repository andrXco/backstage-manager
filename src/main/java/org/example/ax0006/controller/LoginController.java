package org.example.ax0006.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import org.example.ax0006.entity.Usuario;
import org.example.ax0006.manager.SceneManager;
import org.example.ax0006.manager.SesionManager;
import org.example.ax0006.service.ActividadService;
import org.example.ax0006.service.AutenticacionService;
import org.example.ax0006.service.ConciertoService;
import org.example.ax0006.service.StaffService;

import java.io.IOException;

public class LoginController {

    /* ATRIBUTOS */
    private SceneManager sceneManager;
    private AutenticacionService autenService;
    private SesionManager sesion;
    private ActividadService actividadService;
    private StaffService staffService;
    private ConciertoService conciertoService;

    /* CONSTRUCTOR DE LA CLASE */
    public LoginController(
            SceneManager sceneManager,
            AutenticacionService autenService,
            SesionManager sesion,
            ActividadService actividadService,
            StaffService staffService,
            ConciertoService conciertoService
    ) {
        this.sceneManager = sceneManager;
        this.autenService = autenService;
        this.sesion = sesion;
        this.actividadService = actividadService;
        this.staffService = staffService;
        this.conciertoService = conciertoService;
    }

    @FXML
    private TextField fid_Usuario;

    @FXML
    private PasswordField fid_Contrasena;

    @FXML
    private Button fid_login;

    @FXML
    private Button fid_sign_up;

    @FXML
    private TextField fid_ContrasenaVisible;

    private boolean mostrando = false;

    @FXML
    /* METODO PARA HACER QUE EL '👁' QUE MUESTRA LA CONTRASEÑA FUNCIONE EN EL CAMPO DE CONTRASEÑA */
    public void togglePassword() {
        if (mostrando) {
            fid_Contrasena.setText(fid_ContrasenaVisible.getText());
            fid_Contrasena.setVisible(true);
            fid_Contrasena.setManaged(true);
            fid_ContrasenaVisible.setVisible(false);
            fid_ContrasenaVisible.setManaged(false);
        } else {
            fid_ContrasenaVisible.setText(fid_Contrasena.getText());
            fid_ContrasenaVisible.setVisible(true);
            fid_ContrasenaVisible.setManaged(true);
            fid_Contrasena.setVisible(false);
            fid_Contrasena.setManaged(false);
        }

        mostrando = !mostrando;
    }

    /* METODO PARA CREAR UNA PANTALLA POP UP PARA AVISAR EN CASO DE ERROR */
    void AlertaLogin(String CausaError) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de Login");
        alert.setHeaderText("No se pudo iniciar sesión");
        alert.setContentText(CausaError);

        alert.showAndWait();
    }

    @FXML
        /* METODO PARA PODER IR A LA PANTALLA DE SIGN UP */
    void On_sign_up(ActionEvent event) throws IOException {
        sceneManager.showSignUp();
    }

    @FXML
        /* METODO QUE EJECUTA EL LOGIN Y QUE CAMBIA A LA PANTALLA DE MENU SI ESTE ES EXITOSO */
    void On_login(ActionEvent event) throws IOException {
        if (mostrando) {
            togglePassword();
        }

        // A través del servicio de autenticación obtenemos el usuario que se va a logear
        Usuario usuarioLogin = autenService.login(
                fid_Usuario.getText(),
                fid_Contrasena.getText()
        );

        /* MENSAJES DE ERROR */
        if (usuarioLogin == null) {
            System.out.println("Usuario no existe");
            AlertaLogin("Error El usuario o contraseña incorrectos");
            return;
        }

        /* SE ASIGNA EL USUARIO LOGEADO AL USUARIO EN LA CLASE SESION */
        sesion.setUsuarioActual(usuarioLogin);

        /* REGISTRAR NOTIFICACION DE LOGIN */
        if (actividadService != null) {
            actividadService.registrarLogin(usuarioLogin);
        }

        /*
            SI EL USUARIO NO ES ADMINISTRADOR,
            SE BUSCA EL CONCIERTO ASOCIADO AL STAFF
        */
        if (usuarioLogin.getIdRol() != 1) {
            int idConcierto = staffService.obtenerIdConciertoDelUsuario(usuarioLogin.getIdUsuario());

            if (idConcierto != -1) {
                conciertoService.obtenerConciertosSolos().stream()
                        .filter(c -> c.getIdConcierto() == idConcierto)
                        .findFirst()
                        .ifPresent(sesion::setConciertoActual);
            }
        }

        /* EN CASO DE UN LOGEO EXITOSO CAMBIAMOS A LA VENTANA DE MENU */
        sceneManager.showMenu();
    }
}