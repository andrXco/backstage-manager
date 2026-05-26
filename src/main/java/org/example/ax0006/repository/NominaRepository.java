package org.example.ax0006.repository;

import org.example.ax0006.entity.Nomina;
import org.example.ax0006.db.H2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NominaRepository {

    private final H2 h2;

    public NominaRepository(H2 h2) {
        this.h2 = h2;
    }

    public void guardar(Nomina nomina) {
        String sql = "INSERT INTO Nomina (idConcierto, idUsuario, rol, horasTrabajadas, tarifaPorHora, horasExtra, total, estado, pagado) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, nomina.getIdConcierto());
            stmt.setInt(2, nomina.getIdUsuario());
            stmt.setString(3, nomina.getRol());
            stmt.setDouble(4, nomina.getHorasTrabajadas());
            stmt.setDouble(5, nomina.getTarifaPorHora());
            stmt.setDouble(6, nomina.getHorasExtra());
            stmt.setDouble(7, nomina.getTotal());
            stmt.setString(8, nomina.getEstado());
            stmt.setBoolean(9, nomina.isPagado());

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                nomina.setIdNomina(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Nomina> obtenerPorConcierto(int idConcierto) {
        List<Nomina> lista = new ArrayList<>();
        String sql = "SELECT * FROM Nomina WHERE idConcierto = ?";
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idConcierto);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Nomina n = new Nomina();
                n.setIdNomina(rs.getInt("idNomina"));
                n.setIdConcierto(rs.getInt("idConcierto"));
                n.setIdUsuario(rs.getInt("idUsuario"));
                n.setRol(rs.getString("rol"));
                n.setHorasTrabajadas(rs.getDouble("horasTrabajadas"));
                n.setTarifaPorHora(rs.getDouble("tarifaPorHora"));
                n.setHorasExtra(rs.getDouble("horasExtra"));
                n.setTotal(rs.getDouble("total"));
                n.setEstado(rs.getString("estado"));
                n.setPagado(rs.getBoolean("pagado"));
                lista.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void actualizarHorasExtraYTotal(int idNomina,
                                           double horasExtra,
                                           double total) {

        String sql = """
    UPDATE Nomina
    SET horasExtra = ?,
        total = ?
    WHERE idNomina = ?
    """;

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, horasExtra);
            stmt.setDouble(2, total);
            stmt.setInt(3, idNomina);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void actualizarHorasYTarifa(int idNomina, double horasTrabajadas, double tarifaPorHora, double horasExtra) {

        double total = (horasTrabajadas * tarifaPorHora) + (horasExtra * tarifaPorHora * 1.5);

        String sql = """
        UPDATE Nomina
        SET horasTrabajadas = ?,
            tarifaPorHora = ?,
            horasExtra = ?,
            total = ?
        WHERE idNomina = ?
        """;

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, horasTrabajadas);
            stmt.setDouble(2, tarifaPorHora);
            stmt.setDouble(3, horasExtra);
            stmt.setDouble(4, total);
            stmt.setInt(5, idNomina);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void actualizarEstado(int idNomina, String estado) {
        String sql = "UPDATE Nomina SET estado = ? WHERE idNomina = ?";
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, estado);
            stmt.setInt(2, idNomina);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void actualizarHorasYTarifa(int idNomina,
                                       double horasTrabajadas,
                                       double tarifaPorHora) {

        double total = horasTrabajadas * tarifaPorHora;

        String sql = """
        UPDATE Nomina
        SET horasTrabajadas = ?,
            tarifaPorHora = ?,
            total = ?
        WHERE idNomina = ?
        """;

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, horasTrabajadas);
            stmt.setDouble(2, tarifaPorHora);
            stmt.setDouble(3, total);
            stmt.setInt(4, idNomina);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Nomina obtenerPorId(int idNomina) {
        String sql = "SELECT * FROM Nomina WHERE idNomina = ?";
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idNomina);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Nomina n = new Nomina();
                n.setIdNomina(rs.getInt("idNomina"));
                n.setIdConcierto(rs.getInt("idConcierto"));
                n.setIdUsuario(rs.getInt("idUsuario"));
                n.setRol(rs.getString("rol"));
                n.setHorasTrabajadas(rs.getDouble("horasTrabajadas"));
                n.setTarifaPorHora(rs.getDouble("tarifaPorHora"));
                n.setHorasExtra(rs.getDouble("horasExtra"));
                n.setTotal(rs.getDouble("total"));
                n.setEstado(rs.getString("estado"));
                n.setPagado(rs.getBoolean("pagado"));
                return n;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Nomina> obtenerTodas() {

        List<Nomina> lista = new ArrayList<>();

        String sql = "SELECT * FROM Nomina";

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                Nomina n = new Nomina();

                n.setIdNomina(rs.getInt("idNomina"));
                n.setIdConcierto(rs.getInt("idConcierto"));
                n.setIdUsuario(rs.getInt("idUsuario"));
                n.setRol(rs.getString("rol"));
                n.setHorasTrabajadas(rs.getDouble("horasTrabajadas"));
                n.setTarifaPorHora(rs.getDouble("tarifaPorHora"));
                n.setHorasExtra(rs.getDouble("horasExtra"));
                n.setTotal(rs.getDouble("total"));
                n.setEstado(rs.getString("estado"));
                n.setPagado(rs.getBoolean("pagado"));

                lista.add(n);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}
