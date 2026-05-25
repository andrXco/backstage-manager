package org.example.ax0006.repository;

import org.example.ax0006.db.H2;
import org.example.ax0006.entity.Ingreso;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IngresoRepository {

    private H2 h2;

    public IngresoRepository(H2 h2) {
        this.h2 = h2;
    }

    // =========================
    // GUARDAR INGRESO
    // =========================
    public int guardar(Ingreso ingreso) {

        String sql = """
            INSERT INTO Ingreso
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

            stmt.setString(1, ingreso.getDescripcion());
            stmt.setInt(2, ingreso.getValor());
            stmt.setInt(3, ingreso.getIdAnalisisF());

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
    // ELIMINAR INGRESO
    // =========================
    public void eliminar(int idIngreso) {

        String sql = """
            DELETE FROM Ingreso
            WHERE idIngreso = ?
        """;

        try (
                Connection conn = h2.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, idIngreso);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // LISTAR INGRESOS
    // =========================
    public List<Ingreso> listarPorAnalisis(int idAnalisisF) {

        List<Ingreso> lista = new ArrayList<>();

        String sql = """
            SELECT *
            FROM Ingreso
            WHERE idAnalisisF = ?
        """;

        try (
                Connection conn = h2.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, idAnalisisF);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                Ingreso ingreso = new Ingreso();

                ingreso.setIdIngreso(rs.getInt("idIngreso"));
                ingreso.setDescripcion(rs.getString("descripcion"));
                ingreso.setValor(rs.getInt("valor"));
                ingreso.setIdAnalisisF(rs.getInt("idAnalisisF"));

                lista.add(ingreso);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // =========================
    // TOTAL INGRESOS
    // =========================
    public int calcularTotalIngresos(int idAnalisisF) {

        String sql = """
            SELECT SUM(valor) AS total
            FROM Ingreso
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