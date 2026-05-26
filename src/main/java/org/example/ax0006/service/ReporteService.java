package org.example.ax0006.service;

import org.example.ax0006.dto.ReporteDashboardDTO;
import org.example.ax0006.entity.Concierto;
import org.example.ax0006.entity.Reporte;
import org.example.ax0006.entity.Usuario;
import org.example.ax0006.repository.ReporteRepository;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.*;

public class ReporteService {

    private final ConciertoService conciertoService;
    private final IngresoService ingresoService;
    private final BoleteriaService boleteriaService;
    private final GastoService gastoService;
    private final NominaService nominaService;
    private final ReporteRepository reporteRepo;
    private final NumberFormat currencyFormatter;

    public ReporteService(ConciertoService conciertoService,
                          IngresoService ingresoService,
                          BoleteriaService boleteriaService,
                          GastoService gastoService,
                          NominaService nominaService,
                          ReporteRepository reporteRepo) {
        this.conciertoService = conciertoService;
        this.ingresoService = ingresoService;
        this.boleteriaService = boleteriaService;
        this.gastoService = gastoService;
        this.nominaService = nominaService;
        this.reporteRepo = reporteRepo;
        
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        this.currencyFormatter.setMaximumFractionDigits(0);
    }

    public ReporteDashboardDTO generarDashboard() {
        List<Concierto> conciertos = conciertoService.listarConciertos();

        int ingresosTotales = 0;
        int eventosActivos = 0;
        int reportesGenerados = reporteRepo.obtenerTotalReportes();

        Map<String, Integer> rentabilidadPorArtista = new HashMap<>();

        for (Concierto c : conciertos) {
            if (c.isProgramado()) {
                eventosActivos++;
            }

            int idAnalisis = (c.getAnalisis() != null) ? c.getAnalisis().getIdAnalisisF() : 0;
            if (idAnalisis > 0) {
                int ingresosConcierto = calcularIngresosConcierto(idAnalisis);
                int gastosConcierto = calcularGastosConcierto(idAnalisis);
                int utilidadConcierto = ingresosConcierto - gastosConcierto;

                ingresosTotales += ingresosConcierto;

                if (c.getArtista() != null) {
                    String artistName = c.getArtista().getNombre();
                    rentabilidadPorArtista.put(artistName, rentabilidadPorArtista.getOrDefault(artistName, 0) + utilidadConcierto);
                }
            }
        }

        String artistaMasRentable = "N/A";
        int maxUtilidad = Integer.MIN_VALUE;
        for (Map.Entry<String, Integer> entry : rentabilidadPorArtista.entrySet()) {
            if (entry.getValue() > maxUtilidad) {
                maxUtilidad = entry.getValue();
                artistaMasRentable = entry.getKey();
            }
        }

        return new ReporteDashboardDTO(
                ingresosTotales,
                eventosActivos,
                artistaMasRentable,
                reportesGenerados
        );
    }

    public int calcularIngresosConcierto(int idAnalisisF) {
        if (idAnalisisF <= 0) return 0;
        int ingresos = ingresoService.obtenerTotalIngresos(idAnalisisF);
        int boleteria = boleteriaService.obtenerTotalBoleteria(idAnalisisF);
        return ingresos + boleteria;
    }

    public int calcularGastosConcierto(int idAnalisisF) {
        if (idAnalisisF <= 0) return 0;
        return gastoService.obtenerTotalGastos(idAnalisisF);
    }

