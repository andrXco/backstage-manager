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

public class LoginController {

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
    void On_sign_up(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/ax0006/signup.fxml")
        );

        Scene scene = new Scene(loader.load());

        Stage stage = (Stage) fid_sign_up.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void On_login(ActionEvent event) throws IOException {
        if (mostrando) {
            togglePassword();
        }

        Usuario usuarioLogin = UsuarioRepository.getInstance().buscarPorNombre(fid_Usuario.getText());

        if (usuarioLogin == null) {
            System.out.println("Usuario no existe");
            return;
        }

        if (!usuarioLogin.getContrasena().equals(fid_Contrasena.getText())) {
            System.out.println("Contraseña incorrecta");
            return;
        }

        System.out.println("Usuario Logueado");
        System.out.println("Bienvenido " + usuarioLogin.getNombre());

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/ax0006/menu.fxml")
        );

        Scene scene = new Scene(loader.load());

        Stage stage = (Stage) fid_login.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}