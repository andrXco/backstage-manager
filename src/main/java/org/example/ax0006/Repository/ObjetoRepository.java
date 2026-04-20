package org.example.ax0006.Repository;

import org.example.ax0006.Entity.*;
import org.example.ax0006.db.H2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ObjetoRepository {

    private H2 h2;

    public ObjetoRepository(H2 h2) {
        this.h2 = h2;
    }

    public List<Objeto> obtenerDisponibles() {
        List<Objeto> lista = new ArrayList<>();

        String sql = """
            SELECT o.idObjeto, o.estado, o.observaciones, o.disponible,
                   m.idModelo, m.nombre AS modeloNombre,
                   t.idTipoObjeto, t.nombre AS tipoNombre,
                   i.idInventario, i.nombre AS inventarioNombre
            FROM Objeto o
            JOIN ModeloObjeto m ON o.idModelo = m.idModelo
            JOIN TipoObjeto t ON m.idTipoObjeto = t.idTipoObjeto
            JOIN Inventario i ON o.idInventario = i.idInventario
            WHERE o.disponible = TRUE
        """;

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                TipoObjeto tipo = new TipoObjeto(
                        rs.getInt("idTipoObjeto"),
                        rs.getString("tipoNombre")
                );

                ModeloObjeto modelo = new ModeloObjeto(
                        rs.getInt("idModelo"),
                        rs.getString("modeloNombre"),
                        tipo
                );

                Inventario inv = new Inventario(
                        rs.getInt("idInventario"),
                        rs.getString("inventarioNombre")
                );

                Objeto o = new Objeto(
                        rs.getInt("idObjeto"),
                        modelo,
                        inv,
                        rs.getString("estado"),
                        rs.getString("observaciones"),
                        rs.getBoolean("disponible")
                );

                lista.add(o);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public void actualizarDisponibilidad(int idObjeto, boolean disponible) {
        String sql = "UPDATE Objeto SET disponible = ? WHERE idObjeto = ?";

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, disponible);
            stmt.setInt(2, idObjeto);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean estaDisponible(int idObjeto) {

        String sql = "SELECT disponible FROM Objeto WHERE idObjeto = ?";

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idObjeto);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getBoolean("disponible");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}