package org.example.ax0006.Repository;

import org.example.ax0006.Entity.Horario;
import org.example.ax0006.db.H2;

import java.sql.*;

/*ESTA ES UNA IMPLEMENTACION PARCIAL*/
public class HorarioRepository {

    private H2 h2;

    public HorarioRepository(H2 h2) {
        this.h2 = h2;
    }

    /*Guarda un horario en la base de datos*/
    public int guardar(Horario h) {
        String sql = "INSERT INTO Horario (fechaInc, fechaFin, horaInc, horaFin) VALUES (?, ?, ?, ?)";

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDate(1, Date.valueOf(h.getFechaInicio()));
            stmt.setDate(2,Date.valueOf(h.getFechaFin()));
            stmt.setTime(3, Time.valueOf(h.getHoraInicio()));
            stmt.setTime(4, Time.valueOf(h.getHoraFin()));

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /*Elimina un horario de su base de datos*/
    public void eliminarHorario(int idHorario) {

        try (Connection conn = h2.getConnection()) {
            // 2. borrar horario
            String sql2 = "DELETE FROM HORARIO WHERE IDHORARIO = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql2)) {
                stmt.setInt(1, idHorario);
                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
