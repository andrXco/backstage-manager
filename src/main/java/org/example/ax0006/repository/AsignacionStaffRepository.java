package org.example.ax0006.repository;
import org.example.ax0006.entity.Usuario;
import java.util.ArrayList;
import java.util.List;
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


    public List<Integer> obtenerIdsUsuariosAsignados() {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT DISTINCT idUsuario FROM RolConciertoUsuario";
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
        List<Usuario> lista = new ArrayList<>();
        String sql = """
        SELECT DISTINCT u.idUsuario, u.nombre, u.gmail
        FROM RolConciertoUsuario rcu
        JOIN Usuario u ON rcu.idUsuario = u.idUsuario
        WHERE rcu.idConcierto = ?
    """;
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idConcierto);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("idUsuario"));
                u.setNombre(rs.getString("nombre"));
                u.setGmail(rs.getString("gmail"));
                lista.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }


    //En el siguiente metodo se permitira que usario pueda tener mas de un rol en uno o diferentes conciertos aprovechando el cambio que se hizo en la DB.
    public String obtenerNombreRolEnConcierto(int idUsuario, int idConcierto) {
        String sql = """
        SELECT r.rol FROM RolConciertoUsuario rcu
        JOIN Rol r ON rcu.idRol = r.idRol
        WHERE rcu.idUsuario = ? AND rcu.idConcierto = ?
    """;
        List<String> roles = new ArrayList<>();
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idConcierto);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                roles.add(rs.getString("rol"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles.isEmpty() ? "Sin rol" : String.join(", ", roles);
    }
}