package org.example.ax0006.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.ax0006.entity.AnalisisFinanciero;
import org.example.ax0006.manager.SceneManager;
import org.example.ax0006.manager.SesionManager;
import org.example.ax0006.service.AnalisisFinancieroService;

import java.io.IOException;

public class ConsultarFinanzasController {

    // =========================
    // SERVICES
    // =========================
    private AnalisisFinancieroService analisisService;
    private SceneManager sceneManager;
    private SesionManager sesion;

    // =========================
    // CONSTRUCTOR
    // =========================
    public ConsultarFinanzasController(
            AnalisisFinancieroService analisisService,
            SceneManager sceneManager,
            SesionManager sesion
    ) {

        this.analisisService = analisisService;
        this.sceneManager = sceneManager;
        this.sesion = sesion;
    }

    public ConsultarFinanzasController(AnalisisFinancieroService analisisService, SceneManager sceneManager) {
            this.analisisService = analisisService;
            this.sceneManager = sceneManager;
    }

    public ConsultarFinanzasController() {
    }

    // =========================
    // TABLA
    // =========================
    @FXML
    private TableView<AnalisisFinanciero> tablaFinanzas;

    @FXML
    private TableColumn<AnalisisFinanciero, Integer> colId;

    @FXML
    private TableColumn<AnalisisFinanciero, Integer> colPresupuesto;

    @FXML
    private TableColumn<AnalisisFinanciero, String> colEstado;

    // =========================
    // BOTONES
    // =========================
    @FXML
    private Button fid_bt_editar;

    @FXML
    private Button fid_bt_volver;

    // =========================
    // INITIALIZE
    // =========================
    @FXML
    public void initialize() {

        colId.setCellValueFactory(
                data -> new SimpleObjectProperty<>(
                        data.getValue().getIdAnalisisF()
                )
        );

        colPresupuesto.setCellValueFactory(
                data -> new SimpleObjectProperty<>(
                        data.getValue().getPresupuesto()
                )
        );

        colEstado.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().isAprobado()
                                ? "Aprobado"
                                : "Pendiente"
                )
        );

        cargarTabla();
    }

    // =========================
    // CARGAR TABLA
    // =========================
    private void cargarTabla() {

        tablaFinanzas.setItems(
                FXCollections.observableArrayList(
                        analisisService.listarAnalisis()
                )
        );
    }

    // =========================
    // EDITAR
    // =========================
    @FXML
    public void On_editar() {

        AnalisisFinanciero seleccionado =
                tablaFinanzas.getSelectionModel()
                        .getSelectedItem();

        if (seleccionado == null) {

            mostrarError(
                    "Seleccione un presupuesto"
            );

            return;
        }

        try {

            sceneManager.showAnalisisFinanciero(
                    seleccionado.getIdAnalisisF()
            );

        } catch (IOException e) {

            e.printStackTrace();

            mostrarError(
                    "No se pudo abrir el análisis"
            );
        }
    }

    // =========================
    // VOLVER
    // =========================
    @FXML
    public void On_volver() {

        try {

            sceneManager.showMenuFinanzas();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    // =========================
    // ALERTAS
    // =========================
    private void mostrarError(String msg) {

        Alert alert =
                new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Error");
        alert.setHeaderText("No se pudo continuar");
        alert.setContentText(msg);

        alert.showAndWait();
    }
}