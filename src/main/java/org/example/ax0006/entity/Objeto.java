package org.example.ax0006.entity;

public class Objeto {
    private int idObjeto;
    private TipoObjeto tipoObjeto;
    private ReferenciaDeObjeto referenciaDeObjeto;

    public Objeto() {}

    public Objeto(int idObjeto, TipoObjeto tipoObjeto, ReferenciaDeObjeto referenciaDeObjeto) {
        this.idObjeto = idObjeto;
        this.tipoObjeto = tipoObjeto;
        this.referenciaDeObjeto = referenciaDeObjeto;
    }

    public int getIdObjeto() { return idObjeto; }
    public void setIdObjeto(int idObjeto) { this.idObjeto = idObjeto; }

    public TipoObjeto getTipoObjeto() { return tipoObjeto; }
    public void setTipoObjeto(TipoObjeto tipoObjeto) { this.tipoObjeto = tipoObjeto; }

    public ReferenciaDeObjeto getReferenciaDeObjeto() { return referenciaDeObjeto; }
    public void setReferenciaDeObjeto(ReferenciaDeObjeto referenciaDeObjeto) { this.referenciaDeObjeto = referenciaDeObjeto; }
}