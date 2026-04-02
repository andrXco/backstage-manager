package org.example.ax0006.Repository;

import org.example.ax0006.Entity.Inventario;
import org.example.ax0006.Entity.Rol;
import org.example.ax0006.Entity.TipoObjeto;
import org.example.ax0006.db.H2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventarioRepository {
    private H2 h2;
    private List<Inventario> Inventarios = new ArrayList<>();

    //CONSTRUCTOR
    public InventarioRepository(H2 h2) {
        this.h2 = h2;
    }

    //SE CREA UN NUEVO INVENTARIO:
    public void guardarInventario(Inventario I) {
        String sql = "INSERT INTO Inventario DEFAULT VALUES";
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int idGenerado = rs.getInt(1);
                System.out.println("Inventario creado correctamente con ID: " + idGenerado);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Inventario buscarInventarioPorId(int idBuscar) {
        String sql = "SELECT * FROM Inventario WHERE idInventario = ?";
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idBuscar);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Inventario();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
