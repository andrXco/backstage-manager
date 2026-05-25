package org.example.ax0006.entity;


public class Gasto {
    
    private int idGasto;
    private String descripcion;
    private int valor;
    private int idAnalisisF;

    // Constructor vacío
    public Gasto() {
    }

    // Constructor lleno 
    public Gasto(int idGasto, String descripcion, int valor, int idAnalisisF) {
        this.idGasto = idGasto;
        this.descripcion = descripcion;
        this.valor = valor;
        this.idAnalisisF = idAnalisisF;
    }

    // Getters y Setters
    public int getIdGasto() {
        return idGasto;
    }

    public void setIdGasto(int idGasto) {
        this.idGasto = idGasto;
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