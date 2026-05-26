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
    private TableColumn<Usuario, String> colRolGlobal;

    @FXML
    private TableColumn<Usuario, String> colNombreRol;

    @FXML
    private TableColumn<Usuario, Void> colAccion; //columna para asignar rol

    @FXML
    private ComboBox<Object> comboConciertoFiltro;

    // Nueva funcionalidad: Botón para desasignar rol a un usuario seleccionado
    @FXML
    private Button fid_bt_desasignarRol;




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

        colRolGlobal.setCellValueFactory(cellData -> {
            int idRol = cellData.getValue().getIdRol();
            String nombreRol = switch (idRol) {
                case 1 -> "Administrador";
                case 2 -> "Tecnico";
                case 3 -> "Manager";
                case 4 -> "Staff";
                default -> "Sin rol";
            };
            return new SimpleStringProperty(nombreRol);
        });

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
                .filter(u -> u.getIdUsuario() != sesion.getUsuarioActual().getIdUsuario())
                .filter(u -> u.getIdRol() != 1)
                .toList();
        tablaUsuarios.setItems(FXCollections.observableArrayList(sinAsignar));
    }

    // Usuarios asignados a un concierto específico
    private void cargarUsuariosPorConcierto(Concierto concierto) {
        List<Usuario> usuarios = staffService.obtenerUsuariosPorConcierto(concierto.getIdConcierto())
                .stream()
                .filter(u -> u.getIdUsuario() != sesion.getUsuarioActual().getIdUsuario())
                .filter(u -> u.getIdRol() != 1)
                .toList();
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

        Object seleccionado = comboConciertoFiltro.getValue();
        boolean tieneConcierto = seleccionado instanceof Concierto;

        CheckBox chkRolGlobal = new CheckBox("Asignar como rol global");
        Label labelConcierto = new Label("Concierto:");
        ComboBox<Concierto> comboConciertos = new ComboBox<>();

        VBox content = new VBox(10);
        content.getChildren().addAll(new Label("Rol:"), comboRoles);

        if (tieneConcierto) {
            content.getChildren().add(chkRolGlobal);

            // Deshabilitar checkbox si el rol no puede ser global
            comboRoles.setOnAction(e -> {
                Rol rolElegido = comboRoles.getValue();
                if (rolElegido != null) {
                    boolean puedeSerGlobal = rolElegido.getIdRol() == 1 || rolElegido.getIdRol() == 3;
                    chkRolGlobal.setDisable(!puedeSerGlobal);
                    if (!puedeSerGlobal) chkRolGlobal.setSelected(false);
                }
            });

        } else {
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

            content.getChildren().addAll(chkRolGlobal, labelConcierto, comboConciertos);

            // Deshabilitar checkbox si el rol no puede ser global
            comboRoles.setOnAction(e -> {
                Rol rolElegido = comboRoles.getValue();
                if (rolElegido != null) {
                    boolean puedeSerGlobal = rolElegido.getIdRol() == 1 || rolElegido.getIdRol() == 3;
                    chkRolGlobal.setDisable(!puedeSerGlobal);
                    if (!puedeSerGlobal) {
                        chkRolGlobal.setSelected(false);
                        labelConcierto.setVisible(true);
                        labelConcierto.setManaged(true);
                        comboConciertos.setVisible(true);
                        comboConciertos.setManaged(true);
                    }
                }
            });

            chkRolGlobal.setOnAction(e -> {
                boolean global = chkRolGlobal.isSelected();
                labelConcierto.setVisible(!global);
                labelConcierto.setManaged(!global);
                comboConciertos.setVisible(!global);
                comboConciertos.setManaged(!global);
            });
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
                if (chkRolGlobal.isSelected()) {
                    rolService.actualizarRolGlobal(u.getIdUsuario(), rolSeleccionado.getIdRol());
                } else {
                    // Usar el concierto del filtro directamente
                    Concierto conciertoFiltro = (Concierto) seleccionado;

                    // Se asigna el rol seleccionado sin borrar roles anteriores del mismo usuario
                    // El subrol se envia como null porque se gestiona desde la pantalla DirectorioStaff
                    boolean asignado = staffService.asignarStaffAConcierto(
                            u.getIdUsuario(),
                            conciertoFiltro.getIdConcierto(),
                            rolSeleccionado.getIdRol(),
                            null
                    );

                    if (!asignado) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Asignación duplicada");
                        alert.setHeaderText("Este usuario ya tiene ese rol en este concierto");
                        alert.showAndWait();
                        return;
                    }

                    comboConciertoFiltro.setValue(conciertoFiltro);
                }
            } else if (chkRolGlobal.isSelected()) {
                rolService.actualizarRolGlobal(u.getIdUsuario(), rolSeleccionado.getIdRol());
            } else {
                Concierto conciertoSeleccionado = comboConciertos.getValue();
                if (conciertoSeleccionado == null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Campos vacíos");
                    alert.setHeaderText("Selecciona un concierto");
                    alert.showAndWait();
                    return;
                }

                // Se asigna el rol seleccionado sin borrar roles anteriores del mismo usuario
                // El subrol se envia como null porque se gestiona desde la pantalla DirectorioStaff
                boolean asignado = staffService.asignarStaffAConcierto(
                        u.getIdUsuario(),
                        conciertoSeleccionado.getIdConcierto(),
                        rolSeleccionado.getIdRol(),
                        null
                );

                if (!asignado) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Asignación duplicada");
                    alert.setHeaderText("Este usuario ya tiene ese rol en este concierto");
                    alert.showAndWait();
                    return;
                }

                comboConciertoFiltro.setValue(conciertoSeleccionado);
            }
            actualizarTabla();
        }
    }

    // Nueva funcionalidad: Muestra popup para elegir cuál rol específico desasignar
    // (funciona tanto si tiene 1 rol como si tiene varios en el mismo concierto)
    @FXML
    void desasignarRol(ActionEvent event) {
        Usuario u = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (u == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ningún usuario seleccionado");
            alert.setHeaderText("Por favor selecciona un usuario de la tabla");
            alert.showAndWait();
            return;
        }

        Object seleccionado = comboConciertoFiltro.getValue();
        if (!(seleccionado instanceof Concierto c)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Acción no disponible");
            alert.setHeaderText("Debes filtrar por un concierto específico para desasignar un rol");
            alert.showAndWait();
            return;
        }

        String rolesActuales = staffService.obtenerNombreRolEnConcierto(u.getIdUsuario(), c.getIdConcierto());

        if (rolesActuales.equals("Sin rol")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Sin roles");
            alert.setHeaderText("Este usuario no tiene roles asignados en este concierto");
            alert.showAndWait();
            return;
        }

        // Crear popup para elegir el rol a desasignar
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Desasignar Rol");
        dialog.setHeaderText("Usuario: " + u.getNombre() + "\nConcierto: " + c.getNombreConcierto());

        ComboBox<String> comboRoles = new ComboBox<>();
        String[] rolesLista = rolesActuales.split(", ");
        comboRoles.getItems().addAll(rolesLista);
        comboRoles.setValue(rolesLista[0]); // seleccionar el primero por defecto

        VBox content = new VBox(10);
        content.getChildren().addAll(new Label("Selecciona el rol a desasignar:"), comboRoles);
        dialog.getDialogPane().setContent(content);

        ButtonType confirmar = new ButtonType("Desasignar", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmar, cancelar);

        Optional<ButtonType> resultado = dialog.showAndWait();

        if (resultado.isPresent() && resultado.get() == confirmar) {
            String rolSeleccionado = comboRoles.getValue();

            int idRol = switch (rolSeleccionado) {
                case "Administrador" -> 1;
                case "Tecnico" -> 2;
                case "Manager" -> 3;
                case "Staff" -> 4;
                default -> -1;
            };

            if (idRol == -1) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("No se pudo identificar el rol seleccionado");
                alert.showAndWait();
                return;
            }

            boolean desasignado = staffService.desasignarRolEspecifico(u.getIdUsuario(), c.getIdConcierto(), idRol);

            if (desasignado) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Rol desasignado");
                alert.setHeaderText("Se eliminó el rol: " + rolSeleccionado);
                alert.showAndWait();
                actualizarTabla();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText("No se pudo desasignar el rol");
                alert.showAndWait();
            }
        }
    }

    @FXML
    void On_volver(ActionEvent event) throws IOException {
        sceneManager.showMenu();
    }
}