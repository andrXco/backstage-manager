package org.example.ax0006.repository;

import org.example.ax0006.db.H2;
import org.example.ax0006.entity.AnalisisFinanciero;
import org.example.ax0006.entity.Concierto;
import org.example.ax0006.entity.Contrato;
import org.example.ax0006.entity.Horario;
import org.example.ax0006.entity.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConciertoRepository {

    private H2 h2;
    private AnalisisFinancieroRepository analisisRepo;

    public ConciertoRepository(
            H2 h2,
            AnalisisFinancieroRepository analisisRepo
    ) {

        this.h2 = h2;
        this.analisisRepo = analisisRepo;
    }

    /*Metodo para guardar en la base de datos el concierto y su id de horario*/
    public int guardar(Concierto c, int idHorario) {

        String sql = """
            INSERT INTO Concierto
            (nombreConcierto, idHorario, aforo, idContrato, programado)
            VALUES (?, ?, ?, ?, ?)
        """;

        int idConciertoGenerado = 0;

        try (
                Connection conn = h2.getConnection();

                PreparedStatement stmt = conn.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {

            stmt.setString(1, c.getNombreConcierto());
            stmt.setInt(2, idHorario);
            stmt.setInt(3, c.getAforo());
            stmt.setInt(4, c.getIdContrato());
            stmt.setBoolean(5, c.isProgramado());

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

    // Obtiene conciertos simples para dropdowns
    public List<Concierto> obtenerConciertosSolos() {

        List<Concierto> lista = new ArrayList<>();

        String sql = """
            SELECT c.idConcierto,
                   c.nombreConcierto,
                   c.aforo,
                   c.programado,
                   c.idContrato,
                   c.idAnalisisF,
                   h.idHorario,
                   h.fechaInc,
                   h.fechaFin,
                   h.horaInc,
                   h.horaFin
            FROM Concierto c
            JOIN Horario h
                ON c.idHorario = h.idHorario
        """;

        try (
                Connection conn = h2.getConnection();

                PreparedStatement stmt =
                        conn.prepareStatement(sql);

                ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {

                Horario h = new Horario();

                h.setIdHorario(
                        rs.getInt("idHorario")
                );

                h.setFechaInicio(
                        rs.getDate("fechaInc")
                                .toLocalDate()
                );

                h.setFechaFin(
                        rs.getDate("fechaFin")
                                .toLocalDate()
                );

                h.setHoraInicio(
                        rs.getTime("horaInc")
                                .toLocalTime()
                );

                h.setHoraFin(
                        rs.getTime("horaFin")
                                .toLocalTime()
                );

                Concierto c = new Concierto(
                        rs.getInt("idConcierto"),
                        rs.getString("nombreConcierto"),
                        h,
                        rs.getInt("aforo"),
                        null,
                        rs.getBoolean("programado")
                );

                c.setIdContrato(
                        rs.getInt("idContrato")
                );

                int idAnalisis =
                        rs.getInt("idAnalisisF");

                if (!rs.wasNull()) {

                    AnalisisFinanciero af =
                            analisisRepo.buscarPorId(
                                    idAnalisis
                            );

                    c.setAnalisis(af);
                }

                lista.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /*Obtiene conciertos completos*/
    public List<Concierto> obtenerConciertos() {

        List<Concierto> lista = new ArrayList<>();

        String sql = """
            SELECT c.idConcierto,
                   c.nombreConcierto,
                   c.aforo,
                   c.programado,
                   c.idContrato,
                   c.idAnalisisF,
                   h.idHorario,
                   h.fechaInc,
                   h.fechaFin,
                   h.horaInc,
                   h.horaFin,
                   u.idUsuario,
                   u.nombre
            FROM Concierto c
            JOIN Horario h
                ON c.idHorario = h.idHorario
            LEFT JOIN RolConciertoUsuario rcu
                ON c.idConcierto = rcu.idConcierto
                AND rcu.idRol = 3
            LEFT JOIN Usuario u
                ON rcu.idUsuario = u.idUsuario
        """;

        try (
                Connection conn = h2.getConnection();

                PreparedStatement stmt =
                        conn.prepareStatement(sql);

                ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {

                Horario h = new Horario();

                h.setIdHorario(
                        rs.getInt("idHorario")
                );

                h.setFechaInicio(
                        rs.getDate("fechaInc")
                                .toLocalDate()
                );

                h.setFechaFin(
                        rs.getDate("fechaFin")
                                .toLocalDate()
                );

                h.setHoraInicio(
                        rs.getTime("horaInc")
                                .toLocalTime()
                );

                h.setHoraFin(
                        rs.getTime("horaFin")
                                .toLocalTime()
                );

                Usuario artista = null;

                if (rs.getObject("idUsuario") != null) {

                    artista = new Usuario();

                    artista.setIdUsuario(
                            rs.getInt("idUsuario")
                    );

                    artista.setNombre(
                            rs.getString("nombre")
                    );
                }

                Concierto c = new Concierto(
                        rs.getInt("idConcierto"),
                        rs.getString("nombreConcierto"),
                        h,
                        rs.getInt("aforo"),
                        artista,
                        rs.getBoolean("programado")
                );

                c.setIdContrato(
                        rs.getInt("idContrato")
                );

                int idAnalisis =
                        rs.getInt("idAnalisisF");

                if (!rs.wasNull()) {

                    AnalisisFinanciero af =
                            analisisRepo.buscarPorId(
                                    idAnalisis
                            );

                    c.setAnalisis(af);
                }

                lista.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // NUEVO METODO
    public List<Concierto> obtenerConciertosPorUsuarioYRol(
            int idUsuario,
            int idRol
    ) {

        List<Concierto> lista = new ArrayList<>();

        String sql = """
            SELECT c.idConcierto,
                   c.nombreConcierto,
                   c.aforo,
                   c.programado,
                   c.idContrato,
                   c.idAnalisisF,
                   h.idHorario,
                   h.fechaInc,
                   h.fechaFin,
                   h.horaInc,
                   h.horaFin,
                   u.idUsuario,
                   u.nombre
            FROM Concierto c
            JOIN Horario h
                ON c.idHorario = h.idHorario
            JOIN RolConciertoUsuario rcu
                ON c.idConcierto = rcu.idConcierto
            LEFT JOIN Usuario u
                ON rcu.idUsuario = u.idUsuario
            WHERE rcu.idUsuario = ?
              AND rcu.idRol = ?
        """;

        try (
                Connection conn = h2.getConnection();

                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idRol);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                Horario h = new Horario();

                h.setIdHorario(
                        rs.getInt("idHorario")
                );

                h.setFechaInicio(
                        rs.getDate("fechaInc")
                                .toLocalDate()
                );

                h.setFechaFin(
                        rs.getDate("fechaFin")
                                .toLocalDate()
                );

                h.setHoraInicio(
                        rs.getTime("horaInc")
                                .toLocalTime()
                );

                h.setHoraFin(
                        rs.getTime("horaFin")
                                .toLocalTime()
                );

                Usuario u = null;

                if (rs.getObject("idUsuario") != null) {

                    u = new Usuario();

                    u.setIdUsuario(
                            rs.getInt("idUsuario")
                    );

                    u.setNombre(
                            rs.getString("nombre")
                    );
                }

                Concierto c = new Concierto(
                        rs.getInt("idConcierto"),
                        rs.getString("nombreConcierto"),
                        h,
                        rs.getInt("aforo"),
                        u,
                        rs.getBoolean("programado")
                );

                c.setIdContrato(
                        rs.getInt("idContrato")
                );

                int idAnalisis =
                        rs.getInt("idAnalisisF");

                if (!rs.wasNull()) {

                    AnalisisFinanciero af =
                            analisisRepo.buscarPorId(
                                    idAnalisis
                            );

                    c.setAnalisis(af);
                }

                lista.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /*Buscar por artista*/
    public List<Concierto> buscarPorArtista(
            String nombreArtista
    ) {

        List<Concierto> lista = new ArrayList<>();

        String sql = """
            SELECT c.idConcierto,
                   c.nombreConcierto,
                   c.aforo,
                   c.programado,
                   c.idContrato,
                   c.idAnalisisF,
                   h.idHorario,
                   h.fechaInc,
                   h.fechaFin,
                   h.horaInc,
                   h.horaFin,
                   u.idUsuario,
                   u.nombre
            FROM Concierto c
            JOIN Horario h
                ON c.idHorario = h.idHorario
            JOIN RolConciertoUsuario rcu
                ON c.idConcierto = rcu.idConcierto
            JOIN Usuario u
                ON rcu.idUsuario = u.idUsuario
            WHERE u.nombre = ?
        """;

        try (
                Connection conn = h2.getConnection();

                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ) {

            stmt.setString(1, nombreArtista);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                Horario h = new Horario();

                h.setIdHorario(
                        rs.getInt("idHorario")
                );

                h.setFechaInicio(
                        rs.getDate("fechaInc")
                                .toLocalDate()
                );

                h.setFechaFin(
                        rs.getDate("fechaFin")
                                .toLocalDate()
                );

                h.setHoraInicio(
                        rs.getTime("horaInc")
                                .toLocalTime()
                );

                h.setHoraFin(
                        rs.getTime("horaFin")
                                .toLocalTime()
                );

                Usuario u = new Usuario();

                u.setIdUsuario(
                        rs.getInt("idUsuario")
                );

                u.setNombre(
                        rs.getString("nombre")
                );

                Concierto c = new Concierto(
                        rs.getInt("idConcierto"),
                        rs.getString("nombreConcierto"),
                        h,
                        rs.getInt("aforo"),
                        u,
                        rs.getBoolean("programado")
                );

                c.setIdContrato(
                        rs.getInt("idContrato")
                );

                int idAnalisis =
                        rs.getInt("idAnalisisF");

                if (!rs.wasNull()) {

                    AnalisisFinanciero af =
                            analisisRepo.buscarPorId(
                                    idAnalisis
                            );

                    c.setAnalisis(af);
                }

                lista.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /*Relacion artista*/
    public void guardarRelacionArtista(
            int idUsuario,
            int idConcierto,
            int idRol
    ) {

        String sql = """
            INSERT INTO RolConciertoUsuario
            (idRol, idUsuario, idConcierto)
            VALUES (?, ?, ?)
        """;

        try (
                Connection conn = h2.getConnection();

                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, idRol);
            stmt.setInt(2, idUsuario);
            stmt.setInt(3, idConcierto);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*Aprobar concierto*/
    public void aprobarConcierto(int id) {

        String sql = """
            UPDATE Concierto
            SET programado = TRUE
            WHERE idConcierto = ?
        """;

        try (
                Connection conn = h2.getConnection();

                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, id);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*Eliminar concierto*/
    public void eliminarConcierto(
            int idConcierto
    ) {

        try (
                Connection conn = h2.getConnection()
        ) {

            conn.setAutoCommit(false);

            String sqlInv = """
                DELETE FROM ConciertoDocumentoInventario
                WHERE idConcierto = ?
            """;

            try (
                    PreparedStatement stmt =
                            conn.prepareStatement(sqlInv)
            ) {

                stmt.setInt(1, idConcierto);
                stmt.executeUpdate();
            }

            String sqlRel = """
                DELETE FROM RolConciertoUsuario
                WHERE idConcierto = ?
            """;

            try (
                    PreparedStatement stmt =
                            conn.prepareStatement(sqlRel)
            ) {

                stmt.setInt(1, idConcierto);
                stmt.executeUpdate();
            }

            String sqlConc = """
                DELETE FROM Concierto
                WHERE idConcierto = ?
            """;

            try (
                    PreparedStatement stmt =
                            conn.prepareStatement(sqlConc)
            ) {

                stmt.setInt(1, idConcierto);
                stmt.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Asignar análisis financiero
    public void asignarAnalisisFinanciero(
            int idConcierto,
            Integer idAnalisisF
    ) {

        String sql = """
            UPDATE Concierto
            SET idAnalisisF = ?
            WHERE idConcierto = ?
        """;

        try (
                Connection conn = h2.getConnection();
                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ) {

            if (idAnalisisF == null || idAnalisisF == 0) {
                stmt.setNull(1, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(1, idAnalisisF);
            }

            stmt.setInt(2, idConcierto);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}