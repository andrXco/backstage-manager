package org.example.ax0006.repository;

import org.example.ax0006.entity.Contrato;
import org.example.ax0006.entity.Clausula;
import org.example.ax0006.db.H2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContratoRepository {

    private H2 h2;

    public ContratoRepository(H2 h2) {
        this.h2 = h2;
    }

    // =========================
    // GUARDAR CONTRATO
    // =========================
    public int guardar(Contrato c) {

        String sql = "INSERT INTO Contrato (fecha) VALUES (?)";
        int idGenerado = 0;

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDate(1, Date.valueOf(c.getFecha()));
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                idGenerado = rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return idGenerado;
    }

    // =========================
    // GUARDAR CLAUSULA
    // =========================
    public boolean guardarClausula(Clausula cl) {

        String sql = "INSERT INTO Clausula (clausula, idContrato) VALUES (?, ?)";

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cl.getClausula());
            stmt.setInt(2, cl.getIdContrato());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================
    // OBTENER TODOS LOS CONTRATOS
    // =========================
    public List<Contrato> obtenerContratos() {

        List<Contrato> lista = new ArrayList<>();

        String sql = "SELECT * FROM Contrato";

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Contrato c = new Contrato();
                c.setIdContrato(rs.getInt("idContrato"));
                c.setFecha(rs.getDate("fecha").toLocalDate());

                lista.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // =========================
    // BUSCAR CONTRATO POR ID
    // =========================
    public Contrato buscarPorId(int id) {

        String sql = "SELECT * FROM Contrato WHERE idContrato = ?";

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Contrato c = new Contrato();
                c.setIdContrato(rs.getInt("idContrato"));
                c.setFecha(rs.getDate("fecha").toLocalDate());
                return c;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // =========================
    // OBTENER CLAUSULAS POR CONTRATO
    // =========================
    public List<Clausula> obtenerClausulasPorContrato(int idContrato) {

        List<Clausula> lista = new ArrayList<>();

        String sql = "SELECT * FROM Clausula WHERE idContrato = ?";

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idContrato);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Clausula cl = new Clausula();
                cl.setIdClausula(rs.getInt("idClausula"));
                cl.setClausula(rs.getString("clausula"));
                cl.setIdContrato(rs.getInt("idContrato"));

                lista.add(cl);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}