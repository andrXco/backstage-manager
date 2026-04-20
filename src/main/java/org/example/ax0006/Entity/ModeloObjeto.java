package org.example.ax0006.Entity;

public class ModeloObjeto {

    private int idModelo;
    private String nombre;
    private TipoObjeto tipoObjeto;

    public ModeloObjeto() {}

    public ModeloObjeto(int idModelo, String nombre, TipoObjeto tipoObjeto) {
        this.idModelo = idModelo;
        this.nombre = nombre;
        this.tipoObjeto = tipoObjeto;
    }

    public int getIdModelo() {
        return idModelo;
    }

    public void setIdModelo(int idModelo) {
        this.idModelo = idModelo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoObjeto getTipoObjeto() {
        return tipoObjeto;
    }

    public void setTipoObjeto(TipoObjeto tipoObjeto) {
        this.tipoObjeto = tipoObjeto;
    }
}