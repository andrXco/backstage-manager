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

    public void guardarRol(Rol R) {
        String sql = "INSERT INTO Rol (rol) VALUES (?)";
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, R.getRol());
            stmt.executeUpdate();
            System.out.println("Rol guardado en BD: " + R.getRol());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //SE HACE LA CONSULTA AL ROL QUE SE RECIBE COMO PARAMETRO A LA BASE DE DATOS.
    public Rol buscarPorRol(String rol) {
        String sql = "SELECT rol, idRol FROM Rol WHERE rol = ?";
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, rol);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Rol(rs.getString("rol"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

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


}
