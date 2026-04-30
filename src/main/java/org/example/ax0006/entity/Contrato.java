package org.example.ax0006.entity;

import java.time.LocalDate;
import java.util.List;

public class Contrato {
    //ATRIBUTOS
    private int idContrato;
    private LocalDate fecha;
    private List<Clausula> clausulas;

    //CONTRUCTORES
    public Contrato() {
    }

    public Contrato(int idContrato, LocalDate fecha) {
        this.idContrato = idContrato;
        this.fecha = fecha;
    }

    //GETTERS Y SETTERS
    public int getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(int idContrato) {
        this.idContrato = idContrato;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public List<Clausula> getClausulas() {
    return clausulas;
    }

    public void setClausulas(List<Clausula> clausulas) {
        this.clausulas = clausulas;
    }
}