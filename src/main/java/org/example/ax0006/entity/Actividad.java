package org.example.ax0006.entity;

import java.time.LocalDateTime;

public class Actividad {
    private int idActividad;
    private String tipo;
    private String modulo;
    private String origen;
    private String descripcion;
    private LocalDateTime fechaHora;
    private Integer idUsuarioActor;
    private String nombreUsuarioActor;
    private String rolDestino;
    private boolean revisado;

    public int getIdActividad() { return idActividad; }
    public void setIdActividad(int idActividad) { this.idActividad = idActividad; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getModulo() { return modulo; }
    public void setModulo(String modulo) { this.modulo = modulo; }

    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public Integer getIdUsuarioActor() { return idUsuarioActor; }
    public void setIdUsuarioActor(Integer idUsuarioActor) { this.idUsuarioActor = idUsuarioActor; }

    public String getNombreUsuarioActor() { return nombreUsuarioActor; }
    public void setNombreUsuarioActor(String nombreUsuarioActor) { this.nombreUsuarioActor = nombreUsuarioActor; }

    public String getRolDestino() { return rolDestino; }
    public void setRolDestino(String rolDestino) { this.rolDestino = rolDestino; }

    public boolean isRevisado() { return revisado; }
    public void setRevisado(boolean revisado) { this.revisado = revisado; }
}