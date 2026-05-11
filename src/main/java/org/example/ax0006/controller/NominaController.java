package org.example.ax0006.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;

import javafx.util.converter.DoubleStringConverter;

import org.example.ax0006.entity.Concierto;
import org.example.ax0006.entity.Nomina;
import org.example.ax0006.entity.Usuario;

import org.example.ax0006.manager.SceneManager;
import org.example.ax0006.manager.SesionManager;

import org.example.ax0006.service.ConciertoService;
import org.example.ax0006.service.NominaService;
import org.example.ax0006.service.StaffService;

import java.io.IOException;
import java.util.List;

public class NominaController {

    @FXML private ComboBox<Concierto> comboEvento;
    @FXML private TableView<Nomina> tablaNominas;
    @FXML private TableColumn<Nomina, String> colTrabajador;
    @FXML private TableColumn<Nomina, Number> colHoras;
    @FXML private TableColumn<Nomina, Number> colTarifa;
    @FXML private TableColumn<Nomina, Double> colExtra;
    @FXML private TableColumn<Nomina, Number> colTotal;
    @FXML private TableColumn<Nomina, String> colEstado;
    @FXML private Label lblTotalGeneral;
    @FXML private Button btnGenerarReporte;

    private SceneManager sceneManager;
    private SesionManager sesion;
    private ConciertoService conciertoService;
    private NominaService nominaService;
    private StaffService staffService;

    public NominaController() {}

    public NominaController(SceneManager sceneManager, SesionManager sesion,
                            ConciertoService conciertoService, NominaService nominaService,
                            StaffService staffService) {
        this.sceneManager = sceneManager;
        this.sesion = sesion;
        this.conciertoService = conciertoService;
        this.nominaService = nominaService;
        this.staffService = staffService;
    }

    @FXML
    public void initialize() {

        colTrabajador.setCellValueFactory(cellData -> {
            int idUsuario = cellData.getValue().getIdUsuario();
            Usuario u = staffService.listarEmpleados().stream()
                    .filter(emp -> emp.getIdUsuario() == idUsuario)
                    .findFirst().orElse(null);
            return new SimpleStringProperty(u != null ? u.getNombre() : "Desconocido");
        });

        colHoras.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getHorasTrabajadas()));
        colTarifa.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getTarifaPorHora()));
        colExtra.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getHorasExtra()).asObject());
        colTotal.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getTotal()));
        colEstado.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isPagado() ? "Pagado" : "Pendiente"));

        tablaNominas.setEditable(true);
        colExtra.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.DoubleStringConverter()));
        colExtra.setOnEditCommit(event -> {
            Nomina nomina = event.getRowValue();
            nominaService.actualizarHorasExtra(nomina.getIdNomina(), event.getNewValue());
            cargarNominas(comboEvento.getValue().getIdConcierto());
        });

        comboEvento.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                nominaService.generarNominaParaConcierto(newVal.getIdConcierto());
                cargarNominas(newVal.getIdConcierto());
            }
        });

        List<Concierto> conciertos = conciertoService.obtenerConciertosSolos().stream()
                .filter(Concierto::isProgramado)
                .toList();

        comboEvento.setItems(FXCollections.observableArrayList(conciertos));
    }

    private void cargarNominas(int idConcierto) {
        List<Nomina> nominas = nominaService.obtenerNominasPorConcierto(idConcierto);
        tablaNominas.setItems(FXCollections.observableArrayList(nominas));
        double total = nominaService.calcularTotalGeneral(idConcierto);
        lblTotalGeneral.setText(String.format("$%,.0f", total));
    }

    @FXML
    void On_volver(ActionEvent event) {
        try {
            sceneManager.showMenu();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void On_GenerarReporte(ActionEvent event) {
        Concierto seleccionado = comboEvento.getValue();
        if (seleccionado == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText("Seleccione un evento primero");
            alert.showAndWait();
            return;
        }
        cargarNominas(seleccionado.getIdConcierto());
    }
}