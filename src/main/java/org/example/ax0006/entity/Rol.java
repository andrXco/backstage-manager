package org.example.ax0006.entity;

public class Rol {

    private int idRol;
    private String rol;

    public Rol(int idRol, String rol) {
        this.idRol = idRol;
        this.rol = rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Rol(String rol) {
        this.rol = rol;
    }

    public int getIdRol() { return idRol; }
    public String getRol() { return rol; }

    @Override
    public String toString() { return rol; }
}
