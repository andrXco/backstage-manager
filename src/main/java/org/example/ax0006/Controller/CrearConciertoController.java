package org.example.ax0006.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.example.ax0006.Entity.Concierto;
import org.example.ax0006.Entity.Horario;
import org.example.ax0006.Manager.SceneManager;
import org.example.ax0006.Manager.SesionManager;
import org.example.ax0006.Service.ConciertoService;

import java.io.IOException;
import java.time.LocalTime;

public class CrearConciertoController {
    /*En esta pantalla se crean los concietos, pero queda pendendiente el analisis financiero y la implementacion mas compleja del horario*/
    private SesionManager sesion;
    private ConciertoService conciertoService;
    private SceneManager sceneManager;

    public CrearConciertoController(SesionManager sesion, ConciertoService conciertoService, SceneManager sceneManager){
        this.sesion = sesion;
        this.conciertoService = conciertoService;
        this.sceneManager = sceneManager;
    }

    @FXML
    private DatePicker fid_fecha_Inc;

    @FXML
    private DatePicker fid_fecha_Fin;

    @FXML
    private TextField fid_horaInicio;

    @FXML
    private TextField fid_horaFin;

    @FXML
    private TextField fid_aforo;

    @FXML
    /*Metodo en donde se crea el concierto, obteiendo los datos de los campos*/
    void On_crearConcierto(ActionEvent event) {

        try {
            // Fecha
            java.time.LocalDate fechaInc = fid_fecha_Inc.getValue();
            java.time.LocalDate fechaFin = fid_fecha_Fin.getValue(); //CAMBIAR

            // Horas
            fid_horaInicio.setText(verifcarHora(fid_horaInicio.getText()));
            LocalTime horaInicio = LocalTime.parse(fid_horaInicio.getText());
            fid_horaFin.setText(verifcarHora(fid_horaFin.getText()));
            LocalTime horaFin = LocalTime.parse(fid_horaFin.getText());

            // Aforo
            int aforo = Integer.parseInt(fid_aforo.getText());

            // Horario
            Horario horario = new Horario();
            horario.setFechaInicio(fechaInc);
            horario.setFechaFin(fechaFin);
            horario.setHoraInicio(horaInicio);
            horario.setHoraFin(horaFin);

            // Concierto
            Concierto concierto = new Concierto();
            concierto.setHorario(horario);
            concierto.setAforo(aforo);
            concierto.setArtista(sesion.getUsuarioActual());
            concierto.setProgramado(false); // importante

            /*Genera la pantalla de exito*/
            conciertoService.crearConcierto(concierto);

            exitoConcierto();

        } catch (Exception e) {
            e.printStackTrace();
            /*Genera la pantalla de error*/
            alertaConcierto("llene todos los campos con los formatos requeridos");
        }
    }

    @FXML
    /*Boton para volver al menu*/
    void On_volver(ActionEvent event) throws IOException {
        sceneManager.showMenu();
    }


    /*Metodo para mostrar una alerta cuando se llenan mal los datos*/
    void alertaConcierto(String CausaError){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error al crear el concierto");
        alert.setHeaderText("El concierto no se pudo crear");
        alert.setContentText(CausaError);

        alert.showAndWait(); // Esto abre el POP UP
    }

    /*METODO PARA HACER QUE SALGA UNA VENTANA DE EXIITO, CUANDO SE CREA el concierto CORRECTAMENTE*/
    void exitoConcierto () throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("El concierto fue creado");
        alert.setHeaderText("El la solicitud del concierto fue creada correctamente");
        alert.setContentText("La solicitud sera revisada para que el concierto sea programado");
        alert.showAndWait(); // Esto abre el POP UP
        sceneManager.showMenuConcierto();
    }

    String verifcarHora(String hora){
        if(hora.length() == 4){
            hora = "0" + hora;
        }
        return hora;
    }
}
