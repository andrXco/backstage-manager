package org.example.ax0006.Controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.ax0006.Entity.Inventario;
import org.example.ax0006.Entity.Objeto;
import org.example.ax0006.Manager.SceneManager;
import org.example.ax0006.Manager.SesionManager;
import org.example.ax0006.Service.InventarioService;
import org.example.ax0006.Service.ObjetoService;

import java.io.IOException;

public class InventarioDetalleController {


    @FXML
    private TableView<Objeto> tablaObjetos;
    @FXML
    private TableColumn<Objeto, String> colModelo;
    @FXML
    private TableColumn<Objeto, String> colEstado;
    @FXML
    private TableColumn<Objeto, Boolean> colDisponible;
    @FXML
    private TableColumn<Objeto, String> colTipoObjeto;
    @FXML
    private TableColumn<Objeto, Integer> colID;

    @FXML
    private Label fid_InventarioNombre;

    private Inventario inventario;
    private InventarioService inventarioService;
    private ObjetoService objetoService;
    private SceneManager sceneManager;
    private SesionManager session;

    public InventarioDetalleController(SceneManager sceneManager, SesionManager session, InventarioService inventarioService, ObjetoService objetoService) {
        this.sceneManager = sceneManager;
        this.session = session;
        this.inventarioService = inventarioService;
        this.objetoService = objetoService;
    }


    private void cargarObjetos() {
        tablaObjetos.getItems().setAll(
                objetoService.obtenerObjetosInventario(inventario.getIdInventario())
        );
    }

    @FXML
    public void initialize() {
        this.inventario = session.getInventarioSeleccionado();
        fid_InventarioNombre.setText("Inventario: " + session.getInventarioSeleccionado().getNombre());

        colID.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(
                        data.getValue().getIdObjeto()
                ).asObject());

        colTipoObjeto.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getModelo().getTipoObjeto().getNombre()
                ));

        colModelo.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getModelo().getNombre()
                ));

        colEstado.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEstado()));

        colDisponible.setCellValueFactory(data ->
                new SimpleBooleanProperty(data.getValue().isDisponible()));

        cargarObjetos();
    }


    @FXML
    void On_agregarObjeto() {
        //sceneManager.showCrearObjeto(inventario);
    }

    @FXML
    void On_eliminarObjeto() {
        Objeto obj = tablaObjetos.getSelectionModel().getSelectedItem();
        if (obj != null) {
            objetoService.eliminarObjeto(obj.getIdObjeto());
            cargarObjetos();
        }
    }

    @FXML
    void On_volver() throws IOException {
        session.deseleccionarInventario();
        sceneManager.showGestionInventario();
    }
}