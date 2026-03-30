/*
 * MARTIN SANMIGUEL
 */



package org.example.ax0006.Entity;

public class Usuario {
    private int idUsuario;
    private String nombre;
    private String contrasena;
    private String gmail;
    private int idRol;

    public Usuario() {
    }

    public Usuario(int idUsuario, String nombre, String contrasena, String email, int idRol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.contrasena = contrasena;
        this.gmail = gmail;
        this.idRol = idRol;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}

