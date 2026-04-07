package org.example.ax0006.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.ax0006.Entity.Usuario;
import org.example.ax0006.Repository.AsignacionStaffRepository;
import org.example.ax0006.Repository.UsuarioRepository;
import org.example.ax0006.Service.StaffService;
import org.example.ax0006.db.H2;

public class StaffController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtContrasena;
    @FXML private TextField txtGmail;
    @FXML private TableView<Usuario> tablaEmpleados;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colGmail;
    @FXML private Label lblMensaje;

    private StaffService staffService;

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombre()));
        colGmail.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getGmail()));

        H2 h2 = new H2();
        UsuarioRepository usuarioRepo = new UsuarioRepository(h2);
        AsignacionStaffRepository asignacionRepo = new AsignacionStaffRepository(h2);
        //staffService = new StaffService(usuarioRepo, asignacionRepo);

        cargarListaEmpleados();
    }

    @FXML
    private void crearEmpleado() {
        String nombre = txtNombre.getText();
        String contrasena = txtContrasena.getText();
        String gmail = txtGmail.getText();

        if (nombre.isEmpty() || contrasena.isEmpty() || gmail.isEmpty()) {
            lblMensaje.setText("Complete todos los campos");
            return;
        }

        boolean ok = staffService.crearEmpleado(nombre, contrasena, gmail);
        if (ok) {
            lblMensaje.setText("Empleado creado exitosamente");
            limpiarCampos();
            cargarListaEmpleados();
        } else {
            lblMensaje.setText("El nombre de usuario ya existe");
        }
    }

    private void cargarListaEmpleados() {
        ObservableList<Usuario> empleados = FXCollections.observableArrayList(staffService.listarEmpleados());
        tablaEmpleados.setItems(empleados);
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtContrasena.clear();
        txtGmail.clear();
    }
}