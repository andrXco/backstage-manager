/*
* MARTIN SANMIGUEL
*/


package org.example.ax0006.Controller;

import org.example.ax0006.db.H2;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StartController extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        H2.inicializarDB(); //se llama funcion para inicializar la base de datos

        FXMLLoader fxmlLoader = new FXMLLoader(StartController.class.getResource("/org/example/ax0006/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        stage.setTitle("BACKSTAGE-MANAGER.");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

