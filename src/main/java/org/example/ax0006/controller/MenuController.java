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
    public void initialize() {
        if (sesion != null && sesion.getUsuarioActual() != null && fid_Bienvenido != null) {
            fid_Bienvenido.setText("Bienvenido " + sesion.getUsuarioActual().getNombre());
        }
    }

    public void setNombreBienvenido() {
        if (sesion != null && sesion.getUsuarioActual() != null) {
            fid_Bienvenido.setText("Bienvenido " + sesion.getUsuarioActual().getNombre());
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
    void On_Menu_Conciertos(ActionEvent event) throws IOException {
        sceneManager.showMenuConcierto();
    }
}