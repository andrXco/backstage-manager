package org.example.ax0006.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.ax0006.entity.Concierto;
import org.example.ax0006.manager.SceneManager;
import org.example.ax0006.manager.SesionManager;
import org.example.ax0006.service.InventarioService;
import java.util.List;

public class DetallesConciertoController {
    private final SceneManager sceneManager;
    private final SesionManager sesion;
    private final InventarioService inventarioService;

    @FXML private Label lbl_nombre, lbl_aforo, lbl_horario, lbl_contrato;
    @FXML private ListView<String> lv_inventario;

    public DetallesConciertoController(SceneManager sceneManager, SesionManager sesion, InventarioService inventarioService) {
        this.sceneManager = sceneManager;
        this.sesion = sesion;
        this.inventarioService = inventarioService;
    }

    @FXML
    public void initialize() {
        Concierto c = sesion.getConciertoTemporal();
        if (c != null) {
            lbl_nombre.setText(c.getNombreConcierto());
            lbl_aforo.setText(c.getAforo() + " personas");
            lbl_contrato.setText("Contrato #" + c.getIdContrato());
            lbl_horario.setText(c.getHorario().getFechaInicio() + " (" + c.getHorario().getHoraInicio() + ") - " + c.getHorario().getFechaFin() + " (" + c.getHorario().getHoraFin() + ")");

            List<String> objetos = inventarioService.obtenerObjetosPorConcierto(c.getIdConcierto());
            if (objetos.isEmpty()) {
                lv_inventario.getItems().add("No hay objetos registrados para este concierto.");
            } else {
                lv_inventario.getItems().addAll(objetos);
            }
        }
    }

    @FXML
    void on_volver() throws Exception {
        sceneManager.showConciertosProgramados();
    }
}