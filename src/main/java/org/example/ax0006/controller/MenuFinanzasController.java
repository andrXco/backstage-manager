package org.example.ax0006.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.example.ax0006.manager.SceneManager;
import org.example.ax0006.manager.SesionManager;

import java.io.IOException;
public class MenuFinanzasController {

    @FXML
    private Button fid_bt_CrearFinanzasConcierto;

    @FXML
    private Button fid_bt_ConsultarFinanzasConciertos;

    @FXML
    private Button fid_bt_AsignarPresupuesto;

    @FXML
    private Button fid_bt_volver;

    /* ATRIBUTOS */
    private SceneManager sceneManager;
    private SesionManager sesion;

    public MenuFinanzasController()
    {

    }

     public MenuFinanzasController(SceneManager sceneManager, SesionManager sesion) {
        this.sceneManager = sceneManager;
        this.sesion = sesion;
    }

    /* BOTÓN VOLVER (vuelve al menu) */
    @FXML
    void On_btvolver(ActionEvent event) {
        try {
            sceneManager.showMenu();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* CREAR FINANZAS DE UN CONCIERTO */
    @FXML
    void On_CrearFinanzasConcierto(ActionEvent event) {
        try {
            sceneManager.showAnalisisFinanciero();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* CONSULTAR FINANZAS DE UN CONCIERTO */
    @FXML
    void On_ConsultarFinanzasConcierto(ActionEvent event) {

        try {

            sceneManager.showConsultarFinanzas();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @FXML
    void On_AsignarPresupuesto(ActionEvent event) {
        try {
            sceneManager.showSeleccionarConciertoFinanzas();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    
}
