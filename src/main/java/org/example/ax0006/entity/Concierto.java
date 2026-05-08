package org.example.ax0006.entity;

public class Concierto {
    /*La clase concierto:
    * Pendiente a implementar: Horario(actualmente es una implementacion basica), contrato, analisis financiero*/
    private int idConcierto;
    private Horario horario;
    private String nombreConcierto;
    private int aforo;
    private Contrato contrato;
    private boolean programado;
    private AnalisisFinanciero analisis;
    private Usuario artista; //la persona lider del concierto
    private int idContrato;

    public Concierto(){}

    public Concierto(int idConcierto,String nombreConcierto, Horario horario, int aforo, Usuario artista, boolean programado) {
        this.idConcierto = idConcierto;
        this.nombreConcierto = nombreConcierto;
        this.horario = horario;
        this.aforo = aforo;
        this.contrato = null;
        this.programado = programado;
        this.analisis = null;
        this.artista = artista;
    }

    /*GETTERS Y SETTERS*/

    public int getIdConcierto() {
        return idConcierto;
    }

    public void setIdConcierto(int idConcierto) {
        this.idConcierto = idConcierto;
    }

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    public int getAforo() {
        return aforo;
    }

    public void setAforo(int aforo) {
        this.aforo = aforo;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public boolean isProgramado() {
        return programado;
    }

    public void setProgramado(boolean programado) {
        this.programado = programado;
    }

    public AnalisisFinanciero getAnalisis() {
        return analisis;
    }

    public void setAnalisis(AnalisisFinanciero analisis) {
        this.analisis = analisis;
    }

    public Usuario getArtista() {
        return artista;
    }

    public void setArtista(Usuario artista) {
        this.artista = artista;
    }

    public String getNombreConcierto() {
        return nombreConcierto;
    }

    public void setNombreConcierto(String nombreConcierto) {
        this.nombreConcierto = nombreConcierto;
    }

    public int getIdContrato()
    {
        return idContrato;
    }

    public void setIdContrato(int idContrato)
    {
        this.idContrato = idContrato;
    }
}
