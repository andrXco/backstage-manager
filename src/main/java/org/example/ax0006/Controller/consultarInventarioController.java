package org.example.ax0006.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.ax0006.Entity.Inventario;
import org.example.ax0006.Manager.SceneManager;
import org.example.ax0006.Service.consultarInventarioService;

import java.io.IOException;

public class consultarInventarioController {
    private SceneManager sceneManager;
    private consultarInventarioService service;

    public consultarInventarioController(consultarInventarioService service, SceneManager sceneManager) {
        this.service = service;
        this.sceneManager = sceneManager;
    }

    @FXML private TextField ii_idInventario;
    @FXML private Label lbl_resultado;
    @FXML private Button bt_buscar;
    @FXML private Button bt_volver;

    @FXML
    void on_bt_buscar(ActionEvent event) {
        try {
            int id = Integer.parseInt(ii_idInventario.getText());

            String detalle = service.obtenerDetalle(id);

            lbl_resultado.setText(detalle);

        } catch (Exception e) {
            lbl_resultado.setText("Error en datos");
        }
    }

    @FXML
    void on_bt_volver(ActionEvent event) throws IOException {
        sceneManager.showMenu();
    }
}