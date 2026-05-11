package org.example.ax0006.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.ax0006.entity.Concierto;
import org.example.ax0006.entity.Horario;
import org.example.ax0006.manager.SceneManager;
import org.example.ax0006.manager.SesionManager;
import org.example.ax0006.service.ConciertoService;
import java.io.IOException;
import java.time.LocalTime;

public class CrearConciertoController {

    private SesionManager sesion;
    private ConciertoService conciertoService;
    private SceneManager sceneManager;

    public CrearConciertoController(SesionManager sesion, ConciertoService conciertoService, SceneManager sceneManager){
        this.sesion = sesion;
        this.conciertoService = conciertoService;
        this.sceneManager = sceneManager;
    }

    // =========================
    // FXML
    // =========================
    @FXML private TextField fid_nombreConcierto;
    @FXML private DatePicker fid_fecha_Inc;
    @FXML private DatePicker fid_fecha_Fin;
    @FXML private TextField fid_horaInicio;
    @FXML private TextField fid_horaFin;
    @FXML private TextField fid_aforo;
    @FXML private Button fid_bt_agregarContrato;

    // =========================
    // INIT
    // =========================
    @FXML
    public void initialize() {
        Integer idContrato = sesion.getIdContratoTemporal();
        if (idContrato != null) {
            fid_bt_agregarContrato.setDisable(true);
            fid_bt_agregarContrato.setText("Contrato ya agregado");
        } else {
            fid_bt_agregarContrato.setDisable(false);
            fid_bt_agregarContrato.setText("Agregar contrato");
        }

        Concierto temp = sesion.getConciertoTemporal();
        if (temp != null) {
            fid_nombreConcierto.setText(temp.getNombreConcierto());
            if (temp.getAforo() != 0) fid_aforo.setText(String.valueOf(temp.getAforo()));
            if (temp.getHorario() != null) {
                if (temp.getHorario().getFechaInicio() != null) fid_fecha_Inc.setValue(temp.getHorario().getFechaInicio());
                if (temp.getHorario().getFechaFin() != null) fid_fecha_Fin.setValue(temp.getHorario().getFechaFin());
                if (temp.getHorario().getHoraInicio() != null) fid_horaInicio.setText(temp.getHorario().getHoraInicio().toString());
                if (temp.getHorario().getHoraFin() != null) fid_horaFin.setText(temp.getHorario().getHoraFin().toString());
            }
        }
    }

    @FXML
    void On_crearConcierto(ActionEvent event) {
        Integer idContrato = sesion.getIdContratoTemporal();
        if (idContrato == null) {
            alertaConcierto("Debe agregar un contrato antes de crear el concierto");
            return;
        }

        try {
            String nombreConcierto = fid_nombreConcierto.getText();

            if (nombreConcierto == null || nombreConcierto.isEmpty()) {
                alertaConcierto("Debe ingresar el nombre del concierto");
                return;
            }

            if (fid_aforo.getText().isEmpty()) {
                alertaConcierto("Debe ingresar el aforo");
                return;
            }

            int aforo = Integer.parseInt(fid_aforo.getText());

            if (fid_fecha_Inc.getValue() == null || fid_fecha_Fin.getValue() == null) {
                alertaConcierto("Debe seleccionar las fechas");
                return;
            }

            if (fid_horaInicio.getText().isEmpty() || fid_horaFin.getText().isEmpty()) {
                alertaConcierto("Debe ingresar las horas");
                return;
            }

            LocalTime horaInicio = LocalTime.parse(verifcarHora(fid_horaInicio.getText()));
            LocalTime horaFin = LocalTime.parse(verifcarHora(fid_horaFin.getText()));

            Horario horario = new Horario();
            horario.setFechaInicio(fid_fecha_Inc.getValue());
            horario.setFechaFin(fid_fecha_Fin.getValue());
            horario.setHoraInicio(horaInicio);
            horario.setHoraFin(horaFin);

            Concierto concierto = new Concierto();
            concierto.setNombreConcierto(nombreConcierto);
            concierto.setHorario(horario);
            concierto.setAforo(aforo);
            concierto.setArtista(sesion.getUsuarioActual());
            concierto.setProgramado(false);
            concierto.setIdContrato(idContrato);

            conciertoService.crearConcierto(concierto);

            sesion.setConciertoTemporal(null);
            sesion.setIdContratoTemporal(null);
            exitoConcierto();
        } catch (Exception e) {
            alertaConcierto("Revise los datos: " + e.getMessage());
        }
    }

    @FXML
    public void On_agregarContrato() {

        if (sesion.getIdContratoTemporal() != null) {
            alertaConcierto("Ya existe un contrato asociado a este concierto");
            return;
        }

        // INDICAR QUE SE VIENE DE CREAR CONCIERTO
        sesion.setPantallaOrigen("crearContrato");
        Concierto temp = new Concierto();
        temp.setNombreConcierto(fid_nombreConcierto.getText());
        if (!fid_aforo.getText().isEmpty()) temp.setAforo(Integer.parseInt(fid_aforo.getText()));

        Horario h = new Horario();
        h.setFechaInicio(fid_fecha_Inc.getValue());
        h.setFechaFin(fid_fecha_Fin.getValue());
        try {
            if (!fid_horaInicio.getText().isEmpty()) h.setHoraInicio(LocalTime.parse(verifcarHora(fid_horaInicio.getText())));
            if (!fid_horaFin.getText().isEmpty()) h.setHoraFin(LocalTime.parse(verifcarHora(fid_horaFin.getText())));
        } catch (Exception ignored) {}
        temp.setHorario(h);
        sesion.setConciertoTemporal(temp);

        try { sceneManager.showCrearContrato(); } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    void On_volver(ActionEvent event) throws IOException {
        sesion.setConciertoTemporal(null);
        sesion.setIdContratoTemporal(null);
        sceneManager.showMenu();
    }

    void alertaConcierto(String msg){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    void exitoConcierto () throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Concierto creado correctamente");
        alert.showAndWait();
        sceneManager.showMenuConcierto();
    }

    String verifcarHora(String hora){
            return (hora != null && hora.length() == 4) ? "0" + hora : hora;
        }
}