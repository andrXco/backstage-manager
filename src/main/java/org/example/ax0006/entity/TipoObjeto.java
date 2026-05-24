package org.example.ax0006.entity;

public class TipoObjeto {
    private int idTipoObjeto;
    private String tipo;

    public TipoObjeto() {}

    public TipoObjeto(int idTipoObjeto, String tipo) {
        this.idTipoObjeto = idTipoObjeto;
        this.tipo = tipo;
    }

    // Getters y Setters
    public int getIdTipoObjeto() { return idTipoObjeto; }
    public void setIdTipoObjeto(int idTipoObjeto) { this.idTipoObjeto = idTipoObjeto; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}