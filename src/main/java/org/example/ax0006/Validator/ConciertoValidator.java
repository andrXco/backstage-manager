package org.example.ax0006.Validator;

import org.example.ax0006.Entity.Concierto;
import org.example.ax0006.Entity.Contrato;

public class ConciertoValidator {

    HorarioValidator horarioValidator;

    public ConciertoValidator(HorarioValidator horarioValidator){
        this.horarioValidator = horarioValidator;
    }

    public void validar(Concierto c, Contrato contrato) {

        if (c == null) {
            throw new IllegalArgumentException("El concierto es nulo");
        }

        if (c.getNombreConcierto() == null || c.getNombreConcierto().isBlank()) {
            throw new IllegalArgumentException("El nombre del concierto es un campo obligatorio");
        }

        if (c.getAforo() <= 0) {
            throw new IllegalArgumentException("El aforo debe ser mayor a 0");
        }

        if (c.getArtista() == null) {
            throw new IllegalArgumentException("El artista es nulo");
        }

        if (contrato.getFecha().isAfter(c.getHorario().getFechaInicio())) {
        throw new IllegalArgumentException("La fecha de contrato debe ser PREVIA al concierto");
        }   
        horarioValidator.validar(c.getHorario());
    }
}
