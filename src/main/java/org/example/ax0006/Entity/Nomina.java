package org.example.ax0006.Entity;

public class Nomina {
    private int idNomina;
    private int idConcierto;
    private int idUsuario;
    private double horasTrabajadas;   // calculadas automáticamente
    private double tarifaPorHora;      // según rol
    private double horasExtra;          // ingreso manual
    private double total;               // (horasTrabajadas * tarifa) + (horasExtra * tarifa * 1.5) por ejemplo
    private boolean pagado;             // estado

    // Constructor vacío
    public Nomina() {}

    // Constructor con campos principales
    public Nomina(int idConcierto, int idUsuario, double horasTrabajadas, double tarifaPorHora, double horasExtra, double total, boolean pagado) {
        this.idConcierto = idConcierto;
        this.idUsuario = idUsuario;
        this.horasTrabajadas = horasTrabajadas;
        this.tarifaPorHora = tarifaPorHora;
        this.horasExtra = horasExtra;
        this.total = total;
        this.pagado = pagado;
    }

    // Getters y Setters (generar con Alt+Insert)
    public int getIdNomina() { return idNomina; }
    public void setIdNomina(int idNomina) { this.idNomina = idNomina; }
    public int getIdConcierto() { return idConcierto; }
    public void setIdConcierto(int idConcierto) { this.idConcierto = idConcierto; }
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public double getHorasTrabajadas() { return horasTrabajadas; }
    public void setHorasTrabajadas(double horasTrabajadas) { this.horasTrabajadas = horasTrabajadas; }
    public double getTarifaPorHora() { return tarifaPorHora; }
    public void setTarifaPorHora(double tarifaPorHora) { this.tarifaPorHora = tarifaPorHora; }
    public double getHorasExtra() { return horasExtra; }
    public void setHorasExtra(double horasExtra) { this.horasExtra = horasExtra; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public boolean isPagado() { return pagado; }
    public void setPagado(boolean pagado) { this.pagado = pagado; }
}
