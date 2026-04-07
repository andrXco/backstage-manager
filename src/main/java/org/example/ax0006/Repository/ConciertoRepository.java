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

    /*Metodo para guardar en la base de datos el concierto y su id de horario*/
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

    /*Obtiene conciertos de la base de datos y los guarda como objetos, ademas tambien trae el objeto del artista y el objeto de su horario*/
    public List<Concierto> obtenerConciertos() {

        List<Concierto> lista = new ArrayList<>();

        String sql = """
        SELECT c.idConcierto, c.aforo, c.programado,
               h.idHorario, h.fechaInc, h.fechaFin, h.horaInc, h.horaFin,
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

                // Horario
                Horario h = new Horario();
                h.setIdHorario(rs.getInt("idHorario"));
                h.setFechaInicio(rs.getDate("fechaInc").toLocalDate());
                h.setFechaFin(rs.getDate("fechaFin").toLocalDate());
                h.setHoraInicio(rs.getTime("horaInc").toLocalTime());
                h.setHoraFin(rs.getTime("horaFin").toLocalTime());

                // Usuario (artista)
                Usuario artista = null;

                if (rs.getObject("idUsuario") != null) {
                    artista = new Usuario();
                    artista.setIdUsuario(rs.getInt("idUsuario"));
                    artista.setNombre(rs.getString("nombre"));
                }

                // Concierto
                Concierto c = new Concierto(
                        rs.getInt("idConcierto"),
                        h,
                        rs.getInt("aforo"),
                        artista,
                        rs.getBoolean("programado")
                );

                lista.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /*No se utiliza pero se deja una implementacion parcial porque puede ser relevante en un futuro*/
    public List<Concierto> buscarPorArtista(String nombreArtista) {

        List<Concierto> lista = new ArrayList<>();

        String sql = """
        SELECT c.idConcierto, c.aforo, c.programado,
               h.idHorario, h.fechaInc, h.fechaFin, h.horaInc, h.horaFin,
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

                // Horario
                Horario h = new Horario();
                h.setIdHorario(rs.getInt("idHorario"));
                h.setFechaInicio(rs.getDate("fechaInc").toLocalDate());
                h.setFechaFin(rs.getDate("fechaFin").toLocalDate());
                h.setHoraInicio(rs.getTime("horaInc").toLocalTime());
                h.setHoraFin(rs.getTime("horaFin").toLocalTime());

                // Usuario (artista)
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("idUsuario"));
                u.setNombre(rs.getString("nombre"));

                // Concierto
                Concierto c = new Concierto(
                        rs.getInt("idConcierto"),
                        h,
                        rs.getInt("aforo"),
                        u,
                        rs.getBoolean("programado")
                );

                lista.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /*Genera la relacion RolConciertoArtista*/
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

    /*Cambia el atributo de Programado de false a true*/
    public void aprobarConcierto(int id) {

        String sql = "UPDATE Concierto SET programado = TRUE WHERE idConcierto = ?";

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*Elimina un concierto de la base de datos*/
    public void eliminarConcierto(int idConcierto) {

        try (Connection conn = h2.getConnection()) {

            // 1. borrar relación artista
            String sql1 = "DELETE FROM RolConciertoUsuario WHERE idConcierto = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql1)) {
                stmt.setInt(1, idConcierto);
                stmt.executeUpdate();
            }

            // 2. borrar concierto
            String sql2 = "DELETE FROM Concierto WHERE idConcierto = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql2)) {
                stmt.setInt(1, idConcierto);
                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}