    public String obtenerRendimientoConcierto(Concierto c) {
        if (c == null) {
            return "Selecciona un evento primero.";
        }

        int idAnalisis = (c.getAnalisis() != null) ? c.getAnalisis().getIdAnalisisF() : 0;

        int totalBoleteria = 0;
        int totalIngresosAdicionales = 0;
        int totalGastos = 0;
        int presupuesto = 0;
        boolean aprobado = false;

        if (c.getAnalisis() != null) {
            presupuesto = c.getAnalisis().getPresupuesto();
            aprobado = c.getAnalisis().isAprobado();
        }

        if (idAnalisis > 0) {
            totalBoleteria = boleteriaService.obtenerTotalBoleteria(idAnalisis);
            totalIngresosAdicionales = ingresoService.obtenerTotalIngresos(idAnalisis);
            totalGastos = gastoService.obtenerTotalGastos(idAnalisis);
        }

        double totalNomina = nominaService.obtenerTotalGeneralEvento(c.getIdConcierto());
        int cantidadRegistrosNomina = nominaService.obtenerNominasPorConcierto(c.getIdConcierto()).size();
        int totalIngresos = totalBoleteria + totalIngresosAdicionales;
        double utilidad = totalIngresos - totalGastos - totalNomina;
        double margen = totalIngresos > 0 ? ((double) utilidad / totalIngresos) * 100 : 0.0;
        double ejecucionPresupuestal = presupuesto > 0 ? ((double) totalGastos / presupuesto) * 100 : 0.0;

        Usuario artista = c.getArtista();
        if (artista == null) {
            for (Concierto full : conciertoService.listarConciertos()) {
                if (full.getIdConcierto() == c.getIdConcierto()) {
                    artista = full.getArtista();
                    break;
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("==================================================\n");
        sb.append("   RENDIMIENTO FINANCIERO DE EVENTO\n");
        sb.append("==================================================\n");
        sb.append("Concierto: ").append(c.getNombreConcierto()).append("\n");
        sb.append("ID Evento: ").append(c.getIdConcierto()).append("\n");
        sb.append("Artista Principal: ").append(artista != null ? artista.getNombre() : "No asignado").append("\n");
        sb.append("Aforo Proyectado: ").append(c.getAforo()).append(" personas\n");
        sb.append("Estado Programa: ").append(c.isProgramado() ? "Programado" : "Pendiente").append("\n");
        if (c.getHorario() != null) {
            sb.append("Horario: ").append(c.getHorario().getFechaInicio()).append(" ").append(c.getHorario().getHoraInicio())
                    .append(" - ").append(c.getHorario().getFechaFin()).append(" ").append(c.getHorario().getHoraFin()).append("\n");
        }
        sb.append("Contrato Asociado (ID): ").append(c.getIdContrato() > 0 ? c.getIdContrato() : "N/A").append("\n");
        sb.append("ID Análisis Financiero: ").append(idAnalisis > 0 ? idAnalisis : "N/A").append("\n");
        sb.append("--------------------------------------------------\n");
        sb.append("1. INGRESOS:\n");
        sb.append("   • Venta de Boletería:     ").append(formatCurrency(totalBoleteria)).append("\n");
        sb.append("   • Otros Ingresos (Merch): ").append(formatCurrency(totalIngresosAdicionales)).append("\n");
        sb.append("   TOTAL INGRESOS BRUTOS:    ").append(formatCurrency(totalIngresos)).append("\n");
        sb.append("\n");
        sb.append("2. EGRESOS:\n");
        sb.append("   • Presupuesto Asignado:   ").append(formatCurrency(presupuesto)).append(" (Estado: ").append(aprobado ? "Aprobado" : "Pendiente").append(")\n");
        sb.append("   • Gastos Totales Realizados: ").append(formatCurrency(totalGastos)).append(" (").append(String.format("%.1f", ejecucionPresupuestal)).append("% del presupuesto)\n");
        sb.append("   • Nómina Staff Total:     ").append(formatCurrency((int) Math.round(totalNomina))).append(" (").append(cantidadRegistrosNomina).append(" registros)\n");
        sb.append("\n");
        sb.append("3. RESULTADO NETO:\n");
        sb.append("   • Utilidad Proyectada/Real: ").append(formatCurrency((int) Math.round(utilidad))).append("\n");
        sb.append("   • Margen de Ganancia Neto: ").append(String.format("%.2f", margen)).append("%\n");
        sb.append("\n");
        sb.append("4. RESUMEN DE NÓMINA POR ROL:\n");
        List<NominaService.ResumenNomina> resumenNomina = nominaService.obtenerNominaGeneralPorEvento(c.getIdConcierto());
        if (resumenNomina.isEmpty()) {
            sb.append("   • Sin registros de nómina para este evento.\n");
        } else {
            for (NominaService.ResumenNomina r : resumenNomina) {
                sb.append("   • ").append(r.rol).append(": ").append(r.cantidad)
                        .append(" staff, total ").append(formatCurrency((int) Math.round(r.total))).append("\n");
            }
        }
        sb.append("==================================================\n");

        return sb.toString();
    }

    public String generarYGuardarReporte(Concierto c, String emitidoPor) {
        if (c == null) {
            return "Selecciona un evento primero.";
        }

        int idAnalisis = (c.getAnalisis() != null) ? c.getAnalisis().getIdAnalisisF() : 0;
        int totalBoleteria = 0;
        int totalIngresosAdicionales = 0;
        int totalGastos = 0;
        int presupuesto = 0;

        if (c.getAnalisis() != null) {
            presupuesto = c.getAnalisis().getPresupuesto();
        }

        if (idAnalisis > 0) {
            totalBoleteria = boleteriaService.obtenerTotalBoleteria(idAnalisis);
            totalIngresosAdicionales = ingresoService.obtenerTotalIngresos(idAnalisis);
            totalGastos = gastoService.obtenerTotalGastos(idAnalisis);
        }

        double totalNomina = nominaService.obtenerTotalGeneralEvento(c.getIdConcierto());
        int cantidadNominas = nominaService.obtenerNominasPorConcierto(c.getIdConcierto()).size();
        int totalIngresos = totalBoleteria + totalIngresosAdicionales;
        double utilidad = totalIngresos - totalGastos - totalNomina;
        double margen = totalIngresos > 0 ? ((double) utilidad / totalIngresos) * 100 : 0.0;

        String fechaString = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        Usuario artista = c.getArtista();
        if (artista == null) {
            for (Concierto full : conciertoService.listarConciertos()) {
                if (full.getIdConcierto() == c.getIdConcierto()) {
                    artista = full.getArtista();
                    break;
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("==================================================\n");
        sb.append("   REPORTE GERENCIAL OFICIAL\n");
        sb.append("   Fecha Emisión: ").append(fechaString).append("\n");
        sb.append("   Emitido Por:   ").append(emitidoPor).append("\n");
        sb.append("==================================================\n\n");
        sb.append("1. INFORMACIÓN GENERAL DEL EVENTO\n");
        sb.append("   - ID Evento:             ").append(c.getIdConcierto()).append("\n");
        sb.append("   - Nombre del Concierto: ").append(c.getNombreConcierto()).append("\n");
        sb.append("   - Artista Lider:        ").append(artista != null ? artista.getNombre() : "N/A").append("\n");
        sb.append("   - Aforo Planificado:    ").append(c.getAforo()).append(" asistentes\n");
        sb.append("   - Estado de Programación: ").append(c.isProgramado() ? "APROBADO/PROGRAMADO" : "PENDIENTE").append("\n\n");
        if (c.getHorario() != null) {
            sb.append("   - Inicio Evento:         ").append(c.getHorario().getFechaInicio()).append(" ").append(c.getHorario().getHoraInicio()).append("\n");
            sb.append("   - Fin Evento:            ").append(c.getHorario().getFechaFin()).append(" ").append(c.getHorario().getHoraFin()).append("\n");
        }
        sb.append("   - Contrato Asociado ID:  ").append(c.getIdContrato() > 0 ? c.getIdContrato() : "N/A").append("\n");
        sb.append("   - Análisis Financiero ID:").append(idAnalisis > 0 ? idAnalisis : "N/A").append("\n\n");
        sb.append("2. ANÁLISIS FINANCIERO Y CONTROL DE COSTOS\n");
        sb.append("   - Total Ingresos Boletería: ").append(formatCurrency(totalBoleteria)).append("\n");
        sb.append("   - Total Otros Ingresos:     ").append(formatCurrency(totalIngresosAdicionales)).append("\n");
        sb.append("   - INGRESO BRUTO TOTAL:      ").append(formatCurrency(totalIngresos)).append("\n");
        sb.append("   - GASTOS OPERATIVOS TOTALES:").append(formatCurrency(totalGastos)).append("\n");
        sb.append("   - NÓMINA STAFF TOTAL:       ").append(formatCurrency((int) Math.round(totalNomina))).append(" (").append(cantidadNominas).append(" registros)\n");
        sb.append("   - Presupuesto Inicial:      ").append(formatCurrency(presupuesto)).append("\n");
        sb.append("   - UTILIDAD CONSOLIDADA:     ").append(formatCurrency((int) Math.round(utilidad))).append("\n");
        sb.append("   - MARGEN DE RENTABILIDAD:   ").append(String.format("%.2f", margen)).append("%\n\n");
        sb.append("3. RESUMEN DE NÓMINA POR ROL\n");
        List<NominaService.ResumenNomina> resumenNomina = nominaService.obtenerNominaGeneralPorEvento(c.getIdConcierto());
        if (resumenNomina.isEmpty()) {
            sb.append("   - No hay registros de nómina para este evento.\n\n");
        } else {
            for (NominaService.ResumenNomina r : resumenNomina) {
                sb.append("   - Rol ").append(r.rol).append(": ")
                        .append(r.cantidad).append(" personas, total ")
                        .append(formatCurrency((int) Math.round(r.total))).append("\n");
            }
            sb.append("\n");
        }
        sb.append("4. CONCLUSIONES Y RECOMENDACIONES GERENCIALES\n");
        if (utilidad < 0) {
            sb.append("   [ALERTA RIESGO]: El concierto proyecta pérdidas. Se recomienda renegociar costos o incrementar la boletería.\n");
        } else if (margen < 20) {
            sb.append("   [RECOMENDACIÓN]: Rentabilidad por debajo del 20%. Ajustar gastos operativos para optimizar margen.\n");
        } else {
            sb.append("   [ESTADO SALUDABLE]: Excelente proyección de rentabilidad. Continuar con la fase operativa.\n");
        }
        sb.append("\n");
        sb.append("--------------------------------------------------\n");
        sb.append("Este reporte ha sido guardado e indexado en el historial de auditoría.\n");
        sb.append("==================================================\n");

        String contenidoReporte = sb.toString();

        // Guardar en base de datos
        Reporte reporte = new Reporte();
        reporte.setIdConcierto(c.getIdConcierto());
        reporte.setNombreConcierto(c.getNombreConcierto());
        reporte.setTipo("Gerencial");
        reporte.setFechaGeneracion(new Timestamp(System.currentTimeMillis()));
        reporte.setContenido(contenidoReporte);

        reporteRepo.guardar(reporte);

        return contenidoReporte;
    }

    public String obtenerArtistasRentabilidad() {
        List<Concierto> conciertos = conciertoService.listarConciertos();
        Map<String, List<Concierto>> conciertosPorArtista = new HashMap<>();

        for (Concierto c : conciertos) {
            if (c.getArtista() != null) {
                String artistName = c.getArtista().getNombre();
                conciertosPorArtista.computeIfAbsent(artistName, k -> new ArrayList<>()).add(c);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("==================================================\n");
        sb.append("   ANÁLISIS DE RENTABILIDAD DE ARTISTAS\n");
        sb.append("==================================================\n");
        sb.append(String.format("%-18s | %-8s | %-12s | %-8s\n", "Artista", "Eventos", "Utilidad Tot.", "Rentable"));
        sb.append("--------------------------------------------------\n");

        class ArtistaData {
            String nombre;
            int totalConciertos;
            int utilidadTotal;
            int ingresosTotales;
        }

        List<ArtistaData> artistList = new ArrayList<>();

        for (Map.Entry<String, List<Concierto>> entry : conciertosPorArtista.entrySet()) {
            ArtistaData ad = new ArtistaData();
            ad.nombre = entry.getKey();
            ad.totalConciertos = entry.getValue().size();

            for (Concierto c : entry.getValue()) {
                int idAnalisis = (c.getAnalisis() != null) ? c.getAnalisis().getIdAnalisisF() : 0;
                if (idAnalisis > 0) {
                    int ingresos = calcularIngresosConcierto(idAnalisis);
                    int gastos = calcularGastosConcierto(idAnalisis);
                    ad.utilidadTotal += (ingresos - gastos);
                    ad.ingresosTotales += ingresos;
                }
            }
            artistList.add(ad);
        }

        // Ordenar por utilidad decreciente
        artistList.sort((a, b) -> Integer.compare(b.utilidadTotal, a.utilidadTotal));

        for (ArtistaData ad : artistList) {
            String rentableStr = ad.utilidadTotal > 0 ? "SÍ" : "NO";
            sb.append(String.format("%-18.18s | %-8d | %-12s | %-8s\n", 
                    ad.nombre, ad.totalConciertos, formatCurrency(ad.utilidadTotal), rentableStr));
        }

        if (artistList.isEmpty()) {
            sb.append("No se registraron datos de artistas o finanzas en el sistema.\n");
        }

        sb.append("==================================================\n");
        return sb.toString();
    }

    public String obtenerHistorialReportes() {
        List<Reporte> reportes = reporteRepo.listarTodos();

        StringBuilder sb = new StringBuilder();
        sb.append("==================================================\n");
        sb.append("   HISTORIAL DE REPORTES GERENCIALES\n");
        sb.append("==================================================\n");

        if (reportes.isEmpty()) {
            sb.append("No se han generado reportes en el historial.\n");
        } else {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
            for (Reporte r : reportes) {
                String fecha = r.getFechaGeneracion() != null ? sdf.format(r.getFechaGeneracion()) : "Fecha desconocida";
                sb.append(String.format("[%03d] %s - Concierto: %s\n", 
                        r.getIdReporte(), fecha, r.getNombreConcierto()));
                sb.append("      Tipo: ").append(r.getTipo()).append("\n");
                sb.append("--------------------------------------------------\n");
            }
        }
        sb.append("==================================================\n");
        return sb.toString();
    }

    public String obtenerResumenEventoDetallado(Concierto c) {
        if (c == null) {
            return "Selecciona un evento primero.";
        }

        int idAnalisis = (c.getAnalisis() != null) ? c.getAnalisis().getIdAnalisisF() : 0;
        int totalBoleteria = idAnalisis > 0 ? boleteriaService.obtenerTotalBoleteria(idAnalisis) : 0;
        int totalIngresosAdicionales = idAnalisis > 0 ? ingresoService.obtenerTotalIngresos(idAnalisis) : 0;
        int totalGastos = idAnalisis > 0 ? gastoService.obtenerTotalGastos(idAnalisis) : 0;
        double totalNomina = nominaService.obtenerTotalGeneralEvento(c.getIdConcierto());
        int totalIngresos = totalBoleteria + totalIngresosAdicionales;
        double balance = totalIngresos - totalGastos - totalNomina;

        StringBuilder sb = new StringBuilder();
        sb.append("Evento: ").append(c.getNombreConcierto()).append("\n");
        sb.append("ID Evento: ").append(c.getIdConcierto()).append("\n");
        sb.append("Aforo: ").append(c.getAforo()).append("\n");
        sb.append("Programado: ").append(c.isProgramado() ? "Sí" : "No").append("\n");
        if (c.getHorario() != null) {
            sb.append("Fecha/Hora Inicio: ").append(c.getHorario().getFechaInicio()).append(" ").append(c.getHorario().getHoraInicio()).append("\n");
            sb.append("Fecha/Hora Fin: ").append(c.getHorario().getFechaFin()).append(" ").append(c.getHorario().getHoraFin()).append("\n");
        }
        sb.append("Contrato ID: ").append(c.getIdContrato() > 0 ? c.getIdContrato() : "N/A").append("\n");
        sb.append("Análisis ID: ").append(idAnalisis > 0 ? idAnalisis : "N/A").append("\n");
        sb.append("\n");
        sb.append("Resumen económico:\n");
        sb.append("• Ingresos boletería: ").append(formatCurrency(totalBoleteria)).append("\n");
        sb.append("• Otros ingresos: ").append(formatCurrency(totalIngresosAdicionales)).append("\n");
        sb.append("• Gastos: ").append(formatCurrency(totalGastos)).append("\n");
        sb.append("• Nómina staff: ").append(formatCurrency((int) Math.round(totalNomina))).append("\n");
        sb.append("• Balance neto: ").append(formatCurrency((int) Math.round(balance))).append("\n");

        return sb.toString();
    }

    private String formatCurrency(int val) {
        return currencyFormatter.format(val);
    }
}
