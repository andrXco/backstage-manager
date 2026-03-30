package org.example.ax0006.Controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import org.example.ax0006.Manager.SceneManager;
import org.example.ax0006.Service.RolService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.ax0006.Entity.Usuario;
import org.example.ax0006.Manager.SesionManager;
import javafx.event.ActionEvent;
import java.io.IOException;
import org.example.ax0006.Entity.Rol;
import org.example.ax0006.Repository.RolRepository;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.util.List;

public class AdminUsuariosController {

    private SesionManager sesion;
    private RolService rolService;
    private SceneManager sceneManager;

    public AdminUsuariosController(SesionManager sesion, RolService rolService, SceneManager sceneManager) {
        this.sesion = sesion;
        this.rolService = rolService;
        this.sceneManager = sceneManager;
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
    private TableColumn<Usuario, String> colRol;

    @FXML
    private TableColumn<Usuario, String> colNombreRol;

    @FXML
    private TableColumn<Usuario, Void> colAccion; //columna para asignar rol







    @FXML
    public void initialize() {
        if (sesion.getUsuarioActual() != null) {
            fid_Bienvenido.setText("Bienvenido " + sesion.getUsuarioActual().getNombre());
        }
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colGmail.setCellValueFactory(new PropertyValueFactory<>("gmail"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("idRol"));

        colNombreRol.setCellValueFactory(cellData -> {
            int idRol = cellData.getValue().getIdRol();
            String nombreRol = idRol == 0 ? "Sin rol" : rolService.obtenerNombreRol(idRol);
            return new SimpleStringProperty(nombreRol);
        });

        agregarBoton();
        cargarUsuarios();
    }


    private void cargarUsuarios() {
        ObservableList<Usuario> lista = FXCollections.observableArrayList(rolService.obtenerUsuarios());
        tablaUsuarios.setItems(lista);
    }



    //Permite agregar al boton de asignar el popup para ver los roles disponbiles a asignar en cada fila de la tabla por usuario.
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
                    setGraphic(btn);
                }
            }
        });
    }


    //CARGA LOS ROLES DE LA BD
    private void mostrarPopupRol(Usuario u) {
        List<Rol> roles = rolService.obtenerRolesAsignables();

        // CREA EL POPUP
        Dialog<Rol> dialog = new Dialog<>();
        dialog.setTitle("Asignar Rol");
        dialog.setHeaderText("Selecciona un rol para: " + u.getNombre());


        ComboBox<Rol> comboRoles = new ComboBox<>();
        comboRoles.getItems().addAll(roles);
        comboRoles.setPromptText("Seleccionar rol");


        VBox content = new VBox(10);
        content.getChildren().addAll(new Label("Rol:"), comboRoles);
        dialog.getDialogPane().setContent(content);


        ButtonType confirmar = new ButtonType("Confirmar", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmar, cancelar);


        dialog.setResultConverter(bt -> {
            if (bt == confirmar) return comboRoles.getValue();
            return null;
        });

        dialog.showAndWait().ifPresent(rolSeleccionado -> {
            if (rolSeleccionado != null) {
                rolService.asignarRol(u.getIdUsuario(), rolSeleccionado.getIdRol());
                cargarUsuarios(); //actualiza la tabla
            }
        });
    }


    @FXML
    void On_volver(ActionEvent event) throws IOException {
        sceneManager.showMenu();
    }

}
