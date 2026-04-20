package org.example.ax0006.Entity;

public class Objeto {

    private int idObjeto;
    private ModeloObjeto modelo;
    private Inventario inventario;
    private String estado; // DISPONIBLE, DAÑADO, etc
    private String observaciones;
    private boolean disponible;

    public Objeto() {}

    public Objeto(int idObjeto, ModeloObjeto modelo, Inventario inventario,
                  String estado, String observaciones, boolean disponible) {
        this.idObjeto = idObjeto;
        this.modelo = modelo;
        this.inventario = inventario;
        this.estado = estado;
        this.observaciones = observaciones;
        this.disponible = disponible;
    }

    public int getIdObjeto() {
        return idObjeto;
    }

    public void setIdObjeto(int idObjeto) {
        this.idObjeto = idObjeto;
    }

    public ModeloObjeto getModelo() {
        return modelo;
    }

    public void setModelo(ModeloObjeto modelo) {
        this.modelo = modelo;
    }

    public Inventario getInventario() {
        return inventario;
    }

    public void setInventario(Inventario inventario) {
        this.inventario = inventario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}