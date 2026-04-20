package org.example.ax0006.Manager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.ax0006.Controller.*;

import java.io.IOException;

public class SceneManager {

    private Stage stage;
    private ContextManager context;

    public SceneManager(Stage stage, ContextManager context) {
        this.stage = stage;
        this.context = context;
    }

    public void showLogin() throws IOException {
        LoginController loginController = new LoginController(this, context.getAutenService(), context.getSesion());
        loadScene("/org/example/ax0006/login.fxml", loginController);
    }

    public void showSignUp() throws IOException {
        SignUpController signUpControl = new SignUpController(this, context.getAutenService(), context.getSesion());
        loadScene("/org/example/ax0006/signup.fxml", signUpControl);
    }

    public void showMenu() throws IOException{
        MenuController menuControl = new MenuController(this, context.getSesion(), context.getConciertoService());
        loadScene("/org/example/ax0006/menu.fxml", menuControl);
    }

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

    public void showCrearInventario() throws IOException {

        crearInventarioController controller = new crearInventarioController(
                context.getInventarioService(),
                context.getInventarioObjetoService(),
                context.getCrearTipoObjetoService(),
                this
        );

        loadScene("/org/example/ax0006/crearInventario.fxml", controller);
    }

    public void showCrearTipoObjeto() throws IOException {
        CrearObjetoInventarioController controller = new CrearObjetoInventarioController(
                context.getCrearTipoObjetoService(),
                this
        );
        loadScene("/org/example/ax0006/crearTipoObjeto.fxml", controller);
    }

    public void showConsultarInventario() throws IOException {
        consultarInventarioController controller = new consultarInventarioController(
                context.getConsultarInventarioService(),
                this
        );
        loadScene("/org/example/ax0006/consultarInventario.fxml", controller);
    }

    private void loadScene(String fxml, Object controller) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(fxml)
        );

        loader.setController(controller);

        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
    }

}