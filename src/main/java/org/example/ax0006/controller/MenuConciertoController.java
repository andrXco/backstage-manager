package org.example.ax0006.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.example.ax0006.manager.SceneManager;
import org.example.ax0006.manager.SesionManager;

import java.io.IOException;

public class MenuConciertoController {


    @FXML
    private Button fid_bt_CrearConcierto;

    @FXML
    private Button fid_bt_ConsultarSolicitudes;

    @FXML
    private Button fid_bt_ConsultarProgramados;

    @FXML
    private Button fid_bt_CrearContrato;

    @FXML
    private Button fid_bt_ConsultarContrato;

    @FXML
    private Button fid_bt_volver;



    /* ATRIBUTOS */
    private SceneManager sceneManager;
    private SesionManager sesion;

    /* CONSTRUCTOR */
    public MenuConciertoController(SceneManager sceneManager, SesionManager sesion) {
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

    /* CREAR SOLICITUD DE CONCIERTO */
    @FXML
    void On_CrearConcierto(ActionEvent event) {
        try {
            sceneManager.showCrearConcierto();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* CONSULTAR SOLICITUDES (no programados) */
    @FXML
    void On_ConsultarConciertos(ActionEvent event) {
        try {
            sceneManager.showConsultarSolicitudes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* CONSULTAR CONCIERTOS PROGRAMADOS */
    @FXML
    void On_ConsultarCProgramado(ActionEvent event) {
        try {

            sceneManager.showConciertosProgramados();

        } catch (IOException e) {

            e.printStackTrace();

        }
    }



   //control de vistas:
    @FXML
    public void initialize() {
        int idRol = sesion.getUsuarioActual().getIdRol();

        fid_bt_CrearConcierto.setVisible(idRol == 3);
        fid_bt_CrearConcierto.setManaged(idRol == 3);

        fid_bt_ConsultarSolicitudes.setVisible(idRol == 1);
        fid_bt_ConsultarSolicitudes.setManaged(idRol == 1);

        fid_bt_ConsultarProgramados.setVisible(idRol == 1);
        fid_bt_ConsultarProgramados.setManaged(idRol == 1);

        if (idRol == 0) {
            fid_bt_CrearConcierto.setVisible(false);
            fid_bt_CrearConcierto.setManaged(false);
            fid_bt_ConsultarSolicitudes.setVisible(false);
            fid_bt_ConsultarSolicitudes.setManaged(false);
            fid_bt_ConsultarProgramados.setVisible(false);
            fid_bt_ConsultarProgramados.setManaged(false);
        }


    }




}