package org.example.ax0006.entity;

public class Boleteria {
    private int idBoleteria;
    private String seccion;
    private int cantidad;
    private int precioBoleta;
    private int ingresoTotal;
    private int idAnalisisF;

    // Constructor vacío
    public Boleteria() {
    }

    // Constructor lleno 
    public Boleteria(int idBoleteria, String seccion, int cantidad, int precioBoleta, int ingresoTotal, int idAnalisisF) {
        this.idBoleteria = idBoleteria;
        this.seccion = seccion;
        this.cantidad = cantidad;
        this.precioBoleta = precioBoleta;
        this.ingresoTotal = ingresoTotal;
        this.idAnalisisF = idAnalisisF;
    }

    // Getters y Setters
    public int getIdBoleteria() {
        return idBoleteria;
    }

    public void setIdBoleteria(int idBoleteria) {
        this.idBoleteria = idBoleteria;
    }

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getPrecioBoleta() {
        return precioBoleta;
    }

    public void setPrecioBoleta(int precioBoleta) {
        this.precioBoleta = precioBoleta;
    }

    public int getIngresoTotal() {
        return ingresoTotal;
    }

    public void setIngresoTotal(int ingresoTotal) {
        this.ingresoTotal = ingresoTotal;
    }

    public int getIdAnalisisF() {
        return idAnalisisF;
    }

    public void setIdAnalisisF(int idAnalisisF) {
        this.idAnalisisF = idAnalisisF;
    }

    // Método útil para calcular ingresoTotal automáticamente
    public void calcularIngresoTotal() {
        this.ingresoTotal = this.cantidad * this.precioBoleta;
    }
}