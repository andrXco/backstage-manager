package org.example.ax0006.repository;

import org.example.ax0006.entity.Horario;
import org.example.ax0006.db.H2;

import java.sql.*;

/*ESTA ES UNA IMPLEMENTACION PARCIAL*/
public class HorarioRepository {

    private H2 h2;

    public HorarioRepository(H2 h2) {
        this.h2 = h2;
    }

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

    public void eliminarHorario(int idHorario) {

        try (Connection conn = h2.getConnection()) {
            String sql2 = "DELETE FROM HORARIO WHERE IDHORARIO = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql2)) {
                stmt.setInt(1, idHorario);
                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Horario obtenerHorarioPorId(int idHorario) {
        String sql = "SELECT * FROM Horario WHERE idHorario = ?";

        try (java.sql.Connection conn = h2.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idHorario);

            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Horario horario = new Horario();
                    horario.setIdHorario(rs.getInt("idHorario"));

                    if (rs.getDate("fechaInc") != null) {
                        horario.setFechaInicio(rs.getDate("fechaInc").toLocalDate());
                    }
                    if (rs.getDate("fechaFin") != null) {
                        horario.setFechaFin(rs.getDate("fechaFin").toLocalDate());
                    }

                    if (rs.getTime("horaInc") != null) {
                        horario.setHoraInicio(rs.getTime("horaInc").toLocalTime());
                    }
                    if (rs.getTime("horaFin") != null) {
                        horario.setHoraFin(rs.getTime("horaFin").toLocalTime());
                    }

                    return horario;
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error al obtener el horario con ID: " + idHorario);
            e.printStackTrace();
        }
        return null;
    }
}
