package org.example.ax0006.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import org.example.ax0006.Entity.Clausula;
import org.example.ax0006.Entity.Contrato;
import org.example.ax0006.Manager.SceneManager;
import org.example.ax0006.Service.ContratoService;

import java.util.List;

public class ConsultarContratoController {

    // =========================
    // COMPONENTES FXML
    // =========================
    @FXML
    private TextField txtIdContrato;

    @FXML
    private Label lblFecha;

    @FXML
    private ListView<String> listClausulas;

    // =========================
    // ATRIBUTOS
    // =========================
    private SceneManager sceneManager;
    private ContratoService contratoService;

    private ObservableList<String> listaClausulas = FXCollections.observableArrayList();

    // =========================
    // CONSTRUCTOR
    // =========================
    public ConsultarContratoController(SceneManager sceneManager, ContratoService contratoService) {
        this.sceneManager = sceneManager;
        this.contratoService = contratoService;
    }

    // =========================
    // INIT
    // =========================
    @FXML
    public void initialize() {
        listClausulas.setItems(listaClausulas);
    }

    // =========================
    // BUSCAR CONTRATO
    // =========================
    @FXML
    public void buscarContrato() {

        String textoId = txtIdContrato.getText();

        if (textoId == null || textoId.trim().isEmpty()) {
            mostrarAlerta("Error", "Ingrese un ID de contrato");
            return;
        }

        int id;

        try {
            id = Integer.parseInt(textoId);
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El ID debe ser un número");
            return;
        }

        Contrato contrato = contratoService.obtenerContratoCompleto(id);

        if (contrato == null) {
            mostrarAlerta("Error", "No se encontró el contrato");
            return;
        }

        // Mostrar fecha
        lblFecha.setText(contrato.getFecha().toString());

        // Mostrar cláusulas
        listaClausulas.clear();

        List<Clausula> clausulas = contrato.getClausulas();

        if (clausulas != null) {
            for (Clausula c : clausulas) {
                listaClausulas.add(c.getClausula());
            }
        }
    }

    // =========================
    // VOLVER
    // =========================
    @FXML
    public void volver() {
        try {
            sceneManager.showMenu(); // ajusta si quieres otro destino
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // ALERTAS
    // =========================
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}