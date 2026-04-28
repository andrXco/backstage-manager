package org.example.ax0006.Controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.ax0006.Entity.Concierto;
import org.example.ax0006.Entity.Usuario;
import org.example.ax0006.Manager.SceneManager;
import org.example.ax0006.Manager.SesionManager;
import org.example.ax0006.Service.ConciertoService;
import org.example.ax0006.Service.StaffService;

import java.io.IOException;
import java.util.List;

public class DirectorioStaffController {

    private SceneManager sceneManager;
    private SesionManager sesion;
    private ConciertoService conciertoService;
    private StaffService staffService;
    private Usuario usuarioSeleccionado;

    public DirectorioStaffController(SceneManager sceneManager, SesionManager sesion, ConciertoService conciertoService, StaffService staffService) {
        this.sceneManager = sceneManager;
        this.sesion = sesion;
        this.conciertoService = conciertoService;
        this.staffService = staffService;
    }

    @FXML
    private ComboBox<Concierto> comboConcierto;

    @FXML
    private TableView<Usuario> tablaStaff;

    @FXML
    private TableColumn<Usuario, String> colNombre;

    @FXML
    private TableColumn<Usuario, String> colGmail;

    @FXML
    private TableColumn<Usuario, String> colSubrol;

    @FXML
    private ComboBox<String> comboSubrol;

    @FXML
    private Button btnGuardarCambios;

    @FXML
    private Button btnVolver;

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombre())
        );

        colGmail.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getGmail())
        );

        colSubrol.setCellValueFactory(data -> {
            Concierto conciertoSeleccionado = comboConcierto.getValue();
            if (conciertoSeleccionado == null) {
                return new SimpleStringProperty("Sin subrol");
            }

            String subrol = staffService.obtenerSubrolStaffEnConcierto(
                    data.getValue().getIdUsuario(),
                    conciertoSeleccionado.getIdConcierto()
            );

            if (subrol == null || subrol.isBlank()) {
                subrol = "Sin subrol";
            }

            return new SimpleStringProperty(subrol);
        });

        comboSubrol.getItems().addAll(
                "Sonido",
                "Luces",
                "Seguridad",
                "Logística",
                "Producción"
        );

        comboConcierto.getItems().addAll(
                conciertoService.obtenerConciertosSolos().stream()
                        .filter(Concierto::isProgramado)
                        .toList()
        );

        comboConcierto.setCellFactory(lv -> new ListCell<>() {
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

        comboConcierto.setButtonCell(new ListCell<>() {
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

        tablaStaff.getSelectionModel().selectedItemProperty().addListener((obs, anterior, actual) -> usuarioSeleccionado = actual);
    }

    @FXML
    void On_seleccionarConcierto(ActionEvent event) {
        Concierto conciertoSeleccionado = comboConcierto.getValue();

        if (conciertoSeleccionado == null) {
            tablaStaff.getItems().clear();
            usuarioSeleccionado = null;
            return;
        }

        List<Usuario> staffDelConcierto = staffService.obtenerUsuariosPorConcierto(conciertoSeleccionado.getIdConcierto())
                .stream()
                .filter(u -> "Staff".equalsIgnoreCase(
                        staffService.obtenerNombreRolEnConcierto(u.getIdUsuario(), conciertoSeleccionado.getIdConcierto())
                ))
                .toList();

        tablaStaff.setItems(FXCollections.observableArrayList(staffDelConcierto));
        usuarioSeleccionado = null;
        comboSubrol.getSelectionModel().clearSelection();
        tablaStaff.refresh();
    }

    @FXML
    void On_guardarCambios(ActionEvent event) {
        Concierto conciertoSeleccionado = comboConcierto.getValue();
        String subrolSeleccionado = comboSubrol.getValue();

        if (conciertoSeleccionado == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Dato faltante");
            alert.setHeaderText("Selecciona un concierto");
            alert.showAndWait();
            return;
        }

        if (usuarioSeleccionado == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Dato faltante");
            alert.setHeaderText("Selecciona un usuario de la tabla");
            alert.showAndWait();
            return;
        }

        if (subrolSeleccionado == null || subrolSeleccionado.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Dato faltante");
            alert.setHeaderText("Selecciona un subrol");
            alert.showAndWait();
            return;
        }

        boolean actualizado = staffService.actualizarSubrolStaffEnConcierto(
                usuarioSeleccionado.getIdUsuario(),
                conciertoSeleccionado.getIdConcierto(),
                subrolSeleccionado
        );

        if (!actualizado) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo guardar el subrol");
            alert.setContentText("Verifica que el usuario tenga asignado el rol Staff en ese concierto.");
            alert.showAndWait();
            return;
        }

        tablaStaff.refresh();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Subrol asignado");
        alert.setHeaderText("Asignación realizada");
        alert.setContentText("Se guardó el subrol \"" + subrolSeleccionado + "\" para " + usuarioSeleccionado.getNombre() + ".");
        alert.showAndWait();
    }

    @FXML
    void On_volver(ActionEvent event) {
        try {
            sceneManager.showMenu();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}