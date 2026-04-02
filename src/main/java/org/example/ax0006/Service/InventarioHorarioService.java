package org.example.ax0006.Service;

import org.example.ax0006.Repository.InventarioHorarioRepository;

public class InventarioHorarioService {
    private InventarioHorarioRepository inventarioHorarioRepository;

    public InventarioHorarioService(InventarioHorarioRepository inventarioHorarioRepository) {
        this.inventarioHorarioRepository = inventarioHorarioRepository;
    }

    public boolean asignarInventarioAHorario(int inventarioId, int horarioId) {
        inventarioHorarioRepository.guardarInventarioEnHorario(inventarioId, horarioId);
        return true;

    }
}
