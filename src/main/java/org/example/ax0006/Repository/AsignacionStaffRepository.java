package org.example.ax0006.Repository;

import org.example.ax0006.db.H2;
import java.sql.*;

public class AsignacionStaffRepository {

    private final H2 h2;

    public AsignacionStaffRepository(H2 h2) {
        this.h2 = h2;
    }

    public void asignarStaffAConcierto(int idUsuario, int idConcierto, int idRol) {
        String sql = "INSERT INTO RolConciertoUsuario (idRol, idUsuario, idConcierto) VALUES (?, ?, ?)";
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRol);
            stmt.setInt(2, idUsuario);
            stmt.setInt(3, idConcierto);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminarAsignacion(int idUsuario, int idConcierto, int idRol) {
        String sql = "DELETE FROM RolConciertoUsuario WHERE idUsuario = ? AND idConcierto = ? AND idRol = ?";
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idConcierto);
            stmt.setInt(3, idRol);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean existeAsignacion(int idUsuario, int idConcierto, int idRol) {
        String sql = "SELECT 1 FROM RolConciertoUsuario WHERE idUsuario = ? AND idConcierto = ? AND idRol = ?";
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idConcierto);
            stmt.setInt(3, idRol);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}