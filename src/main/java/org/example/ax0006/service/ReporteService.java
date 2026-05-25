package org.example.ax0006.service;

import org.example.ax0006.dto.ReporteDashboardDTO;
import org.example.ax0006.entity.Concierto;

import java.util.List;

public class ReporteService {

    private final ConciertoService conciertoService;
    private final IngresoService ingresoService;
    private final BoleteriaService boleteriaService;

    public ReporteService(ConciertoService conciertoService,
                          IngresoService ingresoService,
                          BoleteriaService boleteriaService) {
        this.conciertoService = conciertoService;
        this.ingresoService = ingresoService;
        this.boleteriaService = boleteriaService;
    }

    public ReporteDashboardDTO generarDashboard() {

        List<Concierto> conciertos = conciertoService.listarConciertos();

        int ingresosTotales = 0;
        int eventosActivos = 0;

        String artistaMasRentable = "N/A";
        int maxIngresos = 0;

        int reportesGenerados = 0;

        for (Concierto c : conciertos) {

            if (c.isProgramado()) {
                eventosActivos++;
            }

            int ingresosConcierto = calcularIngresosConcierto(c.getIdConcierto());

            ingresosTotales += ingresosConcierto;

            if (ingresosConcierto > maxIngresos) {
                maxIngresos = ingresosConcierto;

                if (c.getArtista() != null) {
                    artistaMasRentable = c.getArtista().getNombre();
                }
            }
        }

        return new ReporteDashboardDTO(
                ingresosTotales,
                eventosActivos,
                artistaMasRentable,
                reportesGenerados
        );
    }

    private int calcularIngresosConcierto(int idConcierto) {

        int ingresos = ingresoService.obtenerTotalIngresos(idConcierto);
        int boleteria = boleteriaService.obtenerTotalBoleteria(idConcierto);

        return ingresos + boleteria;
    }
}