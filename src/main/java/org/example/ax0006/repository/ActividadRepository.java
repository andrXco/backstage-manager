package org.example.ax0006.repository;

import org.example.ax0006.entity.Actividad;
import org.example.ax0006.db.H2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActividadRepository {
    private final H2 h2;

    public ActividadRepository(H2 h2) {
        this.h2 = h2;
    }

    public void registrarActividad(String tipo, String modulo, String origen, String descripcion,
                                   Integer idUsuarioActor, String rolDestino) {
        String sql = """
                INSERT INTO ActividadSistema
                (tipo, modulo, origen, descripcion, idUsuarioActor, rolDestino)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tipo);
            stmt.setString(2, modulo);
            stmt.setString(3, origen);
            stmt.setString(4, descripcion);

            if (idUsuarioActor == null) {
                stmt.setNull(5, Types.INTEGER);
            } else {
                stmt.setInt(5, idUsuarioActor);
            }

            stmt.setString(6, rolDestino);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Actividad> listarParaUsuario(int idUsuario, String rolUsuario, String filtroTipo) {
        List<Actividad> actividades = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
                SELECT a.idActividad,
                       a.tipo,
                       a.modulo,
                       a.origen,
                       a.descripcion,
                       a.fechaHora,
                       a.idUsuarioActor,
                       u.nombre AS nombreUsuarioActor,
                       a.rolDestino,
                       CASE WHEN eau.idActividad IS NULL THEN FALSE ELSE eau.revisado END AS revisado
                FROM ActividadSistema a
                LEFT JOIN Usuario u ON u.idUsuario = a.idUsuarioActor
                LEFT JOIN EstadoActividadUsuario eau
                    ON eau.idActividad = a.idActividad
                    AND eau.idUsuario = ?
                WHERE (a.rolDestino IS NULL OR a.rolDestino = ? OR a.idUsuarioActor = ?)
                """);

        if (filtroTipo != null && !filtroTipo.isBlank() && !"TODO".equalsIgnoreCase(filtroTipo)) {
            sql.append(" AND UPPER(a.tipo) = UPPER(?) ");
        }

        sql.append(" ORDER BY a.fechaHora DESC, a.idActividad DESC LIMIT 50");

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            stmt.setInt(1, idUsuario);
            stmt.setString(2, rolUsuario);
            stmt.setInt(3, idUsuario);

            if (filtroTipo != null && !filtroTipo.isBlank() && !"TODO".equalsIgnoreCase(filtroTipo)) {
                stmt.setString(4, filtroTipo);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                actividades.add(mapear(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return actividades;
    }

    public int contarPendientesParaUsuario(int idUsuario, String rolUsuario) {
        String sql = """
                SELECT COUNT(*) AS total
                FROM ActividadSistema a
                LEFT JOIN EstadoActividadUsuario eau
                    ON eau.idActividad = a.idActividad
                    AND eau.idUsuario = ?
                WHERE (a.rolDestino IS NULL OR a.rolDestino = ? OR a.idUsuarioActor = ?)
                  AND (eau.idActividad IS NULL OR eau.revisado = FALSE)
                """;

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            stmt.setString(2, rolUsuario);
            stmt.setInt(3, idUsuario);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void marcarRevisado(int idActividad, int idUsuario) {
        String sql = """
                MERGE INTO EstadoActividadUsuario
                (idActividad, idUsuario, revisado, fechaRevision)
                KEY(idActividad, idUsuario)
                VALUES (?, ?, TRUE, CURRENT_TIMESTAMP)
                """;

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idActividad);
            stmt.setInt(2, idUsuario);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Actividad mapear(ResultSet rs) throws SQLException {
        Actividad actividad = new Actividad();

        actividad.setIdActividad(rs.getInt("idActividad"));
        actividad.setTipo(rs.getString("tipo"));
        actividad.setModulo(rs.getString("modulo"));
        actividad.setOrigen(rs.getString("origen"));
        actividad.setDescripcion(rs.getString("descripcion"));

        Timestamp timestamp = rs.getTimestamp("fechaHora");
        if (timestamp != null) {
            actividad.setFechaHora(timestamp.toLocalDateTime());
        }

        int idActor = rs.getInt("idUsuarioActor");
        actividad.setIdUsuarioActor(rs.wasNull() ? null : idActor);

        actividad.setNombreUsuarioActor(rs.getString("nombreUsuarioActor"));
        actividad.setRolDestino(rs.getString("rolDestino"));
        actividad.setRevisado(rs.getBoolean("revisado"));

        return actividad;
    }
}