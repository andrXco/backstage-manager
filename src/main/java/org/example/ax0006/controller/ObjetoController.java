package org.example.ax0006.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.example.ax0006.entity.TipoObjeto;
import org.example.ax0006.manager.SceneManager;
import org.example.ax0006.service.ObjetoService;

import java.sql.SQLException;

public class ObjetoController {
    private final ObjetoService objetoService;
    private final SceneManager sceneManager;

    @FXML private TextField fid_referencia;
    @FXML private ComboBox<TipoObjeto> cb_tipoObjeto;
    @FXML private Label lbl_msg;

    public ObjetoController(ObjetoService objetoService, SceneManager sceneManager) {
        this.objetoService = objetoService;
        this.sceneManager = sceneManager;
    }

    @FXML
    public void initialize() {
        try {
            cb_tipoObjeto.setItems(FXCollections.observableArrayList(objetoService.obtenerTipos()));

            cb_tipoObjeto.setConverter(new StringConverter<>() {
                @Override public String toString(TipoObjeto tipo) { return tipo != null ? tipo.getTipo() : ""; }
                @Override public TipoObjeto fromString(String string) { return null; }
            });
        } catch (SQLException e) {
            lbl_msg.setText("Error al cargar tipos de objeto.");
        }
    }

    @FXML
    void On_guardarObjeto() {
        try {
            String referencia = fid_referencia.getText();
            TipoObjeto tipoSeleccionado = cb_tipoObjeto.getValue();

            if (referencia.isEmpty() || tipoSeleccionado == null) {
                lbl_msg.setText("Por favor, complete todos los campos.");
                return;
            }

            objetoService.crearObjetoConReferencia(referencia, tipoSeleccionado.getIdTipoObjeto());
            lbl_msg.setText("Objeto registrado con éxito.");
            fid_referencia.clear();
            cb_tipoObjeto.setValue(null);
        } catch (Exception e) {
            lbl_msg.setText("Error: " + e.getMessage());
        }
    }

    @FXML void On_volver() throws Exception { sceneManager.showMenu(); }
}