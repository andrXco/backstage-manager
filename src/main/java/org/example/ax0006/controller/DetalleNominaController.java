package org.example.ax0006.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;

import org.example.ax0006.entity.Nomina;
import org.example.ax0006.entity.Usuario;

import org.example.ax0006.manager.SceneManager;
import org.example.ax0006.manager.SesionManager;

import org.example.ax0006.service.StaffService;

import java.io.IOException;
import javafx.event.ActionEvent;

public class DetalleNominaController {

    @FXML private Label lblNombre;
    @FXML private Label lblRol;
    @FXML private Label lblHoras;
    @FXML private Label lblExtra;
    @FXML private Label lblTarifa;
    @FXML private Label lblTotal;
    @FXML private Label lblEstado;

    private SceneManager sceneManager;
    private SesionManager sesion;
    private StaffService staffService;

    public DetalleNominaController() {}

    public DetalleNominaController(SceneManager sceneManager,
                                   SesionManager sesion,
                                   StaffService staffService) {

        this.sceneManager = sceneManager;
        this.sesion = sesion;
        this.staffService = staffService;
    }

    @FXML
    public void initialize() {}

    public void cargarDatos() {

        Nomina nomina = sesion.getNominaSeleccionada();

        if (nomina == null) {
            return;
        }

        Usuario usuario = staffService.listarEmpleados().stream()
                .filter(u -> u.getIdUsuario() == nomina.getIdUsuario())
                .findFirst()
                .orElse(null);

        String nombre = usuario != null ? usuario.getNombre() : "Desconocido";

        lblNombre.setText("Usuario: " + nombre);
        lblRol.setText("Trabajador");
        lblHoras.setText("Horas trabajadas: " + nomina.getHorasTrabajadas());
        lblExtra.setText("Horas extra: " + nomina.getHorasExtra());
        lblTarifa.setText("Tarifa: $" + String.format("%,.0f", nomina.getTarifaPorHora()));
        lblTotal.setText("Tarifa total: $" + String.format("%,.0f", nomina.getTotal()));
        lblEstado.setText("Estado: " + nomina.getEstado());
    }

    @FXML
    void On_volver(ActionEvent event) {
        try {
            sceneManager.showNomina();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void On_EnviarFirma() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setHeaderText("Contrato enviado");
        alert.setContentText("El contrato fue enviado al manager para firma");

        alert.showAndWait();
    }
}
