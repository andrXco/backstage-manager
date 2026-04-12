package org.example.ax0006.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.ax0006.Manager.SceneManager;
import org.example.ax0006.Service.crearTipoObjetoService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class crearTipoObjetoController {

    public Button bt_crear;
    private crearTipoObjetoService service;
    private SceneManager sceneManager;
    @FXML
    private Button bt_volver;

    public crearTipoObjetoController(crearTipoObjetoService service, SceneManager sceneManager) {
        this.service = service;
        this.sceneManager = sceneManager;
    }

    @FXML private TextField ii_nombre;
    @FXML private Label lbl_msg;

    @FXML
    void on_bt_crear(ActionEvent event) {
        String nombre = ii_nombre.getText();

        if (nombre.isEmpty()) {
            lbl_msg.setText("Nombre vacío");
            return;
        }

        int id = service.crearTipoObjeto(ii_nombre.getText());
        lbl_msg.setText("Tipo creado con ID: "+id);
        ii_nombre.clear();
    }


    @FXML
    void on_bt_volver(ActionEvent event) throws IOException {
        sceneManager.showMenu();
    }
}