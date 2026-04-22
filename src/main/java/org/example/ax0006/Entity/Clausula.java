package org.example.ax0006.Entity;

public class Clausula {

    //ATRIBUTOS
    private int idClausula;
    private String clausula;
    private int idContrato;

    //CONSTRUCTORES
    public Clausula() {
    }

    public Clausula(int idClausula, String clausula, int idContrato) {
        this.idClausula = idClausula;
        this.clausula = clausula;
        this.idContrato = idContrato;
    }

    //GETTERS Y SETTERS
    public int getIdClausula() {
        return idClausula;
    }

    public void setIdClausula(int idClausula) {
        this.idClausula = idClausula;
    }

    public String getClausula() {
        return clausula;
    }

    public void setClausula(String clausula) {
        this.clausula = clausula;
    }

    public int getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(int idContrato) {
        this.idContrato = idContrato;
    }
}