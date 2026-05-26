package org.example.ax0006.dto;

public class ReporteDashboardDTO {
    private int ingresosTotales;
    private int eventosActivos;
    private String artistaMasRentable;
    private int reportesGenerados;

    public ReporteDashboardDTO(int ingresosTotales,
                               int eventosActivos,
                               String artistaMasRentable,
                               int reportesGenerados) {
        this.ingresosTotales = ingresosTotales;
        this.eventosActivos = eventosActivos;
        this.artistaMasRentable = artistaMasRentable;
        this.reportesGenerados = reportesGenerados;
    }

    public int getIngresosTotales() {
        return ingresosTotales;
    }

    public int getEventosActivos() {
        return eventosActivos;
    }

    public String getArtistaMasRentable() {
        return artistaMasRentable;
    }

    public int getReportesGenerados() {
        return reportesGenerados;
    }
}