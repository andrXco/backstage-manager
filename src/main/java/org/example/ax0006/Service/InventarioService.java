package org.example.ax0006.Service;

import org.example.ax0006.Repository.ConciertoObjetoRepository;
import org.example.ax0006.Repository.ObjetoRepository;

public class InventarioService {

    private ObjetoRepository objetoRepo;
    private ConciertoObjetoRepository conciertoObjetoRepo;

    public InventarioService(ObjetoRepository objetoRepo,
                             ConciertoObjetoRepository conciertoObjetoRepo) {
        this.objetoRepo = objetoRepo;
        this.conciertoObjetoRepo = conciertoObjetoRepo;
    }

    public void asignarObjetoAConcierto(int idConcierto, int idObjeto) {

        if (!objetoRepo.estaDisponible(idObjeto)) {
            throw new IllegalStateException("El objeto no está disponible");
        }

        // 1. asignar relación
        conciertoObjetoRepo.asignarObjeto(idConcierto, idObjeto);

        // 2. marcar como no disponible
        objetoRepo.actualizarDisponibilidad(idObjeto, false);
    }

    public void liberarObjetoDeConcierto(int idConcierto, int idObjeto) {

        // 1. eliminar relación
        conciertoObjetoRepo.eliminarAsignacion(idConcierto, idObjeto);

        // 2. volver disponible
        objetoRepo.actualizarDisponibilidad(idObjeto, true);
    }
}