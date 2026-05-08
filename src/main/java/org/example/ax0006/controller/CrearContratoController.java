package org.example.ax0006.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;


import org.example.ax0006.entity.Clausula;
import org.example.ax0006.entity.Concierto;
import org.example.ax0006.entity.Contrato;
import org.example.ax0006.manager.SceneManager;
import org.example.ax0006.manager.SesionManager;
import org.example.ax0006.service.ContratoService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CrearContratoController {

    // =========================
    // COMPONENTES FXML
    // =========================
    @FXML
    private DatePicker dateFecha;

    @FXML
    private TextArea txtClausula;

    @FXML
    private ListView<String> listClausulas;

    // =========================
    // ATRIBUTOS
    // =========================
    private ObservableList<String> listaClausulas = FXCollections.observableArrayList();
    private List<Clausula> clausulas = new ArrayList<>();

    private SceneManager sceneManager;
    private SesionManager sesion;
    private ContratoService contratoService;

    // =========================
    // CONSTRUCTOR
    // =========================
    public CrearContratoController(SceneManager sceneManager, ContratoService contratoService, SesionManager sesion) {
    this.sceneManager = sceneManager;
    this.contratoService = contratoService;
    this.sesion = sesion;
    }

    // =========================
    // INIT (cuando carga la vista)
    // =========================
    @FXML
    public void initialize() {
        listClausulas.setItems(listaClausulas);
    }

    // =========================
    // AGREGAR CLAUSULA
    // =========================
    @FXML
    public void agregarClausula() {

        String texto = txtClausula.getText();

        if (texto == null || texto.trim().isEmpty()) {
            mostrarAlerta("Error", "La cláusula no puede estar vacía");
            return;
        }

        // Crear objeto cláusula (sin id aún)
        Clausula clausula = new Clausula();
        clausula.setClausula(texto);

        clausulas.add(clausula);
        listaClausulas.add(texto);

        txtClausula.clear();
    }

    // =========================
    // GUARDAR CONTRATO
    // =========================
    @FXML
    public void guardarContrato() {
        Concierto conciertoTemp = sesion.getConciertoTemporal();
        LocalDate fecha = dateFecha.getValue();

        if (fecha == null) {
            mostrarAlerta("Error", "Debe seleccionar una fecha");
            return;
        }
        
        if (conciertoTemp != null 
        && conciertoTemp.getHorario() != null 
        && conciertoTemp.getHorario().getFechaInicio() != null) {
            if (fecha.isAfter(conciertoTemp.getHorario().getFechaInicio())) {
                mostrarAlerta("Error", "La fecha del contrato debe ser anterior a la del concierto");
                return;
            }
        }

        if (clausulas.isEmpty()) {
            mostrarAlerta("Error", "Debe agregar al menos una cláusula");
            return;
        }

        // Crear contrato
        Contrato contrato = new Contrato();
        contrato.setFecha(fecha);
        contrato.setClausulas(clausulas);

        int idContrato = contratoService.crearContrato(contrato);
       
        if (idContrato != 0) {

        sesion.setIdContratoTemporal(idContrato); 
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Contrato creado");
        alert.setHeaderText("¡Registro exitoso!");
        alert.setContentText("El ID del contrato es: " + idContrato);
        alert.showAndWait();

        try {
            sceneManager.showCrearConcierto(); 
        } catch (Exception e) {
            e.printStackTrace();
        }
}
        else {
            mostrarAlerta("Error", "No se pudo crear el contrato");
        }

    }

    // =========================
    // CANCELAR
    // =========================
    @FXML
    public void cancelar() {
        try {
            sceneManager.showCrearConcierto(); // o a donde quieras volver
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