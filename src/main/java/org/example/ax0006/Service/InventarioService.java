package org.example.ax0006.Service;

import org.example.ax0006.Entity.Horario;
import org.example.ax0006.Entity.Inventario;
import org.example.ax0006.Repository.InventarioRepository;

public class InventarioService {

    private InventarioRepository inventarioRepository;

    public InventarioService(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    public int crearInventario(Horario horario) {
        Inventario inventario = new Inventario();

        int idInventario = inventarioRepository.guardarInventario(inventario);

        if (idInventario != -1) {
            int idHorario = inventarioRepository.guardarHorario(horario);

            if (idHorario != -1) {
                inventarioRepository.vincularInventarioHorario(idInventario, idHorario);
                return idInventario;
            }
        }

        return -1;
    }
}