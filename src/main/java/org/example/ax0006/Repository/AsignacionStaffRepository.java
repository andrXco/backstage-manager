package org.example.ax0006.Repository;

import org.example.ax0006.Entity.Usuario;
import org.example.ax0006.db.H2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public boolean existeAsignacionEnConcierto(int idUsuario, int idConcierto) {
        String sql = "SELECT 1 FROM RolConciertoUsuario WHERE idUsuario = ? AND idConcierto = ?";
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idConcierto);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void eliminarAsignacionesUsuarioEnConcierto(int idUsuario, int idConcierto) {
        String sql = "DELETE FROM RolConciertoUsuario WHERE idUsuario = ? AND idConcierto = ?";
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idConcierto);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Usuario> obtenerStaffPorConcierto(int idConcierto) {
        String sql = "SELECT DISTINCT u.idUsuario, u.nombre, u.contrasena, u.gmail " +
                "FROM Usuario u " +
                "JOIN RolConciertoUsuario rcu ON u.idUsuario = rcu.idUsuario " +
                "WHERE rcu.idConcierto = ?";
        List<Usuario> staff = new ArrayList<>();

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idConcierto);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("idUsuario"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setContrasena(rs.getString("contrasena"));
                usuario.setGmail(rs.getString("gmail"));
                staff.add(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return staff;
    }

    public List<Integer> obtenerIdsUsuariosAsignados() {
        String sql = "SELECT DISTINCT idUsuario FROM RolConciertoUsuario";
        List<Integer> ids = new ArrayList<>();

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ids.add(rs.getInt("idUsuario"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ids;
    }

    public List<Usuario> obtenerUsuariosPorConcierto(int idConcierto) {
        String sql = "SELECT DISTINCT u.idUsuario, u.nombre, u.contrasena, u.gmail " +
                "FROM Usuario u " +
                "JOIN RolConciertoUsuario rcu ON u.idUsuario = rcu.idUsuario " +
                "WHERE rcu.idConcierto = ?";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idConcierto);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("idUsuario"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setContrasena(rs.getString("contrasena"));
                usuario.setGmail(rs.getString("gmail"));
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usuarios;
    }

    public String obtenerNombreRolEnConcierto(int idUsuario, int idConcierto) {
        String sql = "SELECT r.rol " +
                "FROM RolConciertoUsuario rcu " +
                "JOIN Rol r ON rcu.idRol = r.idRol " +
                "WHERE rcu.idUsuario = ? AND rcu.idConcierto = ?";

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idConcierto);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("rol");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}