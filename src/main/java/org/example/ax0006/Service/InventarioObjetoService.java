package org.example.ax0006.Service;

import org.example.ax0006.Repository.InventarioHorarioRepository;
import org.example.ax0006.Repository.InventarioObjetoRepository;

public class InventarioObjetoService {
    private InventarioObjetoRepository inventarioObjetoRepository;

    public InventarioObjetoService(InventarioObjetoRepository inventarioObjetoRepository) {
        this.inventarioObjetoRepository = inventarioObjetoRepository;
    }

    public boolean asignarObjetoAInventario(int inventarioId, int objetoId) {
        inventarioObjetoRepository.guardarObjetoEnInventario(inventarioId, objetoId);
        return true;

    }
}