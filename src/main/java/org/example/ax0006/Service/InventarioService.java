package org.example.ax0006.Service;

import org.example.ax0006.Entity.Inventario;
import org.example.ax0006.Entity.TipoObjeto;
import org.example.ax0006.Repository.InventarioRepository;

public class InventarioService {
    private InventarioRepository inventarioRepository;

    public InventarioService(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    public boolean crearInventario() {
        Inventario inventario = new Inventario();
        inventarioRepository.guardarInventario(inventario);
        return true;

    }
}
