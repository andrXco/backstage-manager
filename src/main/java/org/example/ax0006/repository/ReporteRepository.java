package org.example.ax0006.repository;

import org.example.ax0006.db.H2;
import org.example.ax0006.entity.Reporte;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReporteRepository {

    private final H2 h2;

    public ReporteRepository(H2 h2) {
        this.h2 = h2;
    }

    public int guardar(Reporte reporte) {
        String sql = """
            INSERT INTO Reporte
            (idConcierto, nombreConcierto, tipo, fechaGeneracion, contenido)
            VALUES (?, ?, ?, ?, ?)
        """;

        int idGenerado = 0;

        try (
                Connection conn = h2.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            if (reporte.getIdConcierto() > 0) {
                stmt.setInt(1, reporte.getIdConcierto());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setString(2, reporte.getNombreConcierto());
            stmt.setString(3, reporte.getTipo());
            
            Timestamp ts = reporte.getFechaGeneracion() != null 
                ? reporte.getFechaGeneracion() 
                : new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(4, ts);
            stmt.setString(5, reporte.getContenido());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                idGenerado = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return idGenerado;
    }

    public List<Reporte> listarTodos() {
        List<Reporte> lista = new ArrayList<>();
        String sql = """
            SELECT *
            FROM Reporte
            ORDER BY fechaGeneracion DESC
        """;

        try (
                Connection conn = h2.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                Reporte r = new Reporte();
                r.setIdReporte(rs.getInt("idReporte"));
                r.setIdConcierto(rs.getInt("idConcierto"));
                r.setNombreConcierto(rs.getString("nombreConcierto"));
                r.setTipo(rs.getString("tipo"));
                r.setFechaGeneracion(rs.getTimestamp("fechaGeneracion"));
                r.setContenido(rs.getString("contenido"));
                lista.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public int obtenerTotalReportes() {
        String sql = "SELECT COUNT(*) AS total FROM Reporte";

        try (
                Connection conn = h2.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
