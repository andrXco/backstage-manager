package org.example.ax0006.Manager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.ax0006.Controller.*;
import org.example.ax0006.Repository.InventarioObjetoRepository;
import org.example.ax0006.Repository.InventarioRepository;
import org.example.ax0006.Repository.TipoObjetoRepository;
import org.example.ax0006.Repository.UsuarioRepository;
import org.example.ax0006.Service.InventarioObjetoService;
import org.example.ax0006.Service.InventarioService;
import org.example.ax0006.Service.consultarInventarioService;
import org.example.ax0006.Service.crearTipoObjetoService;
import org.example.ax0006.db.H2;

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
        MenuController menuControl = new MenuController(this, context.getSesion(), context.getConciertoService());
        loadScene("/org/example/ax0006/menu.fxml", menuControl);
    }

    //metodo para mostrar pantalla de administracion de usuarios.
    public void showAdminUsuarios() throws IOException {
        AdminUsuariosController controller = new AdminUsuariosController(
                context.getSesion(),
                context.getRolService(),
                this,
                context.getConciertoService(),
                context.getStaffService()
        );
        loadScene("/org/example/ax0006/adminUsuarios.fxml", controller);
    }

    //Metodo para mostrar pantalla de perfil del usuario
    public void showProfile() throws IOException {
        ProfileController profileController = new ProfileController(
                this,
                context.getSesion(),
                context.getProfileService()
        );
        loadScene("/org/example/ax0006/profile.fxml", profileController);
    }

    public void showEditProfile() throws IOException {
        EditProfileController editProfileController = new EditProfileController(
                this,
                context.getSesion(),
                context.getProfileService()
        );
        loadScene("/org/example/ax0006/editprofile.fxml", editProfileController);
    }

    /*metodo para mostra la pantalla de mostrar los conciertos no programados*/
    public void showChangePassword() throws IOException {
        ChangePasswordController changePasswordController = new ChangePasswordController(
                this,
                context.getSesion(),
                context.getProfileService()
        );
        loadScene("/org/example/ax0006/changepassword.fxml", changePasswordController);
    }

    public void showConsultarSolicitudes() throws IOException{
        ConsultarSolicitudesController consultarSolicitudesController = new ConsultarSolicitudesController(context.getSesion(), context.getConciertoService(), this);
        loadScene("/org/example/ax0006/consultarsolicitudes.fxml", consultarSolicitudesController);
    }

    public void showCrearConcierto() throws  IOException{
        CrearConciertoController crearConciertoController = new CrearConciertoController(context.getSesion(), context.getConciertoService(), this);
        loadScene("/org/example/ax0006/crearconcierto.fxml", crearConciertoController);
    }

    public void showConciertosProgramados() throws  IOException{
        ConciertosProgramadosController conciertosProgramadosController = new ConciertosProgramadosController(context.getSesion(), context.getConciertoService(), this);
        loadScene("/org/example/ax0006/verconciertosprogramados.fxml", conciertosProgramadosController);
    }

    public void showMenuConcierto() throws IOException{
        MenuConciertoController menuConciertoController = new MenuConciertoController(this, context.getSesion());
        loadScene("/org/example/ax0006/menuconcierto.fxml", menuConciertoController);
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

    public void showCrearInventario() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/ax0006/crearInventario.fxml"));

        crearInventarioController controller =
                new crearInventarioController(
                        new InventarioService(new InventarioRepository(new H2())), this
                );

        loader.setController(controller);

        Parent root = loader.load();
        stage.getScene().setRoot(root);
    }

    public void showCrearTipoObjeto() throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/ax0006/crearTipoObjeto.fxml")
        );

        crearTipoObjetoController controller =
                new crearTipoObjetoController(
                        new crearTipoObjetoService(new TipoObjetoRepository(new H2())), this
                );

        loader.setController(controller);

        Parent root = loader.load();

        stage.getScene().setRoot(root);
    }
    public void showAsignarObjeto() throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/ax0006/asignarObjeto.fxml")
        );

        asignarObjetoController controller =
                new asignarObjetoController(
                        new InventarioObjetoService(
                                new InventarioObjetoRepository(new H2())
                        ), this
                );

        loader.setController(controller);

        Parent root = loader.load();

        stage.getScene().setRoot(root);
    }

    public void showConsultarInventario() throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/ax0006/consultarInventario.fxml")
        );

        consultarInventarioController controller =
                new consultarInventarioController(
                        new consultarInventarioService(
                                new InventarioRepository(new H2())
                        ), this
                );

        loader.setController(controller);

        Parent root = loader.load();

        stage.getScene().setRoot(root);
    }
}