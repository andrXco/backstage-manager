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

        System.out.println("Usuario ID: " + c.getArtista().getIdUsuario());
        System.out.println("ID concierto generado: " + idConcierto);
        // 3. Guardar relación artista
        conciertoRepo.guardarRelacionArtista(
                c.getArtista().getIdUsuario(),
                idConcierto,
                c.getArtista().getIdRol() // o 3 si sabes que siempre es artista
        );
    }

    public List<Concierto> obtenerConciertos() {
        return conciertoRepo.obtenerConciertos();
    }
}