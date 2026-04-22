package org.example.ax0006.Validator;

import org.example.ax0006.Entity.Horario;

public class HorarioValidator {

    public HorarioValidator() {}

    public void validar(Horario h) {

        if (h == null) {
            throw new IllegalArgumentException("El horario es obligatorio");
        }

        if (h.getFechaInicio() == null || h.getFechaFin() == null) {
            throw new IllegalArgumentException("Las fechas son obligatorias");
        }

        if (h.getFechaFin().isBefore(h.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha fin no puede ser menor a la fecha inicio");
        }

        if (h.getHoraInicio() == null || h.getHoraFin() == null) {
            throw new IllegalArgumentException("Las horas son obligatorias");
        }

        if ((h.getHoraFin().isBefore(h.getHoraInicio()) || h.getHoraInicio().equals(h.getHoraFin())) && h.getFechaFin().isEqual(h.getFechaInicio())) {
            throw new IllegalArgumentException("La hora fin no puede ser menor o igual a la hora inicio");
        }
    }
}