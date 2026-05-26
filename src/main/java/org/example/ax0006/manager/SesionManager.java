package org.example.ax0006.manager;

import org.example.ax0006.entity.Concierto;
import org.example.ax0006.entity.Usuario;
import org.example.ax0006.entity.Nomina;

public class SesionManager {

    private Usuario usuarioActual;

    private Concierto conciertoTemporal;

    private Integer idContratoTemporal;

    private String pantallaOrigen;

    // NUEVO
    private Concierto conciertoActual;
    private Nomina nominaSeleccionada;

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public Usuario getUsuarioLogueado() {
        return usuarioActual;
    }

    public void setUsuarioActual(
            Usuario usuarioActual
    ) {

        this.usuarioActual = usuarioActual;
    }

    public void cerrarSesion() {

        this.usuarioActual = null;
        this.conciertoTemporal = null;
        this.idContratoTemporal = null;
        this.pantallaOrigen = null;

        // NUEVO
        this.conciertoActual = null;
    }

    public void setConciertoTemporal(
            Concierto conciertoTemporal
    ) {

        this.conciertoTemporal = conciertoTemporal;
    }

    public Concierto getConciertoTemporal() {
        return conciertoTemporal;
    }

    public void setIdContratoTemporal(
            Integer idContratoTemporal
    ) {

        this.idContratoTemporal = idContratoTemporal;
    }

    public Integer getIdContratoTemporal() {
        return idContratoTemporal;
    }

    public void setPantallaOrigen(
            String pantallaOrigen
    ) {

        this.pantallaOrigen = pantallaOrigen;
    }

    public String getPantallaOrigen() {
        return pantallaOrigen;
    }

    // =========================
    // CONCIERTO ACTUAL
    // =========================
    public Concierto getConciertoActual() {
        return conciertoActual;
    }

    public void setConciertoActual(
            Concierto conciertoActual
    ) {

        this.conciertoActual = conciertoActual;
    }

    public Nomina getNominaSeleccionada() { return nominaSeleccionada; }

    public void setNominaSeleccionada(Nomina nominaSeleccionada) { this.nominaSeleccionada = nominaSeleccionada; }
}