package org.example.ax0006.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import org.example.ax0006.entity.Concierto;
import org.example.ax0006.manager.SceneManager;
import org.example.ax0006.manager.SesionManager;
import org.example.ax0006.service.AnalisisFinancieroService;
import org.example.ax0006.service.ConciertoService;

import java.io.IOException;

public class SeleccionarConciertoFinanzasController {

    // =========================
    // SERVICES
    // =========================
    private ConciertoService conciertoService;
    private AnalisisFinancieroService analisisService;
    private SceneManager sceneManager;
    private SesionManager sesion;

    // =========================
    // CONSTRUCTOR
    // =========================
    public SeleccionarConciertoFinanzasController(
            ConciertoService conciertoService,
            AnalisisFinancieroService analisisService,
            SesionManager sesion,
            SceneManager sceneManager
    ) {
        this.conciertoService = conciertoService;
        this.analisisService = analisisService;
        this.sesion = sesion;
        this.sceneManager = sceneManager;
    }

    // =========================
    // COMBO BOX
    // =========================
    @FXML
    private ComboBox<Concierto> comboConciertos;

    // =========================
    // INITIALIZE
    // =========================
    @FXML
    public void initialize() {

        comboConciertos.setItems(
                FXCollections.observableArrayList(
                        conciertoService.obtenerConciertosSolos()
                )
        );

        // Mostrar nombre en el combo
        comboConciertos.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Concierto c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getNombreConcierto());
            }
        });

        comboConciertos.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Concierto c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getNombreConcierto());
            }
        });
    }

    // =========================
    // CONTINUAR
    // =========================
    @FXML
    public void On_continuar() {

        Concierto seleccionado =
                comboConciertos.getValue();

        if (seleccionado == null) {
            mostrarError("Seleccione un concierto");
            return;
        }

        sesion.setConciertoActual(seleccionado);

        try {

            if (seleccionado.getAnalisis() != null) {

                sceneManager.showAnalisisFinanciero(
                        seleccionado.getAnalisis().getIdAnalisisF()
                );

            } else {

                int nuevoId =
                        analisisService.crearPresupuesto(1);

                conciertoService.asignarPresupuesto(
                        seleccionado.getIdConcierto(),
                        nuevoId
                );

                sceneManager.showAnalisisFinanciero(nuevoId);
            }

        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("No se pudo abrir finanzas");
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

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("No se pudo continuar");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}