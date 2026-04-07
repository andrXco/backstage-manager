package org.example.ax0006.Controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import org.example.ax0006.Entity.Concierto;
import org.example.ax0006.Manager.SceneManager;
import org.example.ax0006.Manager.SesionManager;
import org.example.ax0006.Service.ConciertoService;

import java.io.IOException;
import java.util.List;

public class ConsultarSolicitudesController {
    /*En esta pantalla se consultan los conciertos a programar, ademas se puede aceptar o rechazar la solicitud*/

    private SesionManager sesion;
    private ConciertoService conciertoService;
    private SceneManager sceneManager;

    public ConsultarSolicitudesController(SesionManager sesion, ConciertoService conciertoService, SceneManager sceneManager){
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
    private TableColumn<Concierto, Integer> colAforo;

    @FXML
    private TableColumn<Concierto, Void> colAccion;

    @FXML
    private Button fid_bt_volver;

    @FXML
    void On_volver(ActionEvent event) throws IOException {
        sceneManager.showMenuConcierto();
    }

    //se crea la tabla
    @FXML
    public void initialize() {

        colArtista.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getArtista().getNombre()
                )
        );

        colFechaInicio.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getHorario().getFechaInicio().toString()
                ));

        colFechaFin.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getHorario().getFechaFin().toString()
                ));

        colHoraInicio.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getHorario().getHoraInicio().toString()
                )
        );

        colHoraFin.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getHorario().getHoraFin().toString()
                )
        );

        colAforo.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        data.getValue().getAforo()
                ).asObject()
        );

        agregarBotonesAccion();

        cargarConciertos();
    }

    private void agregarBotonesAccion() {
        colAccion.setCellFactory(param -> new TableCell<>() {

            private final Button btnAceptar = new Button("Aceptar");
            private final Button btnRechazar = new Button("Rechazar");
            private final HBox contenedor = new HBox(10, btnAceptar, btnRechazar);

            {
                btnAceptar.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
                btnRechazar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                // ACEPTAR
                btnAceptar.setOnAction(event -> {
                    Concierto c = getTableView().getItems().get(getIndex());

                    conciertoService.aprobarConcierto(c.getIdConcierto());

                    getTableView().getItems().remove(c);
                });

                // RECHAZAR
                btnRechazar.setOnAction(event -> {
                    Concierto c = getTableView().getItems().get(getIndex());

                    conciertoService.eliminarConcierto(c.getIdConcierto(),c.getHorario().getIdHorario());

                    getTableView().getItems().remove(c);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : contenedor);
            }
        });
    }

    private void cargarConciertos() {
        List<Concierto> lista = conciertoService.obtenerConciertos();

        List<Concierto> pendientes = lista.stream().filter(c -> !c.isProgramado()).toList();

        tablaConciertos.getItems().setAll(pendientes);
    }


}
