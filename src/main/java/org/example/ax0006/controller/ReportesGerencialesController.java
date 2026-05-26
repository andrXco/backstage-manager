package org.example.ax0006.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import org.example.ax0006.entity.Concierto;
import org.example.ax0006.manager.SceneManager;
import org.example.ax0006.manager.SesionManager;
import org.example.ax0006.service.AnalisisFinancieroService;
import org.example.ax0006.service.ConciertoService;

import java.io.IOException;
import java.util.List;

public class ReportesGerencialesController {

    // =========================
    // SERVICES
    // =========================
    private SceneManager sceneManager;
    private SesionManager sesion;
    private ConciertoService conciertoService;
    private AnalisisFinancieroService analisisService;

    // =========================
    // CONSTRUCTOR
    // =========================
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
    // KPI LABELS
    // =========================
    @FXML
    private Label lbl_ingresosTotales;

    @FXML
    private Label lbl_eventosActivos;

    @FXML
    private Label lbl_artistaRentable;

    @FXML
    private Label lbl_reportesGenerados;

    // =========================
    // COMPONENTES
    // =========================
    @FXML
    private ComboBox<Concierto> combo_eventos;

    @FXML
    private TextArea txt_resumenEvento;

    // =========================
    // BOTONES
    // =========================
    @FXML
    private Button bt_exportar;

    @FXML
    private Button bt_rendimiento;

    @FXML
    private Button bt_generarReporte;

    @FXML
    private Button bt_artistaRentable;

    @FXML
    private Button bt_historial;

    @FXML
    private Button bt_volver;

    // =========================
    // INITIALIZE
    // =========================
    @FXML
    public void initialize() {

        cargarConciertos();
        cargarKPIs();

        combo_eventos.setCellFactory(
                lv -> new javafx.scene.control.ListCell<Concierto>() {

                    @Override
                    protected void updateItem(
                            Concierto concierto,
                            boolean empty
                    ) {

                        super.updateItem(concierto, empty);

                        setText(
                                empty || concierto == null
                                        ? null
                                        : concierto.getNombreConcierto()
                        );
                    }
                }
        );

        combo_eventos.setButtonCell(
                new javafx.scene.control.ListCell<Concierto>() {

                    @Override
                    protected void updateItem(
                            Concierto concierto,
                            boolean empty
                    ) {

                        super.updateItem(concierto, empty);

                        setText(
                                empty || concierto == null
                                        ? null
                                        : concierto.getNombreConcierto()
                        );
                    }
                }
        );
    }

    // =========================
    // CARGAR KPIS
    // =========================
    private void cargarKPIs() {

        try {

            List<Concierto> conciertos =
                    conciertoService.listarConciertos();

            int eventosActivos = 0;

            for (Concierto c : conciertos) {

                if (c.isProgramado()) {
                    eventosActivos++;
                }
            }

            lbl_eventosActivos.setText(
                    String.valueOf(eventosActivos)
            );

            // Placeholder temporal
            lbl_ingresosTotales.setText("USD 0.00");
            lbl_artistaRentable.setText("N/A");
            lbl_reportesGenerados.setText("0");

        } catch (Exception e) {

            mostrarError(
                    "Error cargando indicadores"
            );
        }
    }

    // =========================
    // CARGAR CONCIERTOS
    // =========================
    private void cargarConciertos() {

        combo_eventos.setItems(
                FXCollections.observableArrayList(
                        conciertoService.listarConciertos()
                )
        );
    }

    // =========================
    // SELECCIONAR EVENTO
    // =========================
    @FXML
    public void On_seleccionarEvento() {

        Concierto concierto =
                combo_eventos.getValue();

        if (concierto == null) {
            return;
        }

        sesion.setConciertoActual(concierto);

        StringBuilder resumen =
                new StringBuilder();

        resumen.append("Evento: ")
                .append(concierto.getNombreConcierto())
                .append("\n\n");

        resumen.append("ID: ")
                .append(concierto.getIdConcierto())
                .append("\n");

        resumen.append("Aforo: ")
                .append(concierto.getAforo())
                .append("\n");

        resumen.append("Estado: ")
                .append(
                        concierto.isProgramado()
                                ? "Programado"
                                : "Pendiente"
                )
                .append("\n");

        if (concierto.getArtista() != null) {

            resumen.append("Artista Responsable: ")
                    .append(
                            concierto.getArtista()
                                    .getNombre()
                    )
                    .append("\n");
        }

        if (concierto.getHorario() != null) {

            resumen.append("Horario asignado")
                    .append("\n");
        }

        if (concierto.getAnalisis() != null) {

            resumen.append("\nPresupuesto asociado: ")
                    .append(
                            concierto.getAnalisis()
                                    .getPresupuesto()
                    );

        } else {

            resumen.append(
                    "\nNo posee análisis financiero."
            );
        }

        txt_resumenEvento.setText(
                resumen.toString()
        );
    }

    // =========================
    // VER RENDIMIENTO
    // =========================
    @FXML
    public void On_verRendimiento() {

        Concierto concierto =
                combo_eventos.getValue();

        if (concierto == null) {

            mostrarError(
                    "Debe seleccionar un evento."
            );

            return;
        }

        if (concierto.getAnalisis() == null) {

            mostrarError(
                    "El concierto no tiene análisis financiero."
            );

            return;
        }

        try {

            sceneManager.showAnalisisFinanciero(
                    concierto.getAnalisis()
                            .getIdAnalisisF()
            );

        } catch (IOException e) {

            mostrarError(
                    "No se pudo abrir el análisis."
            );
        }
    }

    // =========================
    // GENERAR REPORTE
    // =========================
    @FXML
    public void On_generarReporte() {

        Concierto concierto =
                combo_eventos.getValue();

        if (concierto == null) {

            mostrarError(
                    "Seleccione un evento."
            );

            return;
        }

        mostrarExito(
                "Reporte generado correctamente."
        );
    }

    // =========================
    // ARTISTA RENTABLE
    // =========================
    @FXML
    public void On_artistaRentable() {

        mostrarExito(
                "Funcionalidad en construcción."
        );
    }

    // =========================
    // HISTORIAL
    // =========================
    @FXML
    public void On_historialReportes() {

        mostrarExito(
                "Historial disponible próximamente."
        );
    }

    // =========================
    // EXPORTAR
    // =========================
    @FXML
    public void On_exportarReporte() {

        mostrarExito(
                "Reporte exportado exitosamente."
        );
    }

    // =========================
    // VOLVER
    // =========================
    @FXML
    public void On_volver() {

        try {

            sceneManager.showMenu();

        } catch (IOException e) {

            mostrarError(
                    "No se pudo volver al menú."
            );
        }
    }

    // =========================
    // ALERTAS
    // =========================
    private void mostrarError(String mensaje) {

        Alert alert =
                new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Error");
        alert.setHeaderText("Operación fallida");
        alert.setContentText(mensaje);

        alert.showAndWait();
    }

    private void mostrarExito(String mensaje) {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Éxito");
        alert.setHeaderText("Operación completada");
        alert.setContentText(mensaje);

        alert.showAndWait();
    }
}