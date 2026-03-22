/*
 * MARTIN SANMIGUEL
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

        if(mostrando && mostrandoConfirmation){
            togglePassword();
            togglePasswordConfirmation();
        }

        if(UsuarioRepository.getInstance().buscarPorNombre(fid_Usuario.getText()) != null){
            System.out.println("El Usuario Existe, porfavor intente nuevamente");
        }

        else if(fid_ContrasenaConfirmation.getText() != "" && fid_ContrasenaConfirmation.getText().equals(fid_Contrasena.getText())){
            System.out.println("Usuario Creado Correctamente! porfavor utilice el login");
            UsuarioRepository.getInstance().guardar(new Usuario(fid_Usuario.getText(), fid_Contrasena.getText()));

        }
        else {
            System.out.println("Ingrese una contraseña porfavor o ");
            System.out.print("Verifique que las contraseñas son iguales");
            System.out.println(fid_Contrasena.getText());
            System.out.println(fid_ContrasenaConfirmation.getText());
        }
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

