package org.example.ax0006.Controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.ax0006.Repository.*;
import org.example.ax0006.Service.InventarioService;
import org.example.ax0006.Service.autenticacionService;
import org.example.ax0006.Service.crearTipoObjetoService;
import org.example.ax0006.db.H2;

import java.io.IOException;

//ver base de datos:
// PAGINA: http://localhost:8082
// URL: jdbc:h2:./data/eventosdb

public class startController extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        // BASE DE DATOS
        H2 h2 = new H2();
        h2.inicializarDB();

        // REPOSITORIOS
        usuarioRepository usuarioRepo = new usuarioRepository(h2);
        TipoObjetoRepository tipoObjetoRepository = new TipoObjetoRepository(h2);
        InventarioRepository inventarioRepository = new InventarioRepository(h2);
        InventarioHorarioRepository inventarioHorarioRepository = new InventarioHorarioRepository(h2);
        InventarioObjetoRepository inventarioObjetoRepository = new InventarioObjetoRepository(h2);

        // SERVICIOS
        autenticacionService authService = new autenticacionService(usuarioRepo);
        crearTipoObjetoService authService1 = new crearTipoObjetoService(tipoObjetoRepository);
        InventarioService authService2 = new InventarioService(inventarioRepository);

        // SE CARGA LA PRIMERA VISTA
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/ax0006/login.fxml")
        );

        // SE CARGA EL CONTROLADOR DE LOGIN
        loginController controller = new loginController(usuarioRepo);

        loader.setController(controller);

        Scene scene = new Scene(loader.load());
        stage.setTitle("BACKSTAGE-MANAGER");
        stage.setScene(scene);

        /*METODO PARA QUE EL PROGRAMA MUERA CUANDO SE CIERRA LA VENTANA*/
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0); // asegura cerrar H2 también
        });

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