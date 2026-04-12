package org.example.ax0006.Repository;

import org.example.ax0006.Entity.Inventario;
import org.example.ax0006.db.H2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventarioObjetoRepository {
    private H2 h2;

    // CONSTRUCTOR
    public InventarioObjetoRepository(H2 h2) {
        this.h2 = h2;
    }

    public int guardarObjetoEnInventario(int inventarioId, int objetoId) {

        try (Connection conn = h2.getConnection()) {

            String sqlConflicto = """
                    SELECT 1
                    FROM ObjetoInventario oi
                    JOIN InventarioHorario ih1 ON oi.idInventario = ih1.idInventario
                    JOIN Horario h1 ON ih1.idHorario = h1.idHorario
                
                    JOIN InventarioHorario ih2 ON ih2.idInventario = ?
                    JOIN Horario h2 ON ih2.idHorario = h2.idHorario
                
                    WHERE oi.idTipoObjeto = ?
                    AND (
                    
                        h1.fechaInc < h2.fechaFin
                        AND h1.fechaFin > h2.fechaInc
                
                        AND h1.horaInc < h2.horaFin
                        AND h1.horaFin > h2.horaInc
                    )
            """;

            PreparedStatement stmtCheck = conn.prepareStatement(sqlConflicto);
            stmtCheck.setInt(1, inventarioId);
            stmtCheck.setInt(2, objetoId);

            ResultSet rs = stmtCheck.executeQuery();

            if (rs.next()) {
                System.out.println("el tipo de objeto ya está en uso");
                return -1;
            }


            String sqlInsert = "INSERT INTO ObjetoInventario (idInventario, idTipoObjeto) VALUES (?, ?)";

            PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert);
            stmtInsert.setInt(1, inventarioId);
            stmtInsert.setInt(2, objetoId);

            stmtInsert.executeUpdate();

            System.out.println("bbjeto agregado correctamente al inventario");
            return 1;

        } catch (SQLException e) {
            if (e.getMessage().toLowerCase().contains("constraint")) {
                System.out.println("Ya existe o relación inválida");
            } else {
                e.printStackTrace();
            }
        }

        return -1;
    }

}
