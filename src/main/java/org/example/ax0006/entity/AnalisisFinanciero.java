package org.example.ax0006.entity;

public class AnalisisFinanciero {

    private int idAnalisisF;
    private int presupuesto;
    private boolean aprobado;
  
    public AnalisisFinanciero() {}

    public AnalisisFinanciero(int idAnalisisF, int presupuesto, boolean aprobado) {
        this.idAnalisisF = idAnalisisF;
        this.presupuesto = presupuesto;
        this.aprobado = aprobado;
    }

    public int getIdAnalisisF() {
        return idAnalisisF;
    }

    public void setIdAnalisisF(int idAnalisisF) {
        this.idAnalisisF = idAnalisisF;
    }

    public int getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(int presupuesto) {
        this.presupuesto = presupuesto;
    }

    public boolean isAprobado(){
        return aprobado;
    }

    public void setAprobado(boolean aprobado){
        this.aprobado = aprobado;
    }
}