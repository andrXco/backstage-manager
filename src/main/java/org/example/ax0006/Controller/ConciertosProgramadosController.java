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

    private SesionManager sesion;
    private ConciertoService conciertoService;
    private SceneManager sceneManager;

    @FXML
    private TableView<Concierto> tablaConciertos;

    @FXML
    private TableColumn<Concierto, String> colArtista;

    @FXML
    private TableColumn<Concierto, String> colFecha;

    @FXML
    private TableColumn<Concierto, String> colHoraInicio;

    @FXML
    private TableColumn<Concierto, String> colHoraFin;

    @FXML
    private TableColumn<Concierto, String> colAforo;

    @FXML
    private TableColumn<Concierto, Void> colAccion;

    // BOTÓN VOLVER
    @FXML
    void On_volver(ActionEvent event) {
        try {
            sceneManager.showMenu();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {

        colArtista.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getArtista().getNombre()));

        colFecha.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getHorario().getFecha().toString()));

        colHoraInicio.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getHorario().getHoraInicio().toString()));

        colHoraFin.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getHorario().getHoraFin().toString()));

        colAforo.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getAforo())));

        agregarBotonCancelar();
        cargarConciertos();
    }


    private void agregarBotonCancelar() {

        colAccion.setCellFactory(param -> new TableCell<>() {

            private final Button btnCancelar = new Button("Cancelar");

            {
                btnCancelar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                btnCancelar.setOnAction(event -> {

                    Concierto c = getTableView().getItems().get(getIndex());

                    conciertoService.eliminarConcierto(c.getIdConcierto());

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

    private void cargarConciertos() {
        List<Concierto> lista = conciertoService.obtenerConciertos();

        List<Concierto> programados = lista.stream()
                .filter(c -> c.isProgramado())
                .toList();

        tablaConciertos.getItems().setAll(programados);
    }
}
