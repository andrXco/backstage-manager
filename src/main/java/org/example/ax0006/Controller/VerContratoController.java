package org.example.ax0006.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.example.ax0006.Entity.Clausula;
import org.example.ax0006.Entity.Contrato;
import org.example.ax0006.Manager.SceneManager;
import org.example.ax0006.Manager.SesionManager;
import org.example.ax0006.Service.ContratoService;

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
    public void initialize() {

        Integer idContrato = sesion.getIdContratoTemporal();

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

        for (Clausula cl : clausulas) {
            listClausulas.getItems().add(cl.getClausula());
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
}