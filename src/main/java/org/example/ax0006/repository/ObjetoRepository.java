package org.example.ax0006.repository;

import org.example.ax0006.entity.TipoObjeto;
import org.example.ax0006.db.H2;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ObjetoRepository {
    private final H2 h2;

    public ObjetoRepository(H2 h2) {
        this.h2 = h2;
    }

    public List<String> obtenerListaObjetosFormateada() {
        List<String> lista = new ArrayList<>();
        String sql = """
            SELECT o.idObjeto, t.tipo, r.referencia 
            FROM Objeto o
            JOIN TipoObjeto t ON o.idTipoObjeto = t.idTipoObjeto
            JOIN ReferenciaDeObjeto r ON o.idReferenciaObjeto = r.idReferenciaObjeto
        """;
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                // Formato: "1 - Micrófono (Shure SM58)"
                lista.add(rs.getInt("idObjeto") + " - " + rs.getString("tipo") + " (" + rs.getString("referencia") + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<TipoObjeto> listarTipos() throws SQLException {
        List<TipoObjeto> tipos = new ArrayList<>();
        String sql = "SELECT idTipoObjeto, tipo FROM TipoObjeto";
        try (Connection conn = h2.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tipos.add(new TipoObjeto(rs.getInt("idTipoObjeto"), rs.getString("tipo")));
            }
        }
        return tipos;
    }

    public void crearObjetoConReferencia(String referencia, int idTipoObjeto) throws SQLException {
        String sqlReferencia = "INSERT INTO ReferenciaDeObjeto (referencia) VALUES (?)";
        String sqlObjeto = "INSERT INTO Objeto (idTipoObjeto, idReferenciaObjeto) VALUES (?, ?)";

        try (Connection connection = h2.getConnection()) {
            connection.setAutoCommit(false);
            try {
                int idReferencia;
                try (PreparedStatement stmtRef = connection.prepareStatement(sqlReferencia, Statement.RETURN_GENERATED_KEYS)) {
                    stmtRef.setString(1, referencia);
                    stmtRef.executeUpdate();
                    try (ResultSet rs = stmtRef.getGeneratedKeys()) {
                        if (rs.next()) idReferencia = rs.getInt(1);
                        else throw new SQLException("Error al crear referencia.");
                    }
                }

                try (PreparedStatement stmtObj = connection.prepareStatement(sqlObjeto)) {
                    stmtObj.setInt(1, idTipoObjeto);
                    stmtObj.setInt(2, idReferencia);
                    stmtObj.executeUpdate();
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }
}