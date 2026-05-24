package org.example.ax0006.service;

import org.example.ax0006.entity.Horario;
import org.example.ax0006.repository.InventarioObjetoRepository;

public class InventarioObjetoService {
    private final InventarioObjetoRepository inventarioObjetoRepository;

    public InventarioObjetoService(InventarioObjetoRepository inventarioObjetoRepository) {
        this.inventarioObjetoRepository = inventarioObjetoRepository;
    }

    public int guardarObjetoEnInventario(int inventarioId, int objetoId, Horario hNuevo) {
        return inventarioObjetoRepository.guardarObjetoEnInventario(inventarioId, objetoId, hNuevo);
    }

    public boolean objetoEnUsoEnRango(int objetoId, Horario hNuevo) {
        return inventarioObjetoRepository.objetoEnUsoEnRango(objetoId, hNuevo);
    }
}