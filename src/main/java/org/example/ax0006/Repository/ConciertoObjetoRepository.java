package org.example.ax0006.Repository;

import org.example.ax0006.db.H2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ConciertoObjetoRepository {

    private H2 h2;

    public ConciertoObjetoRepository(H2 h2) {
        this.h2 = h2;
    }

    public void asignarObjeto(int idConcierto, int idObjeto) {

        String sql = """
            INSERT INTO ConciertoObjeto (idConcierto, idObjeto)
            VALUES (?, ?)
        """;

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idConcierto);
            stmt.setInt(2, idObjeto);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminarAsignacion(int idConcierto, int idObjeto) {

        String sql = """
            DELETE FROM ConciertoObjeto
            WHERE idConcierto = ? AND idObjeto = ?
        """;

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idConcierto);
            stmt.setInt(2, idObjeto);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}