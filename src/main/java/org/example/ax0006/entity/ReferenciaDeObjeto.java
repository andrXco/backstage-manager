package org.example.ax0006.entity;

public class ReferenciaDeObjeto {
    private int idReferenciaObjeto;
    private String referencia;

    public ReferenciaDeObjeto() {}

    public ReferenciaDeObjeto(int idReferenciaObjeto, String referencia) {
        this.idReferenciaObjeto = idReferenciaObjeto;
        this.referencia = referencia;
    }

    public int getIdReferenciaObjeto() { return idReferenciaObjeto; }
    public void setIdReferenciaObjeto(int idReferenciaObjeto) { this.idReferenciaObjeto = idReferenciaObjeto; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
}