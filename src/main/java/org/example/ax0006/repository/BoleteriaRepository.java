package org.example.ax0006.repository;

import org.example.ax0006.db.H2;
import org.example.ax0006.entity.Boleteria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BoleteriaRepository {

    private H2 h2;

    public BoleteriaRepository(H2 h2) {
        this.h2 = h2;
    }

    // =========================
    // GUARDAR BOLETERIA
    // =========================
    public int guardar(Boleteria boleteria) {

        String sql = """
            INSERT INTO Boleteria
            (seccion, cantidad, precioBoleta, ingresoTotal, idAnalisisF)
            VALUES (?, ?, ?, ?, ?)
        """;

        int idGenerado = 0;

        try (
                Connection conn = h2.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {

            stmt.setString(1, boleteria.getSeccion());
            stmt.setInt(2, boleteria.getCantidad());
            stmt.setInt(3, boleteria.getPrecioBoleta());
            stmt.setInt(4, boleteria.getIngresoTotal());
            stmt.setInt(5, boleteria.getIdAnalisisF());

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
    // ELIMINAR BOLETERIA
    // =========================
    public void eliminar(int idBoleteria) {

        String sql = """
            DELETE FROM Boleteria
            WHERE idBoleteria = ?
        """;

        try (
                Connection conn = h2.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, idBoleteria);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // LISTAR BOLETERIA
    // =========================
    public List<Boleteria> listarPorAnalisis(int idAnalisisF) {

        List<Boleteria> lista = new ArrayList<>();

        String sql = """
            SELECT *
            FROM Boleteria
            WHERE idAnalisisF = ?
        """;

        try (
                Connection conn = h2.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, idAnalisisF);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                Boleteria boleteria = new Boleteria();

                boleteria.setIdBoleteria(rs.getInt("idBoleteria"));
                boleteria.setSeccion(rs.getString("seccion"));
                boleteria.setCantidad(rs.getInt("cantidad"));
                boleteria.setPrecioBoleta(rs.getInt("precioBoleta"));
                boleteria.setIngresoTotal(rs.getInt("ingresoTotal"));
                boleteria.setIdAnalisisF(rs.getInt("idAnalisisF"));

                lista.add(boleteria);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // =========================
    // TOTAL BOLETERIA
    // =========================
    public int calcularIngresoBoleteria(int idAnalisisF) {

        String sql = """
            SELECT SUM(ingresoTotal) AS total
            FROM Boleteria
            WHERE idAnalisisF = ?
        """;

        try (
                Connection conn = h2.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, idAnalisisF);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}