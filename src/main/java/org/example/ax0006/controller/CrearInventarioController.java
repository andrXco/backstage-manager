package org.example.ax0006.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.example.ax0006.entity.Concierto;
import org.example.ax0006.entity.Objeto;
import org.example.ax0006.manager.SceneManager;
import org.example.ax0006.manager.SesionManager;
import org.example.ax0006.service.InventarioObjetoService;
import org.example.ax0006.service.InventarioService;
import org.example.ax0006.service.ObjetoService;

import java.util.ArrayList;
import java.util.List;

public class CrearInventarioController {
    private final InventarioService inventarioService;
    private final ObjetoService objetoService;
    private final SceneManager sceneManager;
    private final SesionManager sesionManager;
    private final InventarioObjetoService inventarioObjetoService;

    @FXML private DatePicker dp_fechaInicio;
    @FXML private DatePicker dp_fechaFin;
    @FXML private TextField tf_horaInicio;
    @FXML private TextField tf_horaFin;
    @FXML private TextField tf_idConcierto;
    @FXML private ComboBox<String> cb_objetos;
    @FXML private ListView<String> lv_objetos_seleccionados;
    @FXML private VBox panel_agregar;
    @FXML private VBox panel_eliminar;
    @FXML private Button btn_confirmar_todo;

    private ObservableList<String> listaVisualObjetos = FXCollections.observableArrayList();
    private List<Integer> listaIdsParaGuardar = new ArrayList<>();

    public CrearInventarioController(InventarioService inventarioService, ObjetoService objetoService, SceneManager sceneManager, SesionManager sesionManager, InventarioObjetoService inventarioObjetoService) {
        this.inventarioService = inventarioService;
        this.objetoService = objetoService;
        this.sceneManager = sceneManager;
        this.sesionManager = sesionManager;
        this.inventarioObjetoService = inventarioObjetoService;
    }

    @FXML
    public void initialize() {
        Concierto c = sesionManager.getConciertoTemporal();
        if (c != null) {
            tf_idConcierto.setText(String.valueOf(c.getIdConcierto()));
            List<String> inventarioExistente = inventarioService.obtenerObjetosPorConcierto(c.getIdConcierto());

            if (!inventarioExistente.isEmpty()) {
                mostrarPanelEliminar(true);
            }

            if (c.getHorario() != null) {
                dp_fechaInicio.setValue(c.getHorario().getFechaInicio());
                dp_fechaFin.setValue(c.getHorario().getFechaFin());
                tf_horaInicio.setText(c.getHorario().getHoraInicio().toString());
                tf_horaFin.setText(c.getHorario().getHoraFin().toString());
            }
        }

        List<String> objetosDB = objetoService.obtenerListaObjetosFormateada();
        cb_objetos.setItems(FXCollections.observableArrayList(objetosDB));
        lv_objetos_seleccionados.setItems(listaVisualObjetos);
    }

    private void mostrarPanelEliminar(boolean mostrarEliminar) {
        panel_eliminar.setVisible(mostrarEliminar);
        panel_eliminar.setManaged(mostrarEliminar);
        panel_agregar.setVisible(!mostrarEliminar);
        panel_agregar.setManaged(!mostrarEliminar);
    }

    @FXML
    void on_bt_eliminar_existente() {
        Concierto c = sesionManager.getConciertoTemporal();
        if (c == null) return;

        try {
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
                listaVisualObjetos.clear();
                listaIdsParaGuardar.clear();

                panel_eliminar.setVisible(false);
                panel_eliminar.setManaged(false);
                panel_agregar.setVisible(true);
                panel_agregar.setManaged(true);

                btn_confirmar_todo.setDisable(false);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @FXML
    void on_bt_quitar_lista() {
        int index = lv_objetos_seleccionados.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            listaVisualObjetos.remove(index);
            listaIdsParaGuardar.remove(index);
        }
    }

    @FXML
    void on_bt_crear() {
        try {
            Concierto c = sesionManager.getConciertoTemporal();
            if(c.isProgramado()){if (c == null || c.getHorario() == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No hay un concierto o horario válido vinculado.");
                return;
            }

                if (!inventarioService.obtenerObjetosPorConcierto(c.getIdConcierto()).isEmpty()) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Inventario Existente", "Este concierto ya tiene un inventario asignado.");
                    return;
                }

                if (listaIdsParaGuardar.isEmpty()) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Lista Vacía", "Debes agregar al menos un objeto.");
                    return;
                }

                List<String> objetosConConflicto = new ArrayList<>();
                for (Integer idObjeto : listaIdsParaGuardar) {
                    if (inventarioObjetoService.objetoEnUsoEnRango(idObjeto, c.getHorario())) {
                        objetosConConflicto.add("ID: " + idObjeto);
                    }
                }

                if (!objetosConConflicto.isEmpty()) {
                    String detalle = String.join("\n", objetosConConflicto);
                    mostrarAlerta(Alert.AlertType.ERROR, "Conflicto de Horario",
                            "Los siguientes objetos ya están ocupados en otro concierto/mantenimiento durante estas fechas:\n\n" + detalle);
                    return;
                }

                int idConcierto = c.getIdConcierto();
                int idHorario = c.getHorario().getIdHorario();
                int resultado = inventarioService.crearDocumentoInventario(idConcierto, idHorario, listaIdsParaGuardar);

                if (resultado != -1) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Inventario creado y objetos vinculados exitosamente.");
                    sesionManager.setConciertoTemporal(null);
                    sceneManager.showMenuConcierto();
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo crear el documento de inventario.");
                }
            }else{
                mostrarAlerta(Alert.AlertType.WARNING, "Concierto no aprobado", "Este concierto aún no puede tener inventario hasta ser aprobado.");
                return;
            }


        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico", "Ocurrió un error: " + e.getMessage());
        }
    }

    @FXML
    void on_bt_agregar_lista() {
        String seleccionado = cb_objetos.getSelectionModel().getSelectedItem();
        if (seleccionado != null && !listaVisualObjetos.contains(seleccionado)) {
            int id = Integer.parseInt(seleccionado.split(" - ")[0]);
            Concierto c = sesionManager.getConciertoTemporal();

            if (c != null && c.getHorario() != null) {
                if (inventarioObjetoService.objetoEnUsoEnRango(id, c.getHorario())) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Objeto Ocupado",
                            "Este objeto ya tiene un compromiso en las fechas de este concierto.");
                    return;
                }
            }

            listaVisualObjetos.add(seleccionado);
            listaIdsParaGuardar.add(id);
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    void on_bt_volver() throws Exception {
        sesionManager.setConciertoTemporal(null);
        sceneManager.showMenuConcierto();
    }
}