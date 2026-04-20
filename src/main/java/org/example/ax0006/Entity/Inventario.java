package org.example.ax0006.Entity;

public class Inventario {

    private int idInventario;
    private String nombre;

    public Inventario() {}

    public Inventario(int idInventario, String nombre) {
        this.idInventario = idInventario;
        this.nombre = nombre;
    }

    public int getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}