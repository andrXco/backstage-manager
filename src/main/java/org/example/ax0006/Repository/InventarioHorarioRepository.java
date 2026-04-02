package org.example.ax0006.Repository;

import org.example.ax0006.db.H2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InventarioHorarioRepository {
    private H2 h2;

    // CONSTRUCTOR
    public InventarioHorarioRepository(H2 h2) {
        this.h2 = h2;
    }

    public void guardarInventarioEnHorario(int inventarioId, int idHorario) {
        String sql = "INSERT INTO InventarioHorario (idInventario, idHorario) VALUES (?, ?)";
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, inventarioId);
            stmt.setInt(2, idHorario);
            stmt.executeUpdate();
            System.out.println("Objeto agregado al Inventario correctamente");

        } catch (SQLException e) {
            if (e.getMessage().toLowerCase().contains("constraint")) {
                System.out.println("Ya está ese Objeto en ese Inventario o no existe la relación válida");
            } else {
                e.printStackTrace();
            }
        }
    }
}
