package org.example.ax0006.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.ax0006.entity.Concierto;
import org.example.ax0006.manager.SceneManager;
import org.example.ax0006.manager.SesionManager;
import org.example.ax0006.service.ConciertoService;
import org.example.ax0006.service.AnalisisFinancieroService;

import java.io.IOException;
import java.util.List;

public class ReportesGerencialesController {

    private final SceneManager sceneManager;
    private final SesionManager sesion;
    private final ConciertoService conciertoService;
    private final AnalisisFinancieroService analisisService;

    public ReportesGerencialesController(
            SceneManager sceneManager,
            SesionManager sesion,
            ConciertoService conciertoService,
            AnalisisFinancieroService analisisService
    ) {
        this.sceneManager = sceneManager;
        this.sesion = sesion;
        this.conciertoService = conciertoService;
        this.analisisService = analisisService;
    }
    // =========================
    // UI
    // =========================

    @FXML private Button bt_exportar;
    @FXML private Button bt_rendimiento;
    @FXML private Button bt_generarReporte;
    @FXML private Button bt_artistaRentable;
    @FXML private Button bt_historial;
    @FXML private Button bt_volver;

    @FXML private ComboBox<Concierto> combo_eventos;

    @FXML private TextArea txt_resumenEvento;

    @FXML private Label lbl_ingresosTotales;
    @FXML private Label lbl_eventosActivos;
    @FXML private Label lbl_artistaRentable;
    @FXML private Label lbl_reportesGenerados;

    // =========================
    // INIT
    // =========================

    @FXML
    public void initialize() {

        List<Concierto> conciertos = conciertoService.obtenerConciertosSolos();
        combo_eventos.getItems().setAll(conciertos);

        combo_eventos.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Concierto c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getNombreConcierto());
            }
        });

        combo_eventos.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Concierto c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getNombreConcierto());
            }
        });

        cargarKPIs();
    }

    // =========================
    // KPIs (placeholder sin DB extra)
    // =========================

    private void cargarKPIs() {

        List<Concierto> conciertos = conciertoService.obtenerConciertosSolos();

        lbl_eventosActivos.setText(String.valueOf(conciertos.size()));

        lbl_ingresosTotales.setText("COP 0"); // luego lo conectas a analisisService
        lbl_artistaRentable.setText("N/A");
        lbl_reportesGenerados.setText("0");
    }

    // =========================
    // EVENTOS
    // =========================

    @FXML
    public void On_seleccionarEvento() {

        Concierto c = combo_eventos.getValue();

        if (c == null) {
            txt_resumenEvento.clear();
            return;
        }

        String resumen =
                "Evento: " + c.getNombreConcierto() + "\n" +
                        "Aforo: " + c.getAforo() + "\n" +
                        "Programado: " + c.isProgramado() + "\n";

        txt_resumenEvento.setText(resumen);
    }

    @FXML
    public void On_generarReporte() {

        Concierto c = combo_eventos.getValue();

        if (c == null) {
            txt_resumenEvento.setText("Selecciona un evento primero.");
            return;
        }

        txt_resumenEvento.setText(
                "REPORTE DEL EVENTO\n\n" +
                        "Nombre: " + c.getNombreConcierto() + "\n" +
                        "Aforo: " + c.getAforo() + "\n" +
                        "Estado: " + (c.isProgramado() ? "Programado" : "Pendiente") + "\n"
        );
    }

    @FXML
    public void On_artistaRentable() {
        lbl_artistaRentable.setText("Función pendiente (requiere análisis financiero)");
    }

    @FXML
    public void On_historial() {
        txt_resumenEvento.setText("Historial aún no implementado");
    }

    @FXML
    public void On_exportar() {
        txt_resumenEvento.setText("Exportación aún no implementada");
    }

    @FXML
    public void On_volver() throws IOException {
        sceneManager.showMenu();
    }
}