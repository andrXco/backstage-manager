package org.example.ax0006.Entity;
import java.time.LocalDate;
import java.time.LocalTime;

public class Horario {
    /*Implementacion basica de horario, puede cambiar*/
    private int idHorario;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public Horario(){}

    public Horario(int idHorario, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        this.idHorario = idHorario;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    /*GETTERS Y SETTERS*/
    public int getIdHorario() {
        return idHorario;
    }

    public void setIdHorario(int idHorario) {
        this.idHorario = idHorario;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }
}
