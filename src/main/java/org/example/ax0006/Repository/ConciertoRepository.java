package org.example.ax0006.Repository;

import org.example.ax0006.Entity.Concierto;
import org.example.ax0006.Entity.Horario;
import org.example.ax0006.Entity.Usuario;
import org.example.ax0006.db.H2;

import java.util.List;
import java.sql.*;
import java.util.ArrayList;

public class ConciertoRepository {

    private H2 h2;

    public ConciertoRepository(H2 h2) {
        this.h2 = h2;
    }

    public int guardar(Concierto c, int idHorario) {

        String sql = """
        INSERT INTO Concierto (idHorario, aforo, programado)
        VALUES (?, ?, ?)
    """;

        int idConciertoGenerado = 0;

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, idHorario);
            stmt.setInt(2, c.getAforo());
            stmt.setBoolean(3, c.isProgramado());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                idConciertoGenerado = rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return idConciertoGenerado;
    }

    public List<Concierto> obtenerConciertos() {

        List<Concierto> lista = new ArrayList<>();

        String sql = """
        SELECT c.idConcierto, c.aforo, c.programado,
               h.idHorario, h.fecha, h.horaInc, h.horaFin,
               u.idUsuario, u.nombre
        FROM Concierto c
        JOIN Horario h ON c.idHorario = h.idHorario
        LEFT JOIN RolConciertoUsuario rcu ON c.idConcierto = rcu.idConcierto
        LEFT JOIN Usuario u ON rcu.idUsuario = u.idUsuario
    """;

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                // 🔹 Horario
                Horario h = new Horario();
                h.setIdHorario(rs.getInt("idHorario"));
                h.setFecha(rs.getDate("fecha").toLocalDate());
                h.setHoraInicio(rs.getTime("horaInc").toLocalTime());
                h.setHoraFin(rs.getTime("horaFin").toLocalTime());

                // 🔹 Usuario (artista)
                Usuario artista = null;

                if (rs.getObject("idUsuario") != null) {
                    artista = new Usuario();
                    artista.setIdUsuario(rs.getInt("idUsuario"));
                    artista.setNombre(rs.getString("nombre"));
                }

                // 🔹 Concierto
                Concierto c = new Concierto(
                        rs.getInt("idConcierto"),
                        h,
                        rs.getInt("aforo"),
                        artista
                );

                lista.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public List<Concierto> buscarPorArtista(String nombreArtista) {

        List<Concierto> lista = new ArrayList<>();

        String sql = """
        SELECT c.idConcierto, c.aforo, c.programado,
               h.idHorario, h.fecha, h.horaInc, h.horaFin,
               u.idUsuario, u.nombre
        FROM Concierto c
        JOIN Horario h ON c.idHorario = h.idHorario
        JOIN RolConciertoUsuario rcu ON c.idConcierto = rcu.idConcierto
        JOIN Usuario u ON rcu.idUsuario = u.idUsuario
        WHERE u.nombre = ?
    """;

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombreArtista);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                // 🔹 Horario
                Horario h = new Horario();
                h.setIdHorario(rs.getInt("idHorario"));
                h.setFecha(rs.getDate("fecha").toLocalDate());
                h.setHoraInicio(rs.getTime("horaInc").toLocalTime());
                h.setHoraFin(rs.getTime("horaFin").toLocalTime());

                // 🔹 Usuario (artista)
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("idUsuario"));
                u.setNombre(rs.getString("nombre"));

                // 🔹 Concierto
                Concierto c = new Concierto(
                        rs.getInt("idConcierto"),
                        h,
                        rs.getInt("aforo"),
                        u
                );

                lista.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public void guardarRelacionArtista(int idUsuario, int idConcierto, int idRol) {

        String sql = """
        INSERT INTO RolConciertoUsuario (idRol, idUsuario, idConcierto)
        VALUES (?, ?, ?)
    """;

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
}
