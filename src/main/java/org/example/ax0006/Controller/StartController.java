package org.example.ax0006.Controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.ax0006.Manager.ContextManager;
import org.example.ax0006.Manager.SesionManager;
import org.example.ax0006.Repository.RolRepository;
import org.example.ax0006.Repository.UsuarioRepository;
import org.example.ax0006.Service.AutenticacionService;
import org.example.ax0006.Manager.SceneManager;
import org.example.ax0006.db.H2;
import org.example.ax0006.Repository.RolRepository;
import org.example.ax0006.Service.RolService;
import java.io.IOException;

//ver base de datos:
// PAGINA: http://localhost:8082
// URL: jdbc:h2:./data/eventosdb

public class StartController extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        // BASE DE DATOS
        H2 h2 = new H2();
        h2.inicializarDB();

        // REPOSITORIOS
        UsuarioRepository usuarioRepo = new UsuarioRepository(h2);
        RolRepository rolRepo = new RolRepository(h2);

        // SERVICIOS
        AutenticacionService autenService = new AutenticacionService(usuarioRepo);
        RolService rolService = new RolService(rolRepo, usuarioRepo);

        // MANAGERS
        SesionManager sesion = new SesionManager();
        ContextManager context = new ContextManager(autenService,rolService, sesion, h2);
        SceneManager sceneManager = new SceneManager(stage, context);

        /*METODO PARA QUE EL PROGRAMA MUERA CUANDO SE CIERRA LA VENTANA*/
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0); // asegura cerrar H2 también
        });

        /*SE REALIZA DE ESTA MANERA PARA QUE EL PROGRAMA NO MUERA EN CASO DE UNA EXCEPCION*/
        try {
            /*CAMBIA DE ESCENA AL LOGIN*/
            sceneManager.showLogin();
        } catch (IOException e) {
            e.printStackTrace();
        }

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    /*
    * CAMBIOS PRINCIPALES MARTIN SANMIGUEL:
    * 1. LOS CONTROLADORES AHORA TIENEN CONSTRUCTOR
    * 2. SE CAMBIA DE ESCENA DE FORMA DIFERENTE, PERMITIENDO LA INYECCION DE DEPENDENCIAS SIN UTILIZAR SINGLETONS (COMO EL PROFE NOS DIJO)
    * 3. AHORA HAY VARIAS CLASES QUE SE INICIALIZAN EN EL MAIN (OSEA EN EL startController)
    * 4. AHORA LAS CLASES TIENEN LOS ATRIBUTOS DE REPOSITORY Y SERVICE PARA PERMITIR LA INYECCION DE DEPENDENCIAS
    * 5. CAMBIOS MENORES AL POM.XML
    * 6. LOS ARCHIVOS .FXML, NO TIENEN CONTROLADOR POR DEFECTO... OSEA EN EL SCENE BUILDER NO SE LE PONE CONTROLADOR, ATRAVEZ DEL CODIGO SE LE ASIGNA
    * 7. SE AGREGAR POP UPS PARA AVISAR DE LOS ERRORES Y EXITOS, ADEMAS SE DOCUMENTAN BIEN LOS METODOS
    * 8. SE AÑADE UNA FUNCION QUE TERMINA EL PROOGRAMA CUANDO SE CIERRA LA INTERFAZ GRAFICA
    * */

}