package org.example.ax0006.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import org.example.ax0006.entity.AnalisisFinanciero;
import org.example.ax0006.entity.Boleteria;
import org.example.ax0006.entity.Gasto;
import org.example.ax0006.entity.Ingreso;
import org.example.ax0006.manager.SceneManager;
import org.example.ax0006.service.AnalisisFinancieroService;
import org.example.ax0006.service.BoleteriaService;
import org.example.ax0006.service.ConciertoService;
import org.example.ax0006.service.GastoService;
import org.example.ax0006.service.IngresoService;
import org.example.ax0006.entity.Concierto;
import org.example.ax0006.manager.SesionManager;

import java.io.IOException;

public class AnalisisFinancieroController {

    // =========================
    // SERVICES
    // =========================
    private AnalisisFinancieroService analisisService;
    private GastoService gastoService;
    private IngresoService ingresoService;
    private BoleteriaService boleteriaService;
    private SceneManager sceneManager;
    private SesionManager sesion;
    private ConciertoService conciertoService;

    // =========================
    // ID ACTUAL
    // =========================
    private int idAnalisisActual = 0;

    // =========================
    // CONSTRUCTOR
    // =========================

    public AnalisisFinancieroController(

        AnalisisFinancieroService analisisService,
        GastoService gastoService,
        IngresoService ingresoService,
        BoleteriaService boleteriaService,
        ConciertoService conciertoService,
        SesionManager sesion,
        SceneManager sceneManager
) {

    this.analisisService = analisisService;
    this.gastoService = gastoService;
    this.ingresoService = ingresoService;
    this.boleteriaService = boleteriaService;
    this.conciertoService = conciertoService;
    this.sesion = sesion;
    this.sceneManager = sceneManager;
}

    // =========================
    // PRESUPUESTO
    // =========================
    @FXML private TextField fid_presupuesto;

    @FXML private Label lbl_idPresupuesto;

    @FXML private CheckBox chk_aprobado;

    // =========================
    // GASTOS
    // =========================
    @FXML private TextField fid_descripcionGasto;
    @FXML private TextField fid_valorGasto;

    @FXML private TableView<Gasto> tablaGastos;

    @FXML private TableColumn<Gasto, String> colDescripcionGasto;
    @FXML private TableColumn<Gasto, Integer> colValorGasto;

    @FXML private Label lblTotalGastos;

    // =========================
    // INGRESOS
    // =========================
    @FXML private TextField fid_descripcionIngreso;
    @FXML private TextField fid_valorIngreso;

    @FXML private TableView<Ingreso> tablaIngresos;

    @FXML private TableColumn<Ingreso, String> colDescripcionIngreso;
    @FXML private TableColumn<Ingreso, Integer> colValorIngreso;

    @FXML private Label lblTotalIngresos;

    // =========================
    // BOLETERIA
    // =========================
    @FXML private TextField fid_seccion;
    @FXML private TextField fid_cantidad;
    @FXML private TextField fid_precioBoleta;

    @FXML private TableView<Boleteria> tablaBoleteria;

    @FXML private TableColumn<Boleteria, String> colSeccion;
    @FXML private TableColumn<Boleteria, Integer> colCantidad;
    @FXML private TableColumn<Boleteria, Integer> colPrecio;
    @FXML private TableColumn<Boleteria, Integer> colIngresoTotal;

