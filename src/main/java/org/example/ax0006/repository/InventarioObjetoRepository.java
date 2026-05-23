package org.example.ax0006.repository;

import org.example.ax0006.entity.Horario;
import org.example.ax0006.db.H2;
import java.sql.*;

public class InventarioObjetoRepository {
    private H2 h2;

    public InventarioObjetoRepository(H2 h2) {
        this.h2 = h2;
    }

    public int guardarObjetoEnInventario(int inventarioId, int objetoId, Horario hNuevo) {
        if (objetoEnUsoEnRango(objetoId, hNuevo)) {
            System.out.println("[ALERTA] Conflicto detectado para el objeto ID: " + objetoId);
            return -1;
        }

        String sqlInsert = "INSERT INTO ObjetoDocumentoInventario (idInventario, idObjeto) VALUES (?, ?)";
        try (Connection conn = h2.getConnection();
             PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert)) {

            stmtInsert.setInt(1, inventarioId);
            stmtInsert.setInt(2, objetoId);
            stmtInsert.executeUpdate();
            return 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean objetoEnUsoEnRango(int objetoId, Horario hNuevo) {
        String sql = """
            SELECT 1 
            FROM ObjetoDocumentoInventario odi
            JOIN DocumentoInventarioHorario dih ON odi.idInventario = dih.idDocumentoInventario
            JOIN Horario h ON dih.idHorario = h.idHorario
            WHERE odi.idObjeto = ?
            AND (
                CAST(h.fechaInc || ' ' || h.horaInc AS TIMESTAMP) < CAST(? || ' ' || ? AS TIMESTAMP)
                AND
                CAST(h.fechaFin || ' ' || h.horaFin AS TIMESTAMP) > CAST(? || ' ' || ? AS TIMESTAMP)
            )
        """;

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, objetoId);
            stmt.setDate(2, Date.valueOf(hNuevo.getFechaFin()));
            stmt.setTime(3, Time.valueOf(hNuevo.getHoraFin()));
            stmt.setDate(4, Date.valueOf(hNuevo.getFechaInicio()));
            stmt.setTime(5, Time.valueOf(hNuevo.getHoraInicio()));

            try (ResultSet rs = stmt.executeQuery()) {
                boolean hayConflicto = rs.next();
                if (hayConflicto) {
                    System.out.println("[DB] Conflicto encontrado para Objeto " + objetoId);
                }
                return hayConflicto;
            }
        } catch (SQLException e) {
            System.err.println("Error al validar disponibilidad del objeto: " + objetoId);
            e.printStackTrace();
            return true;
        }
    }
}