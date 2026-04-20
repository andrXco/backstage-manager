package org.example.ax0006.Entity;

public class TipoObjeto {

    private int idTipoObjeto;
    private String nombre;

    public TipoObjeto() {}

    public TipoObjeto(int idTipoObjeto, String nombre) {
        this.idTipoObjeto = idTipoObjeto;
        this.nombre = nombre;
    }

    public int getIdTipoObjeto() {
        return idTipoObjeto;
    }

    public void setIdTipoObjeto(int idTipoObjeto) {
        this.idTipoObjeto = idTipoObjeto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}