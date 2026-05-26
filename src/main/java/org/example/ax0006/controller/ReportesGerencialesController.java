package org.example.ax0006.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.ax0006.entity.Concierto;
import org.example.ax0006.manager.SceneManager;
import org.example.ax0006.manager.SesionManager;
import org.example.ax0006.service.ConciertoService;
import org.example.ax0006.service.AnalisisFinancieroService;
import org.example.ax0006.service.ReporteService;
import org.example.ax0006.dto.ReporteDashboardDTO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ReportesGerencialesController {

    private final SceneManager sceneManager;
    private final SesionManager sesion;
    private final ConciertoService conciertoService;
    private final AnalisisFinancieroService analisisService;
    private final ReporteService reporteService;

    public ReportesGerencialesController(
            SceneManager sceneManager,
            SesionManager sesion,
            ConciertoService conciertoService,
            AnalisisFinancieroService analisisService,
            ReporteService reporteService
    ) {
        this.sceneManager = sceneManager;
        this.sesion = sesion;
        this.conciertoService = conciertoService;
        this.analisisService = analisisService;
        this.reporteService = reporteService;
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

        List<Concierto> conciertos = conciertoService.listarConciertos();
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
    // KPIs
    // =========================

    private void cargarKPIs() {
        ReporteDashboardDTO dashboard = reporteService.generarDashboard();

        java.text.NumberFormat currencyFormatter = java.text.NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        currencyFormatter.setMaximumFractionDigits(0);
        lbl_ingresosTotales.setText(currencyFormatter.format(dashboard.getIngresosTotales()));

        lbl_eventosActivos.setText(String.valueOf(dashboard.getEventosActivos()));
        lbl_artistaRentable.setText(dashboard.getArtistaMasRentable());
        lbl_reportesGenerados.setText(String.valueOf(dashboard.getReportesGenerados()));
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
                reporteService.obtenerResumenEventoDetallado(c);

        txt_resumenEvento.setText(resumen);
    }

    @FXML
    public void On_verRendimiento() {
        Concierto c = combo_eventos.getValue();

        if (c == null) {
            txt_resumenEvento.setText("Selecciona un evento primero.");
            return;
        }

        String rendimiento = reporteService.obtenerRendimientoConcierto(c);
        txt_resumenEvento.setText(rendimiento);
    }

    @FXML
    public void On_generarReporte() {

        Concierto c = combo_eventos.getValue();

        if (c == null) {
            txt_resumenEvento.setText("Selecciona un evento primero.");
            return;
        }

        String emitidoPor = (sesion.getUsuarioActual() != null) ? sesion.getUsuarioActual().getNombre() : "Gerente de Eventos";
        String reporte = reporteService.generarYGuardarReporte(c, emitidoPor);
        txt_resumenEvento.setText(reporte);

        cargarKPIs();
    }

    @FXML
    public void On_artistaRentable() {
        String rentabilidad = reporteService.obtenerArtistasRentabilidad();
        txt_resumenEvento.setText(rentabilidad);
    }

    @FXML
    public void On_historial() {
        String historial = reporteService.obtenerHistorialReportes();
        txt_resumenEvento.setText(historial);
    }

    @FXML
    public void On_exportar() {
        String contenido = txt_resumenEvento.getText();
        if (contenido == null || contenido.isBlank()) {
            mostrarAlerta("Exportar Reporte", "No hay contenido para exportar en el resumen del evento.", Alert.AlertType.WARNING);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte");
        fileChooser.setInitialFileName("reporte_gerencial.txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de Texto (*.txt)", "*.txt"));

        Stage stage = (Stage) bt_exportar.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(contenido);
                mostrarAlerta("Exportar Reporte", "Reporte exportado exitosamente a:\n" + file.getAbsolutePath(), Alert.AlertType.INFORMATION);
            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("Error al Exportar", "Ocurrió un error al guardar el archivo:\n" + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    public void On_volver() throws IOException {
        sceneManager.showMenu();
    }
}
