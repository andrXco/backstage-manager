/*
 * MARTIN SANMIGUEL
 * ANDRES CORTES
 */



package org.example.ax0006.Controller;



import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.ax0006.Entity.Usuario;
import org.example.ax0006.Repository.UsuarioRepository;

import java.io.IOException;

public class SignUpControler {

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

    @FXML
    void On_crear_usuario(ActionEvent event) {

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
            return;
        }

        if (!correo.contains("@")) {
            System.out.println("El correo debe contener una @");
            return;
        }

        if (usuario == null || usuario.isEmpty()) {
            System.out.println("Ingrese un nombre de usuario");
            return;
        }

        if (UsuarioRepository.getInstance().buscarPorNombre(usuario) != null) {
            System.out.println("El usuario ya existe, por favor intente nuevamente");
            return;
        }

        if (contrasena == null || contrasena.isEmpty()) {
            System.out.println("Ingrese una contraseña");
            return;
        }

        if (confirmacion == null || confirmacion.isEmpty()) {
            System.out.println("Confirme la contraseña");
            return;
        }

        if (!confirmacion.equals(contrasena)) {
            System.out.println("Verifique que las contraseñas sean iguales");
            return;
        }

        System.out.println("Usuario creado correctamente. Por favor utilice el login");
        UsuarioRepository.getInstance().guardar(new Usuario(usuario, contrasena, correo));
    }

    @FXML
    void On_login(ActionEvent event) throws IOException {

        System.out.println("Login: Iniciando sesion");

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/ax0006/login.fxml")
        );

        Scene scene = new Scene(loader.load());

        Stage stage = (Stage) fid_sign_up.getScene().getWindow();
        stage.setScene(scene);
    }

}

