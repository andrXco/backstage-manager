package org.example.ax0006.Repository;

import org.example.ax0006.Entity.Inventario;
import org.example.ax0006.Entity.Objeto;
import org.example.ax0006.Entity.ModeloObjeto;
import org.example.ax0006.Entity.TipoObjeto;
import org.example.ax0006.db.H2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventarioRepository {

    private H2 h2;

    public InventarioRepository(H2 h2) {
        this.h2 = h2;
    }

    // Crear inventario
    public int guardar(String nombre) {

        String sql = "INSERT INTO Inventario (nombre) VALUES (?)";

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, nombre);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    // Listar inventarios
    public List<Inventario> obtenerTodos() {

        List<Inventario> lista = new ArrayList<>();

        String sql = "SELECT * FROM Inventario";

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Inventario(
                        rs.getInt("idInventario"),
                        rs.getString("nombre")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Buscar por ID
    public Inventario buscarPorId(int idInventario) {

        String sql = "SELECT * FROM Inventario WHERE idInventario = ?";

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idInventario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Inventario(
                        rs.getInt("idInventario"),
                        rs.getString("nombre")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // obtener objetos de un inventario
    public List<Objeto> obtenerObjetosPorInventario(int idInventario) {

        List<Objeto> lista = new ArrayList<>();

        String sql = """
            SELECT o.idObjeto, o.estado, o.observaciones, o.disponible,
                   m.idModelo, m.nombre AS modeloNombre,
                   t.idTipoObjeto, t.nombre AS tipoNombre
            FROM Objeto o
            JOIN ModeloObjeto m ON o.idModelo = m.idModelo
            JOIN TipoObjeto t ON m.idTipoObjeto = t.idTipoObjeto
            WHERE o.idInventario = ?
        """;

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idInventario);
            ResultSet rs = stmt.executeQuery();

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

                Objeto obj = new Objeto();
                obj.setIdObjeto(rs.getInt("idObjeto"));
                obj.setModelo(modelo);
                obj.setEstado(rs.getString("estado"));
                obj.setObservaciones(rs.getString("observaciones"));
                obj.setDisponible(rs.getBoolean("disponible"));

                lista.add(obj);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Eliminar inventario (opcional)
    public void eliminar(int idInventario) {

        String sql = "DELETE FROM Inventario WHERE idInventario = ?";

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idInventario);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}