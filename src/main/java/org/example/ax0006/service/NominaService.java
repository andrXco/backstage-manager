package org.example.ax0006.service;

import org.example.ax0006.entity.*;
import org.example.ax0006.repository.*;

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

    private double obtenerTarifaPorRol(String rol) {
        rol = rol.toLowerCase();
        switch (rol) {
            case "manager":return 50000;
            case "tecnico":return 35000;
            case "luces":return 28000;
            case "sonido":return 30000;
            case "staff":return 15000;
            case "artista":return 40000;
            case "administrador":return 45000;
            default:return 10000;
        }
    }

    public void generarNominaParaConcierto(int idConcierto) {
        Concierto concierto = conciertoRepository.obtenerPorId(idConcierto);
        if (concierto == null) return;

        double horasBase = calcularHorasTrabajadas(concierto);
        List<Usuario> staffList = asignacionStaffRepository.obtenerUsuariosPorConcierto(idConcierto);

        System.out.println("Staff encontrados: " + staffList.size());
        System.out.println("Horas calculadas: " + horasBase);

        for (Usuario u : staffList) {
            List<Nomina> existentes = nominaRepository.obtenerPorConcierto(idConcierto);
            boolean existe = existentes.stream().anyMatch(n -> n.getIdUsuario() == u.getIdUsuario());
            if (existe) continue;

            String rolNombre = asignacionStaffRepository.obtenerNombreRolEnConcierto(u.getIdUsuario(), idConcierto);
            double tarifa = obtenerTarifaPorRol(rolNombre);
            double total = horasBase * tarifa;
            Nomina n = new Nomina( idConcierto, u.getIdUsuario(), rolNombre, horasBase, tarifa, 0, total, "PENDIENTE", false);
            nominaRepository.guardar(n);
        }
    }

    public List<Nomina> obtenerNominasPorConcierto(int idConcierto) {
        return nominaRepository.obtenerPorConcierto(idConcierto);
    }

    public void actualizarHorasExtra(int idNomina, double horasExtra) {
        Nomina n = nominaRepository.obtenerPorId(idNomina); // <- fix: antes llamaba obtenerPorConcierto(0)
        if (n == null) return;
        double nuevoTotal = (n.getHorasTrabajadas() * n.getTarifaPorHora())
                + (horasExtra * n.getTarifaPorHora() * 1.5);
        nominaRepository.actualizarHorasExtraYTotal(idNomina, horasExtra, nuevoTotal);
    }

    public void actualizarEstado(int idNomina, String estado) {
        nominaRepository.actualizarEstado(idNomina, estado);
    }

    public double calcularTotalGeneral(int idConcierto) {
        return nominaRepository.obtenerPorConcierto(idConcierto).stream().mapToDouble(Nomina::getTotal).sum();
    }

    public void actualizarHorasYTarifa(int idNomina, double horasTrabajadas, double tarifaPorHora, double horasExtra) {

        nominaRepository.actualizarHorasYTarifa(
                idNomina,
                horasTrabajadas,
                tarifaPorHora,
                horasExtra
        );
    }

    public List<Nomina> obtenerTodas() {
        return nominaRepository.obtenerTodas();
    }

}
