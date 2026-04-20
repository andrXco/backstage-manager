package org.example.ax0006.Validator;

import org.example.ax0006.Entity.Concierto;

public class ConciertoValidator {

    HorarioValidator horarioValidator;

    public ConciertoValidator(HorarioValidator horarioValidator){
        this.horarioValidator = horarioValidator;
    }

    public void validar(Concierto c) {

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
        horarioValidator.validar(c.getHorario());
    }
}
