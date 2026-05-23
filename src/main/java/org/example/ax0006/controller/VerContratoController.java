package org.example.ax0006.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.example.ax0006.entity.Clausula;
import org.example.ax0006.entity.Contrato;
import org.example.ax0006.manager.SceneManager;
import org.example.ax0006.manager.SesionManager;
import org.example.ax0006.service.ContratoService;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.List;

public class VerContratoController {

    private SceneManager sceneManager;
    private ContratoService contratoService;
    private SesionManager sesion;

    public VerContratoController(SceneManager sceneManager, ContratoService contratoService, SesionManager sesion) {
        this.sceneManager = sceneManager;
        this.contratoService = contratoService;
        this.sesion = sesion;
    }

    @FXML
    private Label lblFecha;

    @FXML
    private ListView<String> listClausulas;

    @FXML
    private Button btnAprobar;

    @FXML
    private Button btnRechazar;

    @FXML
    public void initialize() {

        Integer idContrato = sesion.getIdContratoTemporal();

        boolean esAdmin =
                sesion.getUsuarioActual().getIdRol() == 1;

        btnAprobar.setVisible(esAdmin);
        btnAprobar.setManaged(esAdmin);

        btnRechazar.setVisible(esAdmin);
        btnRechazar.setManaged(esAdmin);

        if (idContrato == null) {
            mostrarError("No hay contrato seleccionado");
            return;
        }

        Contrato contrato = contratoService.obtenerContratoCompleto(idContrato);

        if (contrato == null) {
            mostrarError("No se encontró el contrato");
            return;
        }

        // Mostrar fecha
        lblFecha.setText(contrato.getFecha().toString());

        // Mostrar cláusulas
        List<Clausula> clausulas = contrato.getClausulas();

        if (clausulas != null) {
            for (Clausula cl : clausulas) {
                listClausulas.getItems().add(cl.getClausula());
            }
        }
    }

    @FXML
    public void On_volver() {
    try {

        String origen = sesion.getPantallaOrigen();

        if ("programados".equals(origen)) {
            sceneManager.showConciertosProgramados();
        } else {
            sceneManager.showConsultarSolicitudes();
        }

    } catch (IOException e) {
        e.printStackTrace();
    }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    void On_AprobarFirma() {

        Integer idContrato = sesion.getIdContratoTemporal();

        contratoService.aprobarFirma(idContrato);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Firma");
        alert.setHeaderText("Contrato aprobado");
        alert.setContentText(
                "La firma fue aprobada correctamente."
        );

        alert.showAndWait();
    }

    @FXML
    void On_RechazarFirma() {

        Integer idContrato = sesion.getIdContratoTemporal();

        contratoService.rechazarFirma(idContrato);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Firma");
        alert.setHeaderText("Contrato rechazado");
        alert.setContentText(
                "La firma fue rechazada."
        );

        alert.showAndWait();
    }

}