package org.example.ax0006.Service;

import org.example.ax0006.Repository.InventarioObjetoRepository;

public class InventarioObjetoService {
    private InventarioObjetoRepository inventarioObjetoRepository;

    public InventarioObjetoService(InventarioObjetoRepository inventarioObjetoRepository) {
        this.inventarioObjetoRepository = inventarioObjetoRepository;
    }

    public int asignarObjetoAInventario(int inventarioId, int objetoId) {
        return inventarioObjetoRepository.guardarObjetoEnInventario(inventarioId, objetoId);
    }
}