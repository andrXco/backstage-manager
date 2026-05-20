package org.example.ax0006.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import org.example.ax0006.entity.Concierto;
import org.example.ax0006.manager.SceneManager;
import org.example.ax0006.manager.SesionManager;
import org.example.ax0006.service.ConciertoService;

import java.io.IOException;

public class SeleccionarConciertoFinanzasController {

    // =========================
    // SERVICES
    // =========================
    private ConciertoService conciertoService;
    private SceneManager sceneManager;
    private SesionManager sesion;

    // =========================
    // CONSTRUCTOR
    // =========================
    public SeleccionarConciertoFinanzasController(
            ConciertoService conciertoService,
            SceneManager sceneManager,
            SesionManager sesion
    ) {

        this.conciertoService = conciertoService;
        this.sceneManager = sceneManager;
        this.sesion = sesion;
    }

    public SeleccionarConciertoFinanzasController(

        ConciertoService conciertoService,
        SesionManager sesion,
        SceneManager sceneManager
) {

    this.conciertoService = conciertoService;
    this.sesion = sesion;
    this.sceneManager = sceneManager;
}

    // =========================
    // TABLA
    // =========================
    @FXML
    private TableView<Concierto> tablaConciertos;

    @FXML
    private TableColumn<Concierto, Integer> colId;

    @FXML
    private TableColumn<Concierto, String> colNombre;

    @FXML
    private TableColumn<Concierto, Integer> colAforo;

    @FXML
    private TableColumn<Concierto, String> colEstado;

    // =========================
    // BOTONES
    // =========================
    @FXML
    private Button fid_bt_gestionar;

    @FXML
    private Button fid_bt_volver;

    // =========================
    // INITIALIZE
    // =========================
    @FXML
    public void initialize() {

        colId.setCellValueFactory(
                data -> new SimpleObjectProperty<>(
                        data.getValue().getIdConcierto()
                )
        );

        colNombre.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getNombreConcierto()
                )
        );

        colAforo.setCellValueFactory(
                data -> new SimpleObjectProperty<>(
                        data.getValue().getAforo()
                )
        );

        colEstado.setCellValueFactory(
                data -> new SimpleStringProperty(

                        data.getValue().isProgramado()
                                ? "Programado"
                                : "Pendiente"
                )
        );

        cargarTabla();
    }

    // =========================
    // CARGAR TABLA
    // =========================
    private void cargarTabla() {

        tablaConciertos.setItems(

                FXCollections.observableArrayList(
                        conciertoService.obtenerConciertosSolos()
                )
        );
    }

    // =========================
    // GESTIONAR FINANZAS
    // =========================
    @FXML
    public void On_gestionarFinanzas() {

        Concierto seleccionado =

                tablaConciertos.getSelectionModel()
                        .getSelectedItem();

        if (seleccionado == null) {

            mostrarError(
                    "Seleccione un concierto"
            );

            return;
        }

        // GUARDAR CONCIERTO ACTUAL
        sesion.setConciertoActual(
                seleccionado
        );

        try {

            // SI YA TIENE ANALISIS
            if (seleccionado.getAnalisis() != null) {
                sceneManager.showAnalisisFinanciero(
                        seleccionado.getAnalisis()
                                .getIdAnalisisF()
                );

            } 
            else {

                sceneManager.showAnalisisFinanciero();
            }

        } catch (IOException e) {

            e.printStackTrace();

            mostrarError(
                    "No se pudo abrir finanzas"
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