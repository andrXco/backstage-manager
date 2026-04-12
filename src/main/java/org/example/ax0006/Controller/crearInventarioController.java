package org.example.ax0006.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.ax0006.Entity.Horario;
import org.example.ax0006.Service.InventarioService;
import org.example.ax0006.Manager.SceneManager;

import java.io.IOException;
import java.time.LocalTime;

public class crearInventarioController {

    @FXML private DatePicker dp_fechaInicio;
    @FXML private DatePicker dp_fechaFin;
    @FXML private TextField tf_horaInicio;
    @FXML private TextField tf_horaFin;
    @FXML private Label lbl_msg;

    private InventarioService service;
    private SceneManager sceneManager;

    public crearInventarioController(InventarioService service, SceneManager sceneManager) {
        this.service = service;
        this.sceneManager = sceneManager;
    }

    @FXML
    void on_bt_crear(ActionEvent event) {

        try {
            // 🔹 Crear objeto Horario con los datos del formulario
            Horario horario = new Horario();
            horario.setFechaInicio(dp_fechaInicio.getValue());
            horario.setFechaFin(dp_fechaFin.getValue());
            horario.setHoraInicio(LocalTime.parse(tf_horaInicio.getText()));
            horario.setHoraFin(LocalTime.parse(tf_horaFin.getText()));

            // 🔹 Llamar al nuevo método del service
            int id = service.crearInventario(horario);

            if (id != -1) {
                lbl_msg.setText("Inventario creado con horario. ID: " + id);
            } else {
                lbl_msg.setText("Error al crear inventario");
            }

        } catch (Exception e) {
            lbl_msg.setText("Datos inválidos. Verifica fechas y horas (HH:mm)");
            e.printStackTrace();
        }
    }

    @FXML
    void on_bt_volver(ActionEvent event) throws IOException {
        sceneManager.showMenu();
    }
}