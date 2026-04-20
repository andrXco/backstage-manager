package org.example.ax0006.Controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.example.ax0006.Manager.SceneManager;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class consultarInventarioController implements Initializable {
    private SceneManager sceneManager;
    private consultarInventarioService service;

    @FXML private ComboBox<Integer> cb_inventarios; // Cambiado de TextField a ComboBox
    @FXML private Label lbl_resultado;
    @FXML private Button bt_buscar;
    @FXML private Button bt_volver;

    public consultarInventarioController(consultarInventarioService service, SceneManager sceneManager) {
        this.service = service;
        this.sceneManager = sceneManager;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Cargar los IDs al iniciar la pantalla
        List<Integer> ids = service.listarIdsInventarios();
        cb_inventarios.setItems(FXCollections.observableArrayList(ids));
    }

    @FXML
    void on_bt_buscar(ActionEvent event) {
        Integer idSeleccionado = cb_inventarios.getValue();

        if (idSeleccionado == null) {
            lbl_resultado.setText("Por favor, seleccione un ID de la lista.");
            return;
        }

        try {
            String detalle = service.obtenerDetalle(idSeleccionado);
            lbl_resultado.setText(detalle);
        } catch (Exception e) {
            lbl_resultado.setText("Error al recuperar los detalles.");
            e.printStackTrace();
        }
    }

    @FXML
    void on_bt_volver(ActionEvent event) throws IOException {
        sceneManager.showMenu();
    }
}