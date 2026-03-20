/*
 * MARTIN SANMIGUEL
 */


package org.example.pruebafismsd.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.pruebafismsd.Entity.Usuario;
import org.example.pruebafismsd.Repository.UsuarioRepository;

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


    //BOTTON PARA VER LA CONTRASEÑA
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

    //BOTON DE SIGN UP
    @FXML
    void On_sign_up(ActionEvent event) throws IOException {
        System.out.println("Sign Up: Crear Usuario");
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/pruebafismsd/signup.fxml")
        );

        Scene scene = new Scene(loader.load());

        Stage stage = (Stage) fid_sign_up.getScene().getWindow();
        stage.setScene(scene);
    }

    //BOTON DE LOGIN
    @FXML
    void On_login(ActionEvent event) {
        System.out.println("Login: Logear Usuario");

        if(mostrando){
            togglePassword();
        }
        Usuario UsuarioLogin = UsuarioRepository.getInstance().buscarPorNombre(fid_Usuario.getText());
        if(UsuarioLogin != null){
            if(UsuarioLogin.getContrasena().equals(fid_Contrasena.getText())){
                System.out.println("Usuario Logueado");
                System.out.println("Bienvenido " + UsuarioLogin.getNombre());
            }
        }
        else{
            System.out.println("Usuario no Existe");
        }
    }

}
