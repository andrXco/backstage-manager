package org.example.ax0006.Repository;

import org.example.ax0006.Entity.Inventario;
import org.example.ax0006.db.H2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventarioObjetoRepository {
    private H2 h2;

    // CONSTRUCTOR
    public InventarioObjetoRepository(H2 h2) {
        this.h2 = h2;
    }

    public void guardarObjetoEnInventario(int inventarioId, int objetoId) {
        String sql = "INSERT INTO ObjetoInventario (idInventario, idTipoObjeto) VALUES (?, ?)";
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, inventarioId);
            stmt.setInt(2, objetoId);
            stmt.executeUpdate();
            System.out.println("Objeto agregado al inventario correctamente");

        } catch (SQLException e) {
            if (e.getMessage().toLowerCase().contains("constraint")) {
                System.out.println("Ya está ese objeto en ese inventario o no existe la relación válida");
            } else {
                e.printStackTrace();
            }
        }
    }
}
