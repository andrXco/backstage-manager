package org.example.ax0006.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;

import org.example.ax0006.entity.Nomina;
import org.example.ax0006.entity.Usuario;

import org.example.ax0006.manager.SesionManager;
import org.example.ax0006.manager.SceneManager;

import org.example.ax0006.service.NominaService;
import org.example.ax0006.service.StaffService;

import java.io.IOException;
import java.util.List;

public class AsignarNominaController {

    @FXML private TableView<Nomina> tablaNomina;

    @FXML private TableColumn<Nomina, String> colTrabajador;
    @FXML private TableColumn<Nomina, String> colRol;
    @FXML private TableColumn<Nomina, Double> colHoras;
    @FXML private TableColumn<Nomina, Double> colTarifa;
    @FXML private TableColumn<Nomina, String> colHorasExtra;
    @FXML private TextField txtHoras;
    @FXML private TextField txtTarifa;
    @FXML private TextField txtHorasExtra;

    private SceneManager sceneManager;
    private NominaService nominaService;
    private StaffService staffService;
    private SesionManager sesion;

    private List<Nomina> nominas;

    public AsignarNominaController(SceneManager sceneManager,
                                   SesionManager sesion,
                                   NominaService nominaService,
                                   StaffService staffService) {

        this.sceneManager = sceneManager;
        this.sesion = sesion;
        this.nominaService = nominaService;
        this.staffService = staffService;
    }

    @FXML
    public void initialize() {

        boolean esManager =
                sesion.getUsuarioActual().getIdRol() == 3;

        tablaNomina.setEditable(esManager);

        colHoras.setEditable(true);
        colTarifa.setEditable(true);

        colTrabajador.setCellValueFactory(cellData -> {

            int idUsuario = cellData.getValue().getIdUsuario();

            Usuario u = staffService.listarEmpleados()
                    .stream()
                    .filter(emp -> emp.getIdUsuario() == idUsuario)
                    .findFirst()
                    .orElse(null);

            return new SimpleStringProperty(
                    u != null ? u.getNombre() : "Desconocido"
            );
        });

        colRol.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getRol()
                )
        );

        colHoras.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(
                        cellData.getValue().getHorasTrabajadas()
                ).asObject()
        );

        colTarifa.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(
                        cellData.getValue().getTarifaPorHora()
                ).asObject()
        );

        colHorasExtra.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.valueOf(
                                data.getValue().getHorasExtra()
                        )
                )
        );

        tablaNomina.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, nuevaNomina) -> {

            if (nuevaNomina != null) {

                txtHoras.setText(
                        String.valueOf(nuevaNomina.getHorasTrabajadas())
                );

                txtTarifa.setText(
                        String.valueOf(nuevaNomina.getTarifaPorHora())
                );
            }
        });
    }

    public void cargarNominas(List<Nomina> lista) {

        this.nominas = lista;

        tablaNomina.setItems(
                FXCollections.observableArrayList(lista)
        );
    }

    @FXML
    void On_Guardar(ActionEvent event) {

        for (Nomina n : nominas) {

            nominaService.actualizarHorasYTarifa(
                    n.getIdNomina(),
                    n.getHorasTrabajadas(),
                    n.getTarifaPorHora(),
                    n.getHorasExtra()
            );
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Cambios guardados");
        alert.showAndWait();

        try {
            sceneManager.showNomina();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void On_AplicarCambios(ActionEvent event) {

        Nomina nomina = tablaNomina.getSelectionModel().getSelectedItem();

        if (nomina == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Seleccione una nómina");
            alert.showAndWait();
            return;
        }

        try {

            double horas = Double.parseDouble(txtHoras.getText());
            double tarifa = Double.parseDouble(txtTarifa.getText());
            double horasExtra = Double.parseDouble(txtHorasExtra.getText());
            double total = (horas * tarifa) + (horasExtra * tarifa);

            nomina.setHorasTrabajadas(horas);
            nomina.setTarifaPorHora(tarifa);
            nomina.setHorasExtra(horasExtra);
            nomina.setTotal(total);

            tablaNomina.refresh();

        } catch (Exception e) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Valores inválidos");
            alert.showAndWait();
        }
    }

    @FXML
    void On_Volver(ActionEvent event) {

        try {
            sceneManager.showMenu();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}