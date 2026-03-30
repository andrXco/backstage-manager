package org.example.ax0006.Manager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.ax0006.Controller.LoginController;
import org.example.ax0006.Controller.MenuController;
import org.example.ax0006.Controller.SignUpController;
import org.example.ax0006.Controller.AdminUsuariosController;
import org.example.ax0006.Repository.UsuarioRepository;

import java.io.IOException;


/*ESTA CLASE ES LA QUE HACE TODO EL CAMBIO DE ESCENAS POSIBLE AL ENVIAR LA INFORMACION Y AL SIMPLIFICAR EL CAMBIAR DE ESCENA*/
public class SceneManager {

    /*ATRIBUTOS*/
    private Stage stage;
    private ContextManager context;

    /*CONSTRUCTOR*/
    public SceneManager(Stage stage, ContextManager context) {
        this.stage = stage;
        this.context = context;
    }

    /*METODO PARA MOSTRAR EL LOGIN*/
    public void showLogin() throws IOException {
        LoginController loginController = new LoginController(this, context.getAutenService(), context.getSesion());
        loadScene("/org/example/ax0006/login.fxml", loginController);
    }

    /*METODO PARA MOSTRAR EL SIGN UP*/
    public void showSignUp() throws IOException {
        SignUpController signUpControl = new SignUpController(this, context.getAutenService(), context.getSesion());
        loadScene("/org/example/ax0006/signup.fxml", signUpControl);
    }

    /*METOOD PARA MOSTRAR EL MENU*/
    public void showMenu() throws IOException{
        MenuController menuControl = new MenuController(this, context.getSesion());
        loadScene("/org/example/ax0006/menu.fxml", menuControl);
    }

    //metodo para mostrar pantalla de administracion de usuarios.
    public void showAdminUsuarios() throws IOException {
        AdminUsuariosController controller = new AdminUsuariosController(
                context.getSesion(), context.getRolService(), this
        );
        loadScene("/org/example/ax0006/adminUsuarios.fxml", controller);
    }


    /*METODO PARA NO REPETIR ESTO COMO MIL VECES Y HACER QUE EL CAMBIO DE ESCENA SE VEA MAS LIMPIO*/
    private void loadScene(String fxml, Object controller) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(fxml)
        );

        loader.setController(controller);

        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
    }


}