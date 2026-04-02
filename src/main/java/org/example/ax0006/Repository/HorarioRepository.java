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

    public int guardar(Horario h) {
        String sql = "INSERT INTO Horario (fecha, horaInc, horaFin) VALUES (?, ?, ?)";

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDate(1, Date.valueOf(h.getFecha()));
            stmt.setTime(2, Time.valueOf(h.getHoraInicio()));
            stmt.setTime(3, Time.valueOf(h.getHoraFin()));

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // 🔥 id generado
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
