package org.example.ax0006.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.example.ax0006.Entity.Horario;
import org.example.ax0006.Entity.TipoObjeto;
import org.example.ax0006.Manager.SceneManager;
import org.example.ax0006.Service.InventarioService;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class crearInventarioController {

    @FXML private DatePicker dp_fechaInicio;
    @FXML private DatePicker dp_fechaFin;
    @FXML private TextField tf_horaInicio;
    @FXML private TextField tf_horaFin;
    @FXML private VBox contenedorObjetos;
    @FXML private Label lbl_msg;

    private InventarioService inventarioService;
    private InventarioObjetoService inventarioObjetoService;
    private CrearObjetoInventarioService tipoObjetoService;
    private SceneManager sceneManager;

    private List<ComboBox<TipoObjeto>> combos = new ArrayList<>();
    private List<TipoObjeto> todosLosObjetos;

    private boolean confirmacion = false;

    public crearInventarioController(
            InventarioService inventarioService,
            InventarioObjetoService inventarioObjetoService,
            CrearObjetoInventarioService tipoObjetoService,
            SceneManager sceneManager
    ) {
        this.inventarioService = inventarioService;
        this.inventarioObjetoService = inventarioObjetoService;
        this.tipoObjetoService = tipoObjetoService;
        this.sceneManager = sceneManager;
    }

    @FXML
    public void initialize() {
        todosLosObjetos = tipoObjetoService.obtenerTodos();
        agregarNuevoCombo();
    }

    @FXML
    void on_bt_agregarObjeto(ActionEvent event) {
        agregarNuevoCombo();
    }

    private void agregarNuevoCombo() {
        ComboBox<TipoObjeto> combo = new ComboBox<>();
        combo.getItems().addAll(todosLosObjetos);
        combos.add(combo);
        contenedorObjetos.getChildren().add(combo);
    }

    @FXML
    void on_bt_crear(ActionEvent event) {

        if (!confirmacion) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmación");
            alert.setHeaderText(null);
            alert.setContentText("¿Confirmar envío? Presiona nuevamente 'Crear' para continuar.");
            alert.showAndWait();
            confirmacion = true;
            return;
        }

        try {

            Horario horario = new Horario();
            horario.setFechaInicio(dp_fechaInicio.getValue());
            horario.setFechaFin(dp_fechaFin.getValue());
            horario.setHoraInicio(LocalTime.parse(tf_horaInicio.getText()));
            horario.setHoraFin(LocalTime.parse(tf_horaFin.getText()));

            Set<Integer> usados = new HashSet<>();
            List<String> nombresConflictos = new ArrayList<>();


            for (ComboBox<TipoObjeto> cb : combos) {
                TipoObjeto obj = cb.getValue();
                if (obj == null) continue;

                if (!usados.add(obj.getIdTipoObjeto())) {
                    lbl_msg.setText("Error: No puedes repetir el objeto '" + obj.getNombre() + "'");
                    confirmacion = false;
                    return;
                }

                boolean enUso = inventarioObjetoService.objetoEnUsoEnRango(
                        obj.getIdTipoObjeto(),
                        horario
                );

                if (enUso) {
                    nombresConflictos.add(obj.getNombre());
                }
            }

            if (!nombresConflictos.isEmpty()) {
                String msg = "Conflicto de horario en:\n- " + String.join("\n- ", nombresConflictos);
                lbl_msg.setText(msg);

                confirmacion = false;
                return;
            }

            int inventarioId = inventarioService.crearInventario(horario);
            
            if (inventarioService.existeHorario(horario)) {
                lbl_msg.setText("Error: El horario ya existe o es inválido.");
                confirmacion = false;
                return;
            }

            if (inventarioId == -1) {
                lbl_msg.setText("Error: El horario ya existe o es inválido.");
                confirmacion = false;
                return;
            }

            for (Integer idObjeto : usados) {
                inventarioObjetoService.asignarObjetoAInventario(
                        inventarioId,
                        idObjeto
                );
            }

            lbl_msg.setText("Inventario #" + inventarioId + " creado exitosamente.");
            confirmacion = false;

        } catch (DateTimeParseException e) {
            lbl_msg.setText("Error: Formato de hora inválido (HH:mm)");
            confirmacion = false;
        } catch (Exception e) {
            lbl_msg.setText("Ocurrió un error inesperado.");
            confirmacion = false;
            e.printStackTrace();
        }
    }
    @FXML
    void on_bt_volver(ActionEvent event) throws IOException {
        sceneManager.showMenu();
    }

}