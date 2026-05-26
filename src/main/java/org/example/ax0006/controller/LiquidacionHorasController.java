package org.example.ax0006.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.ListCell;
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

public class LiquidacionHorasController {

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

    public LiquidacionHorasController(SceneManager sceneManager, SesionManager sesion,
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
                "ROL EN LIQUIDACION: " +
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

        boolean esManager =
                sesion.getUsuarioActual().getIdRol() == 3;

        tablaNominas.setEditable(esManager);

        List<Concierto> conciertos = conciertoService.obtenerConciertosSolos().stream()
                .filter(Concierto::isProgramado)
                .toList();

        System.out.println("[LiquidacionHoras] Conciertos programados cargados: " + conciertos.size());

        comboEvento.setItems(FXCollections.observableArrayList(conciertos));

        comboEvento.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Concierto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNombreConcierto());
                }
            }
        });

        comboEvento.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Concierto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNombreConcierto());
                }
            }
        });

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

    @FXML
    void On_seleccionarEvento(ActionEvent event) {
        Concierto seleccionado = comboEvento.getValue();
        if (seleccionado == null) {
            System.out.println("[LiquidacionHoras] Concierto null, limpiando tabla");
            tablaNominas.getItems().clear();
            lblTotalGeneral.setText("$0");
            return;
        }

        int idConcierto = seleccionado.getIdConcierto();
        System.out.println("[LiquidacionHoras] Seleccionado concierto ID: " + idConcierto + " - " + seleccionado.getNombreConcierto());

        nominaService.generarNominaParaConcierto(idConcierto);
        System.out.println("[LiquidacionHoras] Generación de nóminas ejecutada");

        cargarNominas(idConcierto);
        System.out.println("[LiquidacionHoras] Tabla cargada con " + tablaNominas.getItems().size() + " nóminas");
    }

    private void cargarNominas(int idConcierto) {
        List<Nomina> nominas = nominaService.obtenerNominasPorConcierto(idConcierto);
        System.out.println("[LiquidacionHoras] Nominas recuperadas de BD: " + nominas.size());
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
        Concierto c = comboEvento.getValue();
        if (c == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText("Seleccione un evento primero");
            alert.showAndWait();
            return;
        }

        try {
            sceneManager.showNominaGeneral(c.getIdConcierto());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void On_AsignarNomina(ActionEvent event) {
        System.out.println("ENTRO AL BOTON ASIGNAR");
        int rol = sesion.getUsuarioActual().getIdRol();
        boolean esManager = rol == 3;

        if (!esManager) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Acceso denegado");
            alert.setContentText("Solo el manager puede asignar nómina.");
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

}
