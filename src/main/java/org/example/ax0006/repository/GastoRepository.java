package org.example.ax0006.repository;

import org.example.ax0006.db.H2;
import org.example.ax0006.entity.Gasto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GastoRepository {

    private H2 h2;

    public GastoRepository(H2 h2) {
        this.h2 = h2;
    }

    // =========================
    // GUARDAR GASTO
    // =========================
    public int guardar(Gasto gasto) {

        String sql = """
            INSERT INTO Gasto
            (descripcion, valor, idAnalisisF)
            VALUES (?, ?, ?)
        """;

        int idGenerado = 0;

        try (
                Connection conn = h2.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {

            stmt.setString(1, gasto.getDescripcion());
            stmt.setInt(2, gasto.getValor());
            stmt.setInt(3, gasto.getIdAnalisisF());

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
    // ELIMINAR GASTO
    // =========================
    public void eliminar(int idGasto) {

        String sql = """
            DELETE FROM Gasto
            WHERE idGasto = ?
        """;

        try (
                Connection conn = h2.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, idGasto);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // LISTAR GASTOS
    // =========================
    public List<Gasto> listarPorAnalisis(int idAnalisisF) {

        List<Gasto> lista = new ArrayList<>();

        String sql = """
            SELECT *
            FROM Gasto
            WHERE idAnalisisF = ?
        """;

        try (
                Connection conn = h2.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, idAnalisisF);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                Gasto gasto = new Gasto();

                gasto.setIdGasto(rs.getInt("idGasto"));
                gasto.setDescripcion(rs.getString("descripcion"));
                gasto.setValor(rs.getInt("valor"));
                gasto.setIdAnalisisF(rs.getInt("idAnalisisF"));

                lista.add(gasto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // =========================
    // TOTAL GASTOS
    // =========================
    public int calcularTotalGastos(int idAnalisisF) {

        String sql = """
            SELECT SUM(valor) AS total
            FROM Gasto
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