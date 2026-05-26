package org.example.ax0006.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.TableRow;
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

    @FXML
    private ComboBox<Concierto> comboEvento;
    @FXML
    private TableView<Nomina> tablaNominas;
    @FXML
    private TableColumn<Nomina, String> colTrabajador;
    @FXML
    private TableColumn<Nomina, Double> colHoras;
    @FXML
    private TableColumn<Nomina, Double> colTarifa;
    @FXML
    private TableColumn<Nomina, Double> colExtra;
    @FXML
    private TableColumn<Nomina, Double> colTotal;
    @FXML
    private TableColumn<Nomina, String> colEstado;
    @FXML
    private Label lblTotalGeneral;
    @FXML
    private Button btnGenerarReporte;


    private SceneManager sceneManager;
    private SesionManager sesion;
    private ConciertoService conciertoService;
    private NominaService nominaService;
    private StaffService staffService;


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

        System.out.println(
                "ROL EN NOMINA: " +
                sesion.getUsuarioActual().getIdRol()
        );

        colTrabajador.setCellValueFactory(cellData -> {
            int idUsuario = cellData.getValue().getIdUsuario();
            Usuario u = staffService.listarEmpleados().stream()
                    .filter(emp -> emp.getIdUsuario() == idUsuario)
                    .findFirst().orElse(null);
            return new SimpleStringProperty(u != null ? u.getNombre() : "Desconocido");
        });

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

        colExtra.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(
                        cellData.getValue().getHorasExtra()
                ).asObject()
        );

        colTotal.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(
                        cellData.getValue().getTotal()
                ).asObject()
        );

        colEstado.setCellValueFactory(cellData -> {
            String estado = cellData.getValue().getEstado();

            String bonito;

            switch (estado) {
                case "PAGADO":
                    bonito = "Pagado";
                    break;
                case "NO_PAGO":
                    bonito = "No pago";
                    break;
                default:
                    bonito = "Pendiente";
            }

            return new SimpleStringProperty(bonito);
        });

        tablaNominas.setEditable(true);


        System.out.println("SESION NOMINA: " + sesion);
        System.out.println("USUARIO NOMINA: " + sesion.getUsuarioActual());
        System.out.println("ROL NOMINA: " + sesion.getUsuarioActual().getIdRol());


        boolean esManager =
                sesion.getUsuarioActual().getIdRol() == 3;

        tablaNominas.setEditable(esManager);

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

        tablaNominas.setRowFactory(tv -> {
            TableRow<Nomina> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {

                    Nomina nomina = row.getItem();

                    try {
                        sceneManager.showDetalleNomina(nomina);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            return row;
        });
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
        Nomina nominaSeleccionada = tablaNominas.getSelectionModel().getSelectedItem();
        if (nominaSeleccionada == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText("Seleccione una nómina de la tabla");
            alert.showAndWait();
            return;
        }
        try {
            sceneManager.showDetalleNomina(nominaSeleccionada);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void On_AsignarNomina(ActionEvent event) {

        System.out.println("ENTRO AL BOTON");
        int rol = sesion.getUsuarioActual().getIdRol();
        System.out.println("ROL ACTUAL: " + rol);
        boolean esManager = rol == 3;
        System.out.println("ES MANAGER: " + esManager);

        if (!esManager) {

            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setHeaderText("Acceso denegado");
            alert.setContentText(
                    "Solo el manager puede asignar nómina."
            );

            alert.showAndWait();
            return;
        }

        try {

            sceneManager.showAsignarNomina(
                    nominaService.obtenerTodas()
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void On_Aprobar(ActionEvent event) {

        cambiarEstado("PAGADO");
    }

    @FXML
    void On_Rechazar(ActionEvent event) {

        cambiarEstado("NO_PAGO");
    }

    private void cambiarEstado(String estado) {

        Nomina nomina =
                tablaNominas.getSelectionModel().getSelectedItem();

        if (nomina == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Seleccione una nómina");
            alert.showAndWait();
            return;
        }

        nominaService.actualizarEstado(
                nomina.getIdNomina(),
                estado
        );

        nomina.setEstado(estado);

        tablaNominas.refresh();
    }

    @FXML
    void On_VerNominaGeneral(ActionEvent event) {

        Concierto c = comboEvento.getValue();
        if (c == null) return;

        var resumen = nominaService.obtenerNominaGeneralPorEvento(c.getIdConcierto());

        resumen.forEach(r -> {
            System.out.println(r.rol + " | " + r.cantidad + " | " + r.total);
        });
    }

}