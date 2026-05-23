package org.example.ax0006.service;

import org.example.ax0006.entity.Objeto;
import org.example.ax0006.repository.InventarioRepository;
import java.util.List;

public class InventarioService {
    private InventarioRepository inventarioRepo;

    public InventarioService(InventarioRepository inventarioRepo) {
        this.inventarioRepo = inventarioRepo;
    }

    public int crearDocumentoInventario(int idConcierto, int idHorario, List<Integer> idsObjetos) {
        return inventarioRepo.crearDocumentoInventario(idConcierto, idHorario, idsObjetos);
    }

    public List<String> obtenerObjetosPorConcierto(int idConcierto) {
        return inventarioRepo.obtenerObjetosPorConcierto(idConcierto);
    }

    public void  eliminarDocumentoInventario(int idInventario, int idConcierto, int idHorario, List<Integer> idsObjetos) {
        inventarioRepo.eliminarInventario( idInventario,  idConcierto,  idHorario, idsObjetos);
    }

    public int obtenerDocumentoInventarioPorConcierto(int idConcierto) {
        return inventarioRepo.obtenerDocumentoInventarioConcierto(idConcierto);
    }

    public List<Objeto> obtenerObjetoObjetosPorConcierto(int idConcierto) {
        return inventarioRepo.obtenerObjetosCompletosPorConcierto(idConcierto);
    }

    public List<Objeto> obtenerObjetosPorInventario(int idInventario){
        return inventarioRepo.obtenerObjetosCompletosPorInventario(idInventario);
    }

    public List<Integer> obtenerInventariosSinConcierto(){
        return inventarioRepo.obtenerInventariosSinConcierto();
    }

    public int obtenerIdHorarioPorInventario(int idInventario) {
        return inventarioRepo.obtenerIdHorarioPorInventario(idInventario);
    }

    public void EliminarHorarioInventario(int idHorario){
        inventarioRepo.romperRelacionesHorario(idHorario);
    }

}