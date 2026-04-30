package org.example.ax0006.controller;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import org.example.ax0006.entity.Concierto;
import org.example.ax0006.manager.SceneManager;
import org.example.ax0006.service.ConciertoService;
import org.example.ax0006.service.RolService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.ax0006.entity.Usuario;
import org.example.ax0006.manager.SesionManager;
import javafx.event.ActionEvent;
import java.io.IOException;
import org.example.ax0006.entity.Rol;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.ax0006.service.StaffService;

import java.util.List;

public class AdminUsuariosController {

    private SesionManager sesion;
    private RolService rolService;
    private SceneManager sceneManager;
    private ConciertoService conciertoService;
    private StaffService staffService;

    public AdminUsuariosController(SesionManager sesion, RolService rolService, SceneManager sceneManager,ConciertoService conciertoService, StaffService staffService) {
        this.sesion = sesion;
        this.rolService = rolService;
        this.sceneManager = sceneManager;
        this.conciertoService = conciertoService;
        this.staffService = staffService;
    }


    //elementos de la pantalla de administracion de usuarios:
    @FXML
    private Label fid_Bienvenido;

    @FXML
    private Button fid_bt_volver;

    @FXML
    private TableView<Usuario> tablaUsuarios;

    @FXML
    private TableColumn<Usuario, String> colNombre;

    @FXML
    private TableColumn<Usuario, String> colGmail;





    @FXML
    private TableColumn<Usuario, String> colNombreRol;

    @FXML
    private TableColumn<Usuario, Void> colAccion; //columna para asignar rol

    @FXML
    private ComboBox<Object> comboConciertoFiltro;






    @FXML
    public void initialize() {
        if (sesion.getUsuarioActual() != null) {
            fid_Bienvenido.setText("Bienvenido " + sesion.getUsuarioActual().getNombre());
        }

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colGmail.setCellValueFactory(new PropertyValueFactory<>("gmail"));

        colNombreRol.setCellValueFactory(cellData ->
                new SimpleStringProperty(obtenerRolEnConcierto(cellData.getValue()))
        );

        cargarComboConciertoFiltro();
        agregarBoton();

        comboConciertoFiltro.setOnAction(e -> actualizarTabla());

        cargarUsuariosSinAsignar();

    }


    private void cargarComboConciertoFiltro() {
        comboConciertoFiltro.getItems().clear();
        comboConciertoFiltro.getItems().add("Sin asignar");
        comboConciertoFiltro.getItems().addAll(
                conciertoService.obtenerConciertosSolos().stream()
                        .filter(c -> c.isProgramado())
                        .toList()
        );
        comboConciertoFiltro.setValue("Sin asignar");


        comboConciertoFiltro.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else if (item instanceof Concierto c)
                            setText(c.getNombreConcierto());
                else setText(item.toString());
            }
        });

        comboConciertoFiltro.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else if (item instanceof Concierto c)
                    setText(c.getNombreConcierto());
                else setText(item.toString());
            }
        });
    }

    // Decide qué cargar según el filtro seleccionado
    private void actualizarTabla() {
        Object seleccionado = comboConciertoFiltro.getValue();
        if (seleccionado instanceof Concierto c) {
            cargarUsuariosPorConcierto(c);
        } else {
            cargarUsuariosSinAsignar();
        }
    }

    // Usuarios que NO tienen ninguna asignación en RolConciertoUsuario
    private void cargarUsuariosSinAsignar() {
        List<Usuario> todos = rolService.obtenerUsuarios();
        List<Integer> asignados = staffService.obtenerIdsUsuariosAsignados();
        List<Usuario> sinAsignar = todos.stream()
                .filter(u -> !asignados.contains(u.getIdUsuario()))
                .toList();
        tablaUsuarios.setItems(FXCollections.observableArrayList(sinAsignar));
    }

    // Usuarios asignados a un concierto específico
    private void cargarUsuariosPorConcierto(Concierto concierto) {
        List<Usuario> usuarios = staffService.obtenerUsuariosPorConcierto(concierto.getIdConcierto());
        tablaUsuarios.setItems(FXCollections.observableArrayList(usuarios));
    }

    // Obtiene el rol del usuario en el concierto actualmente filtrado
    private String obtenerRolEnConcierto(Usuario u) {
        Object seleccionado = comboConciertoFiltro.getValue();
        if (seleccionado instanceof Concierto c) {
            return staffService.obtenerNombreRolEnConcierto(u.getIdUsuario(), c.getIdConcierto());
        }
        return "Sin asignar";
    }

    private void agregarBoton() {
        colAccion.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Asignar");
            {
                btn.setOnAction(event -> {
                    Usuario u = getTableView().getItems().get(getIndex());
                    mostrarPopupRol(u);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    btn.setDisable(false);//pequeño cambio para que el boton estuviera disponible siempre para asignar mas roles asi ya tenga
                    setGraphic(btn);
                }
            }
        });
    }

    private void mostrarPopupRol(Usuario u) {
        List<Rol> roles = rolService.obtenerRolesAsignables();

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Asignar Rol");
        dialog.setHeaderText("Asignar rol a: " + u.getNombre());

        ComboBox<Rol> comboRoles = new ComboBox<>();
        comboRoles.getItems().addAll(roles);
        comboRoles.setPromptText("Seleccionar rol");

        VBox content = new VBox(10);
        content.getChildren().addAll(new Label("Rol:"), comboRoles);


        Object seleccionado = comboConciertoFiltro.getValue();
        boolean tieneConcierto = seleccionado instanceof Concierto;

        ComboBox<Concierto> comboConciertos = new ComboBox<>();

        if (!tieneConcierto) {
            List<Concierto> conciertos = conciertoService.obtenerConciertosSolos();
            comboConciertos.getItems().addAll(conciertos);
            comboConciertos.setPromptText("Seleccionar concierto");

            comboConciertos.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Concierto item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) setText(null);
                    else setText(item.getNombreConcierto());
                }
            });
            comboConciertos.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Concierto item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) setText(null);
                    else setText(item.getNombreConcierto());
                }
            });

            content.getChildren().addAll(new Label("Concierto:"), comboConciertos);
        }

        dialog.getDialogPane().setContent(content);

        ButtonType confirmar = new ButtonType("Confirmar", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmar, cancelar);

        Optional<ButtonType> resultado = dialog.showAndWait();
        if (resultado.isPresent() && resultado.get() == confirmar) {
            Rol rolSeleccionado = comboRoles.getValue();

            if (rolSeleccionado == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Campos vacíos");
                alert.setHeaderText("Selecciona un rol");
                alert.showAndWait();
                return;
            }

            if (tieneConcierto) {
                // Usar el concierto del filtro directamente
                Concierto conciertoFiltro = (Concierto) seleccionado;
                staffService.asignarStaffAConcierto(
                        u.getIdUsuario(),
                        conciertoFiltro.getIdConcierto(),
                        rolSeleccionado.getIdRol()
                );
            } else {

                Concierto conciertoSeleccionado = comboConciertos.getValue();
                if (conciertoSeleccionado == null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Campos vacíos");
                    alert.setHeaderText("Selecciona un concierto");
                    alert.showAndWait();
                    return;
                }
                staffService.asignarStaffAConcierto(
                        u.getIdUsuario(),
                        conciertoSeleccionado.getIdConcierto(),
                        rolSeleccionado.getIdRol()
                );
            }
            actualizarTabla();
        }
    }

    @FXML
    void On_volver(ActionEvent event) throws IOException {
        sceneManager.showMenu();
    }
}






