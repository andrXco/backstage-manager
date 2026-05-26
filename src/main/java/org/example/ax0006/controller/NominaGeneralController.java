package org.example.ax0006.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import org.example.ax0006.service.NominaService;

import java.util.List;

public class NominaGeneralController {

    @FXML
    private Label lblTitulo;
    @FXML
    private TableView<ResumenNominaVista> tablaResumen;
    @FXML
    private TableColumn<ResumenNominaVista, String> colRol;
    @FXML
    private TableColumn<ResumenNominaVista, Integer> colCantidad;
    @FXML
    private TableColumn<ResumenNominaVista, Double> colTotal;
    @FXML
    private Label lblTotalEvento;
    @FXML
    private Button btnCerrar;

    private NominaService nominaService;
    private int idConcierto;

    public NominaGeneralController(NominaService nominaService, int idConcierto) {
        this.nominaService = nominaService;
        this.idConcierto = idConcierto;
    }

    @FXML
    public void initialize() {
        colRol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRol())
        );

        colCantidad.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getCantidad()).asObject()
        );

        colTotal.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getTotal()).asObject()
        );

        cargarDatos();
    }

    private void cargarDatos() {
        var lista = nominaService.obtenerNominaGeneralPorEvento(idConcierto);

        var listaVista = lista.stream()
                .map(r -> new ResumenNominaVista(r.rol, r.cantidad, r.total))
                .toList();

        tablaResumen.setItems(FXCollections.observableArrayList(listaVista));

        double totalEvento = nominaService.obtenerTotalGeneralEvento(idConcierto);
        lblTotalEvento.setText(String.format("Total del Evento: $%,.0f", totalEvento));
    }

    @FXML
    void On_Cerrar(ActionEvent event) {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }

    public static class ResumenNominaVista {
        private String rol;
        private int cantidad;
        private double total;

        public ResumenNominaVista(String rol, int cantidad, double total) {
            this.rol = rol;
            this.cantidad = cantidad;
            this.total = total;
        }

        public String getRol() { return rol; }
        public int getCantidad() { return cantidad; }
        public double getTotal() { return total; }
    }
}
