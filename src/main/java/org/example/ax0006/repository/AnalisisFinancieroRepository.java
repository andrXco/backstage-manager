package org.example.ax0006.repository;

import org.example.ax0006.db.H2;
import org.example.ax0006.entity.AnalisisFinanciero;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnalisisFinancieroRepository {

    private H2 h2;

    public AnalisisFinancieroRepository(H2 h2) {
        this.h2 = h2;
    }

    // =========================
    // GUARDAR ANALISIS
    // =========================
    public int guardar(AnalisisFinanciero af) {

        String sql = """
            INSERT INTO AnalisisFinanciero
            (presupuesto, aprobado)
            VALUES (?, ?)
        """;

        int idGenerado = 0;

        try (
                Connection conn = h2.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {

            stmt.setInt(1, af.getPresupuesto());
            stmt.setBoolean(2, af.isAprobado());

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
    // BUSCAR POR ID
    // =========================
    public AnalisisFinanciero buscarPorId(int id) {

        String sql = """
            SELECT *
            FROM AnalisisFinanciero
            WHERE idAnalisisF = ?
        """;

        try (
                Connection conn = h2.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                AnalisisFinanciero af =
                        new AnalisisFinanciero();

                af.setIdAnalisisF(
                        rs.getInt("idAnalisisF")
                );

                af.setPresupuesto(
                        rs.getInt("presupuesto")
                );

                af.setAprobado(
                        rs.getBoolean("aprobado")
                );

                return af;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // =========================
    // ACTUALIZAR PRESUPUESTO
    // =========================
    public void actualizarPresupuesto(
            int id,
            int nuevoPresupuesto
    ) {

        String sql = """
            UPDATE AnalisisFinanciero
            SET presupuesto = ?
            WHERE idAnalisisF = ?
        """;

        try (
                Connection conn = h2.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, nuevoPresupuesto);
            stmt.setInt(2, id);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // APROBAR
    // =========================
    public void aprobar(int id) {

        String sql = """
            UPDATE AnalisisFinanciero
            SET aprobado = TRUE
            WHERE idAnalisisF = ?
        """;

        try (
                Connection conn = h2.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, id);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // DESAPROBAR
    // =========================
    public void desaprobar(int id) {

        String sql = """
            UPDATE AnalisisFinanciero
            SET aprobado = FALSE
            WHERE idAnalisisF = ?
        """;

        try (
                Connection conn = h2.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, id);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // ELIMINAR
    // =========================
    public void eliminar(int id) {

        try (Connection conn = h2.getConnection()) {

            conn.setAutoCommit(false);

            // 1. ELIMINAR GASTOS
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM Gasto WHERE idAnalisisF = ?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }

            // 2. ELIMINAR INGRESOS
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM Ingreso WHERE idAnalisisF = ?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }

            // 3. ELIMINAR BOLETERIA
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM Boleteria WHERE idAnalisisF = ?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }

            // 4. ELIMINAR ANALISIS
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM AnalisisFinanciero WHERE idAnalisisF = ?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // LISTAR
    // =========================
    public List<AnalisisFinanciero> listar() {

            List<AnalisisFinanciero> lista =
                    new ArrayList<>();

            String sql = """
                SELECT *
                FROM AnalisisFinanciero
            """;

            try (
                    Connection conn = h2.getConnection();
                    PreparedStatement stmt =
                            conn.prepareStatement(sql)
            ) {

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {

                    AnalisisFinanciero af =
                            new AnalisisFinanciero();

                    af.setIdAnalisisF(
                            rs.getInt("idAnalisisF")
                    );

                    af.setPresupuesto(
                            rs.getInt("presupuesto")
                    );

                    af.setAprobado(
                            rs.getBoolean("aprobado")
                    );

                    lista.add(af);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return lista;
        }


}