package org.example.ax0006.Service;

import org.example.ax0006.Entity.*;
import org.example.ax0006.Repository.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class NominaService {

    private final NominaRepository nominaRepository;
    private final ConciertoRepository conciertoRepository;
    private final AsignacionStaffRepository asignacionStaffRepository;

    public NominaService(NominaRepository nominaRepository,
                         ConciertoRepository conciertoRepository,
                         AsignacionStaffRepository asignacionStaffRepository) {
        this.nominaRepository = nominaRepository;
        this.conciertoRepository = conciertoRepository;
        this.asignacionStaffRepository = asignacionStaffRepository;
    }

    private double calcularHorasTrabajadas(Concierto concierto) {
        Horario horario = concierto.getHorario();
        if (horario == null) return 0;
        LocalDateTime inicio = LocalDateTime.of(horario.getFechaInicio(), horario.getHoraInicio());
        LocalDateTime fin = LocalDateTime.of(horario.getFechaFin(), horario.getHoraFin());
        Duration duration = Duration.between(inicio, fin);
        return duration.toHours() + (duration.toMinutesPart() / 60.0);
    }

    private double obtenerTarifaPorRol(String nombreRol) {
        // Si hay múltiples roles separados por coma, tomamos el primero
        String primerRol = nombreRol.split(",")[0].trim().toLowerCase();
        switch (primerRol) {
            case "staff": return 10000;
            case "tecnico": return 20000;
            case "artista": return 15000;
            case "administrador": return 25000;
            default: return 10000;
        }
    }

    public void generarNominaParaConcierto(int idConcierto) {
        Concierto concierto = conciertoRepository.obtenerPorId(idConcierto);
        if (concierto == null) return;

        double horasBase = calcularHorasTrabajadas(concierto);
        List<Usuario> staffList = asignacionStaffRepository.obtenerUsuariosPorConcierto(idConcierto);

        for (Usuario u : staffList) {
            List<Nomina> existentes = nominaRepository.obtenerPorConcierto(idConcierto);
            boolean existe = existentes.stream().anyMatch(n -> n.getIdUsuario() == u.getIdUsuario());
            if (existe) continue;

            String rolNombre = asignacionStaffRepository.obtenerNombreRolEnConcierto(u.getIdUsuario(), idConcierto);
            double tarifa = obtenerTarifaPorRol(rolNombre);
            double total = horasBase * tarifa;
            Nomina n = new Nomina(idConcierto, u.getIdUsuario(), horasBase, tarifa, 0, total, false);
            nominaRepository.guardar(n);
        }
    }

    public List<Nomina> obtenerNominasPorConcierto(int idConcierto) {
        return nominaRepository.obtenerPorConcierto(idConcierto);
    }

    public void actualizarHorasExtra(int idNomina, double horasExtra) {
        // Buscar la nómina por ID
        List<Nomina> todas = nominaRepository.obtenerPorConcierto(0); // Solución temporal: necesitamos un método obtenerPorId()
        Nomina n = todas.stream().filter(nom -> nom.getIdNomina() == idNomina).findFirst().orElse(null);
        if (n == null) return;

        double nuevoTotal = (n.getHorasTrabajadas() * n.getTarifaPorHora()) + (horasExtra * n.getTarifaPorHora() * 1.5);
        nominaRepository.actualizarHorasExtraYTotal(idNomina, horasExtra, nuevoTotal);
    }

    public void actualizarEstado(int idNomina, boolean pagado) {
        nominaRepository.actualizarEstado(idNomina, pagado);
    }

    public double calcularTotalGeneral(int idConcierto) {
        return nominaRepository.obtenerPorConcierto(idConcierto).stream().mapToDouble(Nomina::getTotal).sum();
    }
}