    // =========================
    // INITIALIZE
    // =========================
    @FXML
    public void initialize() {

        colDescripcionGasto.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getDescripcion()
                )
        );

        colValorGasto.setCellValueFactory(
                data -> new javafx.beans.property.SimpleObjectProperty<>(
                        data.getValue().getValor()
                )
        );

        colDescripcionIngreso.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getDescripcion()
                )
        );

        colValorIngreso.setCellValueFactory(
                data -> new javafx.beans.property.SimpleObjectProperty<>(
                        data.getValue().getValor()
                )
        );

        colSeccion.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getSeccion()
                )
        );

        colCantidad.setCellValueFactory(
                data -> new javafx.beans.property.SimpleObjectProperty<>(
                        data.getValue().getCantidad()
                )
        );

        colPrecio.setCellValueFactory(
                data -> new javafx.beans.property.SimpleObjectProperty<>(
                        data.getValue().getPrecioBoleta()
                )
        );

        colIngresoTotal.setCellValueFactory(
                data -> new javafx.beans.property.SimpleObjectProperty<>(
                        data.getValue().getIngresoTotal()
                )
        );




    }

    // =========================
    // CREAR PRESUPUESTO
    // =========================
    @FXML
    public void On_guardarPresupuesto() {

        try {

            int presupuesto =
                    Integer.parseInt(
                            fid_presupuesto.getText()
                    );

            int id =
                    analisisService.crearPresupuesto(
                            presupuesto
                    );

            if (id != 0) {

                idAnalisisActual = id;

                lbl_idPresupuesto.setText(
                        "ID Presupuesto: " + id
                );

                mostrarExito(
                        "Presupuesto creado"
                );

            } else {

                mostrarError(
                        "No se pudo crear"
                );
            }

        } catch (Exception e) {

            mostrarError(
                    "Datos inválidos"
            );
        }
    }

    // =========================
    // EDITAR PRESUPUESTO
    // =========================
    @FXML
    public void On_editarPresupuesto() {

        try {

            int presupuesto =
                    Integer.parseInt(
                            fid_presupuesto.getText()
                    );

            analisisService.editarPresupuesto(
                    idAnalisisActual,
                    presupuesto
            );

            mostrarExito(
                    "Presupuesto actualizado"
            );

        } catch (Exception e) {

            mostrarError(
                    "Error al actualizar"
            );
        }
    }

    // =========================
    // ELIMINAR PRESUPUESTO
    // =========================
    @FXML
    public void On_eliminarPresupuesto() {

        analisisService.eliminarAnalisis(
                idAnalisisActual
        );

        mostrarExito(
                "Presupuesto eliminado"
        );
    }

    // =========================
    // AGREGAR GASTO
    // =========================
    @FXML
    public void On_agregarGasto() {

        try {

            String descripcion =
                    fid_descripcionGasto.getText();

            int valor =
                    Integer.parseInt(
                            fid_valorGasto.getText()
                    );

            gastoService.agregarGasto(
                    descripcion,
                    valor,
                    idAnalisisActual
            );

            actualizarTablaGastos();

        } catch (Exception e) {

            mostrarError(
                    "Error al agregar gasto"
            );
        }
    }

    // =========================
    // ELIMINAR GASTO
    // =========================
    @FXML
    public void On_eliminarGasto() {

        Gasto gasto =
                tablaGastos.getSelectionModel()
                        .getSelectedItem();

        if (gasto == null) {
            return;
        }

        gastoService.eliminarGasto(
                gasto.getIdGasto()
        );

        actualizarTablaGastos();
    }

    // =========================
    // AGREGAR INGRESO
    // =========================
    @FXML
    public void On_agregarIngreso() {

        try {

            String descripcion =
                    fid_descripcionIngreso.getText();

            int valor =
                    Integer.parseInt(
                            fid_valorIngreso.getText()
                    );

            ingresoService.agregarIngreso(
                    descripcion,
                    valor,
                    idAnalisisActual
            );

            actualizarTablaIngresos();

        } catch (Exception e) {

            mostrarError(
                    "Error al agregar ingreso"
            );
        }
    }

    // =========================
    // ELIMINAR INGRESO
    // =========================
    @FXML
    public void On_eliminarIngreso() {

        Ingreso ingreso =
                tablaIngresos.getSelectionModel()
                        .getSelectedItem();

        if (ingreso == null) {
            return;
        }

        ingresoService.eliminarIngreso(
                ingreso.getIdIngreso()
        );

        actualizarTablaIngresos();
    }

    // =========================
    // AGREGAR BOLETERIA
    // =========================
    @FXML
    public void On_agregarBoleteria() {

        try {

            String seccion =
                    fid_seccion.getText();

            int cantidad =
                    Integer.parseInt(
                            fid_cantidad.getText()
                    );

            int precio =
                    Integer.parseInt(
                            fid_precioBoleta.getText()
                    );

            boleteriaService.agregarBoleteria(
                    seccion,
                    cantidad,
                    precio,
                    idAnalisisActual
            );

            actualizarTablaBoleteria();

        } catch (Exception e) {

            mostrarError(
                    "Error al agregar boletería"
            );
        }
    }

    // =========================
    // ELIMINAR BOLETERIA
    // =========================
    @FXML
    public void On_eliminarBoleteria() {

        Boleteria boleteria =
                tablaBoleteria.getSelectionModel()
                        .getSelectedItem();

        if (boleteria == null) {
            return;
        }

        boleteriaService.eliminarBoleteria(
                boleteria.getIdBoleteria()
        );

        actualizarTablaBoleteria();
    }

    // =========================
    // APROBAR
    // =========================
    @FXML
    public void On_aprobarPresupuesto() {

        if (chk_aprobado.isSelected()) {

            analisisService.aprobarPresupuesto(
                    idAnalisisActual
            );

        } else {

            analisisService.desaprobarPresupuesto(
                    idAnalisisActual
            );
        }

        mostrarExito(
                "Estado actualizado"
        );
    }

    // =========================
    // ACTUALIZAR TABLAS
    // =========================
    private void actualizarTablaGastos() {

        ObservableList<Gasto> lista =
                FXCollections.observableArrayList(
                        gastoService.listarGastos(
                                idAnalisisActual
                        )
                );

        tablaGastos.setItems(lista);

        int total =
                gastoService.obtenerTotalGastos(
                        idAnalisisActual
                );

        lblTotalGastos.setText(
                "Gasto Total: " + total
        );
    }

    private void actualizarTablaIngresos() {

        ObservableList<Ingreso> lista =
                FXCollections.observableArrayList(
                        ingresoService.listarIngresos(
                                idAnalisisActual
                        )
                );

        tablaIngresos.setItems(lista);

        int total =
                ingresoService.obtenerTotalIngresos(
                        idAnalisisActual
                );

        lblTotalIngresos.setText(
                "Ingreso Total: " + total
        );
    }

    private void actualizarTablaBoleteria() {

        ObservableList<Boleteria> lista =
                FXCollections.observableArrayList(
                        boleteriaService.listarBoleteria(
                                idAnalisisActual
                        )
                );

        tablaBoleteria.setItems(lista);
    }


    public void cargarAnalisis(int id) {
        AnalisisFinanciero af =
                analisisService.obtenerAnalisis(id);

        if (af == null) {
            return;
        }

        idAnalisisActual = id;

        fid_presupuesto.setText(
                String.valueOf(
                        af.getPresupuesto()
                )
        );

        chk_aprobado.setSelected(
                af.isAprobado()
        );

        lbl_idPresupuesto.setText(
                "ID Presupuesto: " + id
        );

        actualizarTablaGastos();
        actualizarTablaIngresos();
        actualizarTablaBoleteria();
    }

    @FXML
    public void On_guardarFinanzas() {

    if (idAnalisisActual == 0) {

        mostrarError(
                "Primero cree un presupuesto"
        );

        return;
    }

    mostrarExito(
            "Finanzas del evento guardadas correctamente"
    );

    // Aquí puedes volver al menú
    // o cerrar el proceso
    }

    // =========================
    // VOLVER
    // =========================
    @FXML
    public void On_volver() throws IOException {

        sceneManager.showMenu();
    }

    // =========================
    // ALERTAS
    // =========================
    private void mostrarError(String msg) {

        Alert alert =
                new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Error");
        alert.setHeaderText("No se pudo continuar");
        alert.setContentText(msg);

        alert.showAndWait();
    }

    private void mostrarExito(String msg) {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Éxito");
        alert.setHeaderText("Operación realizada");
        alert.setContentText(msg);

        alert.showAndWait();
    }
}