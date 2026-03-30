package org.example.ax0006.Repository;

import org.example.ax0006.Entity.Rol;
import org.example.ax0006.db.H2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RolRepository {

    private H2 h2;

    public RolRepository(H2 h2) {
        this.h2 = h2;
    }

    public List<Rol> obtenerRoles() {
        List<Rol> lista = new ArrayList<>();
        String sql = "SELECT idRol, rol FROM Rol";
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Rol(rs.getInt("idRol"), rs.getString("rol")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }


    //permite obtener el nombre del rol segun el idrol para mostrarlo en la tabla de administracion de usuarios
    public String obtenerNombreRol(int idRol) {
        String sql = "SELECT rol FROM Rol WHERE idRol = ?";
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRol);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("rol");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Sin rol";
    }
}
