package org.example.ax0006.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.ax0006.entity.Concierto;
import org.example.ax0006.entity.DocumentoInventario;
import org.example.ax0006.entity.Objeto;
import org.example.ax0006.manager.SceneManager;
import org.example.ax0006.manager.SesionManager;
import org.example.ax0006.service.ConciertoService;
import org.example.ax0006.service.InventarioService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConciertosProgramadosController {
    /*pantalla en donde se ven los conciertos programados, ademas se pueden cancelar ahi mismo*/

    private SesionManager sesion;
    private ConciertoService conciertoService;
    private SceneManager sceneManager;
    private final InventarioService inventarioService;

    public ConciertosProgramadosController(SesionManager sesion, ConciertoService conciertoService, SceneManager sceneManager, InventarioService inventarioService) {
        this.sesion = sesion;
        this.conciertoService = conciertoService;
        this.sceneManager = sceneManager;
        this.inventarioService = inventarioService;
    }


    @FXML
    private TableView<Concierto> tablaConciertos;




    @FXML
    private TableColumn<Concierto, String> colFechaInicio;

    @FXML
    private TableColumn<Concierto, String> colFechaFin;

    @FXML
    private TableColumn<Concierto, String> colHoraInicio;

    @FXML
    private TableColumn<Concierto, String> colHoraFin;

    @FXML
    private TableColumn<Concierto, String> colAforo;

    @FXML
    private TableColumn<Concierto, String> colNombreConcierto;

    @FXML
    private TableColumn<Concierto, Void> colContrato;

    @FXML
    private TableColumn<Concierto, Void> colAccion;

    /*Es un boton para volver al menu*/
    @FXML
    void On_volver(ActionEvent event) {
        try {
            sceneManager.showMenuConcierto();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*Hace que se actualize y se genere la tabla*/

    @FXML private TableColumn<Concierto, Void> colDetalles;

    @FXML
    public void initialize() {
        colNombreConcierto.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombreConcierto()));
        colAforo.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getAforo())));

        colFechaInicio.setCellValueFactory(data -> {
            var horario = data.getValue().getHorario();
            return new SimpleStringProperty(horario != null && horario.getFechaInicio() != null
                    ? horario.getFechaInicio().toString() : "N/A");
        });

        colFechaFin.setCellValueFactory(data -> {
            var horario = data.getValue().getHorario();
            return new SimpleStringProperty(horario != null && horario.getFechaFin() != null
                    ? horario.getFechaFin().toString() : "N/A");
        });

        colHoraInicio.setCellValueFactory(data -> {
            var horario = data.getValue().getHorario();
            return new SimpleStringProperty(horario != null && horario.getHoraInicio() != null
                    ? horario.getHoraInicio().toString() : "N/A");
        });

        colHoraFin.setCellValueFactory(data -> {
            var horario = data.getValue().getHorario();
            return new SimpleStringProperty(horario != null && horario.getHoraFin() != null
                    ? horario.getHoraFin().toString() : "N/A");
        });

        agregarBotonContrato();
        agregarBotonCancelar();
        agregarBotonDetalles();
        cargarConciertos();
    }
    private void agregarBotonDetalles() {
        colDetalles.setCellFactory(param -> new TableCell<>() {
            private final Button btnInfo = new Button("Ver Detalles");
            {
                btnInfo.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
                btnInfo.setOnAction(event -> {
                    Concierto c = getTableView().getItems().get(getIndex());
                    sesion.setConciertoTemporal(c); // Guardamos para la siguiente pantalla
                    try {
                        sceneManager.showDetallesConcierto();
                    } catch (IOException e) { e.printStackTrace(); }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnInfo);
            }
        });
    }

    // =========================
    //  BOTÓN VER CONTRATO
    // =========================
    private void agregarBotonContrato() {
        sesion.setPantallaOrigen("programados");  //Identificar de que pantalla viene
        colContrato.setCellFactory(param -> new TableCell<>() {

            private final Button btnVer = new Button("Ver");

            {
                btnVer.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

                btnVer.setOnAction(event -> {
                    Concierto c = getTableView().getItems().get(getIndex());

                    // Guardar contrato en sesión
                    sesion.setIdContratoTemporal(c.getIdContrato());

                    try {
                        sceneManager.showVerContrato();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnVer);
            }
        });
    }


    /*Se agrega un boton para cancelar los conciertos programados*/
    private void agregarBotonCancelar() {

        colAccion.setCellFactory(param -> new TableCell<>() {

            private final Button btnCancelar = new Button("Cancelar");

            {
                btnCancelar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                btnCancelar.setOnAction(event -> {

                    Concierto c = getTableView().getItems().get(getIndex());

                    int idConcierto = c.getIdConcierto();
                    int idHorario = c.getHorario().getIdHorario();
                    int idInventario = inventarioService.obtenerDocumentoInventarioPorConcierto(idConcierto);
                    List<Objeto> inventarioExistente = inventarioService.obtenerObjetoObjetosPorConcierto(idConcierto);
                    List<Integer> idsObjetosActuales = new ArrayList<Integer>();

                    for(Objeto objeto : inventarioExistente){
                        int idObjeto = objeto.getIdObjeto();
                        idsObjetosActuales.add(idObjeto);
                    }

                    for(int i = 0; i < 3; i++){
                        inventarioService.eliminarDocumentoInventario(idInventario, idConcierto, idHorario, idsObjetosActuales);
                    }

                    conciertoService.eliminarConcierto(c.getIdConcierto(), c.getHorario().getIdHorario());

                    getTableView().getItems().remove(c);
                });
            }


            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnCancelar);
            }
        });
    }

    /*Carga los datos de los conciertos para que sean cargados en la tabla*/
    private void cargarConciertos() {
        List<Concierto> lista = conciertoService.obtenerConciertosSolos();

        List<Concierto> programados = lista.stream()
                .filter(c -> c.isProgramado())
                .toList();

        tablaConciertos.getItems().setAll(programados);
    }
}
