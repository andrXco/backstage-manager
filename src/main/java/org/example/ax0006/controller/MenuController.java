package org.example.ax0006.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.example.ax0006.manager.SceneManager;
import org.example.ax0006.manager.SesionManager;
import org.example.ax0006.service.ConciertoService;

import java.io.IOException;

public class MenuController {

    private SceneManager sceneManager;
    private SesionManager sesion;
    private ConciertoService conciertoService;

    public MenuController() {
    }

    public MenuController(SceneManager sceneManager, SesionManager sesion, ConciertoService conciertoService) {
        this.sceneManager = sceneManager;
        this.sesion = sesion;
        this.conciertoService = conciertoService;
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void setSesion(SesionManager sesion) {
        this.sesion = sesion;
    }

    public void setConciertoService(ConciertoService conciertoService) {
        this.conciertoService = conciertoService;
    }

    @FXML
    private Label fid_Bienvenido;

    @FXML
    private Button fid_bt_volver;

    @FXML
    private Button fid_Menu_Conciertos;

    @FXML 
    private Button fid_Menu_Finanzas;

    @FXML
    private Button fid_bt_admin;

    @FXML
    private Label fid_lbl_concierto;

    @FXML
    private Button fid_bt_crearObjeto;

    @FXML
    private Button fid_bt_mantenimientoObjeto;


    @FXML
    public void initialize() {
        if (sesion != null && sesion.getUsuarioActual() != null) {
            fid_Bienvenido.setText("Bienvenido " + sesion.getUsuarioActual().getNombre());

            boolean esAdmin = sesion.getUsuarioActual().getIdRol() == 1;
            boolean esTecnico = sesion.getUsuarioActual().getIdRol() == 2;
            boolean esManager = sesion.getUsuarioActual().getIdRol() == 3;
            fid_bt_admin.setVisible(esAdmin);
            fid_bt_admin.setManaged(esAdmin);

            fid_bt_crearObjeto.setVisible(esAdmin);
            fid_bt_crearObjeto.setManaged(esAdmin);

            fid_bt_mantenimientoObjeto.setVisible(esAdmin);
            fid_bt_mantenimientoObjeto.setManaged(esAdmin);

            fid_bt_mantenimientoObjeto.setVisible(esManager);
            fid_bt_mantenimientoObjeto.setManaged(esManager);

            fid_bt_mantenimientoObjeto.setVisible(esAdmin);
            fid_bt_mantenimientoObjeto.setManaged(esAdmin);



            if (sesion.getConciertoActual() != null) {
                fid_lbl_concierto.setText("Concierto: " + sesion.getConciertoActual().getNombreConcierto());
            } else {
                fid_lbl_concierto.setText("");
            }
        }
    }

    public void setNombreBienvenido() {
        if (sesion != null && sesion.getUsuarioActual() != null) {
            fid_Bienvenido.setText("Bienvenido " + sesion.getUsuarioActual().getNombre());

            boolean esAdmin = sesion.getUsuarioActual().getIdRol() == 1;
            fid_bt_admin.setVisible(esAdmin);
            fid_bt_admin.setManaged(esAdmin);

        }
    }

    @FXML
    void On_btvolver(ActionEvent event) throws IOException {
        sesion.cerrarSesion();
        sceneManager.showLogin();
    }

    @FXML
    void On_admin(ActionEvent event) throws IOException {
        sceneManager.showAdminUsuarios();
    }

    @FXML
    void On_Perfil(ActionEvent event) {
        try {
            sceneManager.showProfile();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo abrir la ventana de perfil");
            alert.setContentText("Ocurrió un problema al cargar la vista.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    public void On_irMenuFinanzas(ActionEvent event) throws IOException {
        sceneManager.showMenuFinanzas();
    }

    @FXML
    void On_Menu_Conciertos(ActionEvent event) throws IOException {
        sceneManager.showMenuConcierto();
    }

    @FXML
    void on_bt_crearObjeto(ActionEvent event) throws IOException {
        sceneManager.showCrearObjeto();
    }

    @FXML
    void on_bt_MantenimientoObjeto() throws IOException {
        sceneManager.showMantenimiento();
    }



    @FXML
    void On_Directorio_Staff(ActionEvent event) throws IOException {
        sceneManager.showDirectorioStaff();
    }
}