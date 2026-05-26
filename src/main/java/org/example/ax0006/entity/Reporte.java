package org.example.ax0006.entity;

import java.sql.Timestamp;

public class Reporte {
    private int idReporte;
    private int idConcierto;
    private String nombreConcierto;
    private String tipo;
    private Timestamp fechaGeneracion;
    private String contenido;

    public Reporte() {}

    public Reporte(int idReporte, int idConcierto, String nombreConcierto, String tipo, Timestamp fechaGeneracion, String contenido) {
        this.idReporte = idReporte;
        this.idConcierto = idConcierto;
        this.nombreConcierto = nombreConcierto;
        this.tipo = tipo;
        this.fechaGeneracion = fechaGeneracion;
        this.contenido = contenido;
    }

    public int getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(int idReporte) {
        this.idReporte = idReporte;
    }

    public int getIdConcierto() {
        return idConcierto;
    }

    public void setIdConcierto(int idConcierto) {
        this.idConcierto = idConcierto;
    }

    public String getNombreConcierto() {
        return nombreConcierto;
    }

    public void setNombreConcierto(String nombreConcierto) {
        this.nombreConcierto = nombreConcierto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Timestamp getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(Timestamp fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
}
