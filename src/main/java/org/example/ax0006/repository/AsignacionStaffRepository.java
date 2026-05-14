package org.example.ax0006.repository;

import org.example.ax0006.entity.Usuario;
import org.example.ax0006.db.H2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AsignacionStaffRepository {

    private final H2 h2;

    public AsignacionStaffRepository(H2 h2) {
        this.h2 = h2;
    }

    // Método que permite asignar un rol a un usuario dentro de un concierto
    // También guarda el id del subrol cuando el usuario asignado tiene rol de Staff
    public void asignarStaffAConcierto(int idUsuario, int idConcierto, int idRol, String subrol) {
        String sql = "INSERT INTO RolConciertoUsuario (idRol, idUsuario, idConcierto, idSubrol) VALUES (?, ?, ?, ?)";

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            Integer idSubrol = obtenerIdSubrolPorNombre(subrol);

            stmt.setInt(1, idRol);
            stmt.setInt(2, idUsuario);
            stmt.setInt(3, idConcierto);

            if (idSubrol == null) {
                stmt.setNull(4, Types.INTEGER);
            } else {
                stmt.setInt(4, idSubrol);
            }

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
        String sql = """
        SELECT DISTINCT u.idUsuario, u.nombre, u.contrasena, u.gmail, u.idRol
        FROM RolConciertoUsuario rcu
        JOIN Usuario u ON rcu.idUsuario = u.idUsuario
        WHERE rcu.idConcierto = ?
    """;

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
                usuario.setIdRol(rs.getInt("idRol"));
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usuarios;
    }

    // Método que permite obtener todos los roles que tiene un usuario dentro de un concierto
    // Si el usuario tiene más de un rol, los retorna separados por coma
    public String obtenerNombreRolEnConcierto(int idUsuario, int idConcierto) {
        String sql = "SELECT r.rol " +
                "FROM RolConciertoUsuario rcu " +
                "JOIN Rol r ON rcu.idRol = r.idRol " +
                "WHERE rcu.idUsuario = ? AND rcu.idConcierto = ? " +
                "ORDER BY r.idRol";

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

    // Método que permite obtener el subrol de un usuario con rol Staff dentro de un concierto
    public String obtenerSubrolStaffEnConcierto(int idUsuario, int idConcierto) {
        String sql = """
        SELECT s.nombre
        FROM RolConciertoUsuario rcu
        LEFT JOIN Subrol s ON rcu.idSubrol = s.idSubrol
        WHERE rcu.idUsuario = ? AND rcu.idConcierto = ? AND rcu.idRol = 4
    """;

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idConcierto);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String subrol = rs.getString("nombre");
                return subrol == null || subrol.isBlank() ? "Sin subrol" : subrol;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "Sin subrol";
    }

    // Método que permite actualizar el subrol de un usuario con rol Staff dentro de un concierto
    public boolean actualizarSubrolStaffEnConcierto(int idUsuario, int idConcierto, String subrol) {
        String sql = "UPDATE RolConciertoUsuario SET idSubrol = ? WHERE idUsuario = ? AND idConcierto = ? AND idRol = 4";

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            Integer idSubrol = obtenerIdSubrolPorNombre(subrol);

            if (idSubrol == null) {
                return false;
            }

            stmt.setInt(1, idSubrol);
            stmt.setInt(2, idUsuario);
            stmt.setInt(3, idConcierto);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método que permite obtener el id del subrol a partir de su nombre
    private Integer obtenerIdSubrolPorNombre(String subrol) {
        if (subrol == null || subrol.isBlank()) {
            return null;
        }

        String sql = "SELECT idSubrol FROM Subrol WHERE nombre = ?";

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, subrol);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("idSubrol");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Método que permite obtener los subroles disponibles desde la base de datos
    public List<String> obtenerSubrolesDisponibles() {
        String sql = "SELECT nombre FROM Subrol ORDER BY idSubrol";
        List<String> subroles = new ArrayList<>();

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                subroles.add(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return subroles;
    }

    //Metodo para obtener el concierto del usuario
    public int obtenerIdConciertoDelUsuario(int idUsuario) {
        String sql = """
        SELECT idConcierto FROM RolConciertoUsuario
        WHERE idUsuario = ? AND idConcierto IS NOT NULL
        LIMIT 1
    """;
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("idConcierto");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}