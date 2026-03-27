/*
 * MARTIN SANMIGUEL
 * ANDRES CORTES
 */



package org.example.ax0006.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.ax0006.Manager.SceneManager;
import org.example.ax0006.Manager.SesionManager;
import org.example.ax0006.Service.AutenticacionService;

import java.io.IOException;

public class SignUpController {

    /*ATRIBUTOS*/
    private SceneManager sceneManager;
    private AutenticacionService autenService;
    private SesionManager sesion;

    /*CONSTRUCTOR DE LA CLASE*/
    public SignUpController(SceneManager sceneManager, AutenticacionService autenService, SesionManager sesion) {
        this.sceneManager = sceneManager;
        this.autenService = autenService;
        this.sesion = sesion;
    }

    @FXML
    private TextField fid_correo;

    @FXML
    private TextField fid_Usuario;

    @FXML
    private PasswordField fid_Contrasena;

    @FXML
    private TextField fid_ContrasenaVisibleConfirmation;
    boolean mostrando = false;

    @FXML
    private Button fid_login;

    @FXML
    private PasswordField fid_ContrasenaConfirmation;
    boolean mostrandoConfirmation = false;

    @FXML
    private Button fid_sign_up;

    @FXML
    private TextField fid_ContrasenaVisible;

    @FXML
    /*METODO PARA HACER QUE EL '👁' QUE MUESTRA LA CONTRASEÑA FUNCIONE EN EL CAMPO DE CONTRASEÑA*/
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

    @FXML
    /*METODO PARA HACER QUE EL '👁' QUE MUESTRA LA CONTRASEÑA FUNCIONE EN EL CAMPO DE CONFIRMAR CONTRASEÑA*/
    void togglePasswordConfirmation() {
        if (mostrandoConfirmation) {
            fid_ContrasenaConfirmation.setText(fid_ContrasenaVisibleConfirmation.getText());

            fid_ContrasenaConfirmation.setVisible(true);
            fid_ContrasenaConfirmation.setManaged(true);

            fid_ContrasenaVisibleConfirmation.setVisible(false);
            fid_ContrasenaVisibleConfirmation.setManaged(false);

        } else {
            fid_ContrasenaVisibleConfirmation.setText(fid_ContrasenaConfirmation.getText());

            fid_ContrasenaVisibleConfirmation.setVisible(true);
            fid_ContrasenaVisibleConfirmation.setManaged(true);

            fid_ContrasenaConfirmation.setVisible(false);
            fid_ContrasenaConfirmation.setManaged(false);
        }

        mostrandoConfirmation = !mostrandoConfirmation;
    }

    /*METODO PARA HACER QUE SALGA UNA VENTANA DE ERROR, CUANDO HAY ALGUN ERROR EN EL SIGN UP*/
    void alertaSignUp(String CausaError){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error al Crear la cuenta");
        alert.setHeaderText("No se pudo Realizar el Sign up");
        alert.setContentText(CausaError);

        alert.showAndWait(); // Esto abre el POP UP
    }

    /*METODO PARA HACER QUE SALGA UNA VENTANA DE EXIITO, CUANDO SE CREA LA CUENTA CORRECTAMENTE*/
    void exitoSignUp (){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("La cuenta se creo correctamente");
        alert.setHeaderText("El proceso de sign up fue exitoso");
        alert.setContentText("Por favor inicie sesion");
        alert.showAndWait(); // Esto abre el POP UP
    }

    @FXML
    void On_crear_usuario(ActionEvent event) throws IOException {

        if (mostrando && mostrandoConfirmation) {
            togglePassword();
            togglePasswordConfirmation();
        }

        String correo = fid_correo.getText();
        String usuario = fid_Usuario.getText();
        String contrasena = fid_Contrasena.getText();
        String confirmacion = fid_ContrasenaConfirmation.getText();

        if (correo == null || correo.isEmpty()) {
            System.out.println("Ingrese un correo");
            alertaSignUp("Tiene que ingresar un correo");
            return;
        }

        if (!correo.contains("@")) {
            System.out.println("El correo debe contener una @");
            alertaSignUp("Tiene que ingresar un correo valido, debe contener un @");
            return;
        }

        if (usuario == null || usuario.isEmpty()) {
            System.out.println("Ingrese un nombre de usuario");
            alertaSignUp("Tiene que ingresar un Nombre de usuario Valido");
            return;
        }

        if (contrasena == null || contrasena.isEmpty()) {
            System.out.println("Ingrese una contraseña");
            alertaSignUp("Tiene que ingresar una contraseña");
            return;
        }

        if (confirmacion == null || confirmacion.isEmpty()) {
            System.out.println("Confirme la contraseña");
            alertaSignUp("Tiene que ingresar la confirmacion de la contraseña");
            return;
        }

        if (!confirmacion.equals(contrasena)) {
            System.out.println("Verifique que las contraseñas sean iguales");
            alertaSignUp("Las contraseñas ingresadas deben de ser iguales");
            return;
        }

        /*ESTE VERIFICA EL USUARIO Y LA CONTRASEÑA CON EL METODO DE SIGN UP DEL SERVICIO DE AUTENTICACION
        * ADEMAS EN CASO DE QUE ESTE SEA EXITOSO DE UNA LA GUARDA EN LA BASE DE DATOS*/
        if (autenService.signUp(usuario, contrasena, correo) == false) {
            System.out.println("Usuario o contraseña invalida");
            alertaSignUp("por favor intente nuevamente");
            return;
        }

        /*MUESTRA EL MENSAJE DE EXITO*/
        exitoSignUp();
        /*CAMBIA DE ESCENA AL LOGIN*/
        sceneManager.showLogin();
    }

    /*SOLO SE CAMBIA DE ESCENA AL LOGIN*/
    @FXML
    void On_login(ActionEvent event) throws IOException {
        System.out.println("Login: Iniciando sesion");
        sceneManager.showLogin();
    }

}

