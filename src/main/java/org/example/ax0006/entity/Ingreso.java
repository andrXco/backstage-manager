package org.example.ax0006.entity;

public class Ingreso {

    private int idIngreso;
    private String descripcion;
    private int valor;
    private int idAnalisisF;

    // Constructor vacío
    public Ingreso() {
    }

    // Constructor lleno 
    public Ingreso(int idIngreso, String descripcion, int valor, int idAnalisisF) {
        this.idIngreso = idIngreso;
        this.descripcion = descripcion;
        this.valor = valor;
        this.idAnalisisF = idAnalisisF;
    }

    // Getters y Setters
    public int getIdIngreso() {
        return idIngreso;
    }

    public void setIdIngreso(int idIngreso) {
        this.idIngreso = idIngreso;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public int getIdAnalisisF() {
        return idAnalisisF;
    }

    public void setIdAnalisisF(int idAnalisisF) {
        this.idAnalisisF = idAnalisisF;
    }

}