package org.example.ax0006.Service;

import org.example.ax0006.Entity.Concierto;
import org.example.ax0006.Repository.ConciertoRepository;
import org.example.ax0006.Repository.HorarioRepository;

import java.util.List;

public class ConciertoService {

    private ConciertoRepository conciertoRepo;
    private HorarioRepository horarioRepo;

    public ConciertoService(ConciertoRepository conciertoRepo, HorarioRepository horarioRepo) {
        this.conciertoRepo = conciertoRepo;
        this.horarioRepo = horarioRepo;
    }

    public void crearConcierto(Concierto c) {

        // 1. Guardar horario
        int idHorario = horarioRepo.guardar(c.getHorario());

        // 2. Guardar concierto
        int idConcierto = conciertoRepo.guardar(c, idHorario);

        // 3. Guardar relación artista
        conciertoRepo.guardarRelacionArtista(c.getArtista().getIdUsuario(), idConcierto, 3);
    }


    public List<Concierto> obtenerConciertosSolos() {
        return conciertoRepo.obtenerConciertosSolos();
    }

    /*Obtiene los conciertos, sus horarios y usuarios de la base de datos*/
    public List<Concierto> obtenerConciertos() {
        return conciertoRepo.obtenerConciertos();
    }



    /*El atributo del programado = true */
    public void aprobarConcierto(int idConcierto) {
        conciertoRepo.aprobarConcierto(idConcierto);
    }

    /*Elimina un concierto y su horario de la base de datos*/
    public void eliminarConcierto(int idConcierto, int idHorario) {
        conciertoRepo.eliminarConcierto(idConcierto);
        horarioRepo.eliminarHorario(idHorario);
    }
}