package org.example.ax0006.Controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.ax0006.Entity.Concierto;
import org.example.ax0006.Manager.SceneManager;
import org.example.ax0006.Manager.SesionManager;
import org.example.ax0006.Service.ConciertoService;

import java.io.IOException;
import java.util.List;

public class ConciertosProgramadosController {
    /*pantalla en donde se ven los conciertos programados, ademas se pueden cancelar ahi mismo*/

    private SesionManager sesion;
    private ConciertoService conciertoService;
    private SceneManager sceneManager;

    public ConciertosProgramadosController(SesionManager sesion, ConciertoService conciertoService, SceneManager sceneManager) {
        this.sesion = sesion;
        this.conciertoService = conciertoService;
        this.sceneManager = sceneManager;
    }

    @FXML
    private TableView<Concierto> tablaConciertos;

    @FXML
    private TableColumn<Concierto, String> colArtista;

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
    @FXML
    public void initialize() {

        colArtista.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getArtista().getNombre()));

        colFechaInicio.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getHorario().getFechaInicio().toString()));

        colFechaFin.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getHorario().getFechaFin().toString()));

        colHoraInicio.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getHorario().getHoraInicio().toString()));

        colHoraFin.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getHorario().getHoraFin().toString()));

        colAforo.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getAforo())));

        agregarBotonCancelar();
        cargarConciertos();
    }


    /*Se agrega un boton para cancelar los conciertos programados*/
    private void agregarBotonCancelar() {

        colAccion.setCellFactory(param -> new TableCell<>() {

            private final Button btnCancelar = new Button("Cancelar");

            {
                btnCancelar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                btnCancelar.setOnAction(event -> {

                    Concierto c = getTableView().getItems().get(getIndex());

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
        List<Concierto> lista = conciertoService.obtenerConciertos();

        List<Concierto> programados = lista.stream()
                .filter(c -> c.isProgramado())
                .toList();

        tablaConciertos.getItems().setAll(programados);
    }
}
