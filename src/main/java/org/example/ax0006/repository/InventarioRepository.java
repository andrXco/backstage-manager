package org.example.ax0006.repository;

import org.example.ax0006.entity.Objeto;
import org.example.ax0006.entity.ReferenciaDeObjeto;
import org.example.ax0006.entity.TipoObjeto;
import org.example.ax0006.db.H2;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventarioRepository {

    private H2 h2;

    public InventarioRepository(H2 h2) {
        this.h2 = h2;
    }

    public int crearDocumentoInventario(int idConcierto, int idHorario, List<Integer> idsObjetos) {
        String sqlDoc = "INSERT INTO DocumentoInventario DEFAULT VALUES";
        String sqlRelConcierto = "INSERT INTO ConciertoDocumentoInventario (idDocumentoInventario, idConcierto) VALUES (?, ?)";
        String sqlRelHorario = "INSERT INTO DocumentoInventarioHorario (idDocumentoInventario, idHorario) VALUES (?, ?)";
        String sqlRelObjetos = "INSERT INTO ObjetoDocumentoInventario (idInventario, idObjeto) VALUES (?, ?)";

        int idGenerado = -1;

        try (Connection conn = h2.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtDoc = conn.prepareStatement(sqlDoc, Statement.RETURN_GENERATED_KEYS)) {
                stmtDoc.executeUpdate();
                ResultSet rs = stmtDoc.getGeneratedKeys();
                if (rs.next()) {
                    idGenerado = rs.getInt(1);
                }
            }

            try (PreparedStatement stmtRelC = conn.prepareStatement(sqlRelConcierto)) {
                stmtRelC.setInt(1, idGenerado);
                stmtRelC.setInt(2, idConcierto);
                stmtRelC.executeUpdate();
            }

            try (PreparedStatement stmtRelH = conn.prepareStatement(sqlRelHorario)) {
                stmtRelH.setInt(1, idGenerado);
                stmtRelH.setInt(2, idHorario);
                stmtRelH.executeUpdate();
            }

            try (PreparedStatement stmtRelO = conn.prepareStatement(sqlRelObjetos)) {
                for (Integer idObjeto : idsObjetos) {
                    stmtRelO.setInt(1, idGenerado);
                    stmtRelO.setInt(2, idObjeto);
                    stmtRelO.addBatch();
                }
                stmtRelO.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idGenerado;
    }

    public List<String> obtenerObjetosPorConcierto(int idConcierto) {
        List<String> lista = new ArrayList<>();
        String sql = """
        SELECT t.tipo, r.referencia 
        FROM ObjetoDocumentoInventario odi
        JOIN ConciertoDocumentoInventario cdi ON odi.idInventario = cdi.idDocumentoInventario
        JOIN Objeto o ON odi.idObjeto = o.idObjeto
        JOIN TipoObjeto t ON o.idTipoObjeto = t.idTipoObjeto
        JOIN ReferenciaDeObjeto r ON o.idReferenciaObjeto = r.idReferenciaObjeto
        WHERE cdi.idConcierto = ?
    """;
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idConcierto);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(rs.getString("tipo") + " - " + rs.getString("referencia"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public List<Objeto> obtenerObjetosCompletosPorConcierto(int idConcierto) {
        List<Objeto> lista = new ArrayList<>();
        String sql = """
        SELECT o.idObjeto, t.idTipoObjeto, t.tipo, r.idReferenciaObjeto, r.referencia 
        FROM ObjetoDocumentoInventario odi
        JOIN ConciertoDocumentoInventario cdi ON odi.idInventario = cdi.idDocumentoInventario
        JOIN Objeto o ON odi.idObjeto = o.idObjeto
        JOIN TipoObjeto t ON o.idTipoObjeto = t.idTipoObjeto
        JOIN ReferenciaDeObjeto r ON o.idReferenciaObjeto = r.idReferenciaObjeto
        WHERE cdi.idConcierto = ?
    """;

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idConcierto);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                TipoObjeto tipo = new TipoObjeto();
                tipo.setIdTipoObjeto(rs.getInt("idTipoObjeto"));
                tipo.setTipo(rs.getString("tipo"));

                ReferenciaDeObjeto ref = new ReferenciaDeObjeto();
                ref.setIdReferenciaObjeto(rs.getInt("idReferenciaObjeto"));
                ref.setReferencia(rs.getString("referencia"));

                Objeto obj = new Objeto();
                obj.setIdObjeto(rs.getInt("idObjeto"));
                obj.setTipoObjeto(tipo);
                obj.setReferenciaDeObjeto(ref);

                lista.add(obj);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Objeto> obtenerObjetosCompletosPorInventario(int idInventario) {
        List<Objeto> lista = new ArrayList<>();
        String sql = """
        SELECT o.idObjeto, t.idTipoObjeto, t.tipo, r.idReferenciaObjeto, r.referencia 
        FROM ObjetoDocumentoInventario odi
        JOIN Objeto o ON odi.idObjeto = o.idObjeto
        JOIN TipoObjeto t ON o.idTipoObjeto = t.idTipoObjeto
        JOIN ReferenciaDeObjeto r ON o.idReferenciaObjeto = r.idReferenciaObjeto
        WHERE odi.idInventario = ?
    """;

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idInventario);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                TipoObjeto tipo = new TipoObjeto();
                tipo.setIdTipoObjeto(rs.getInt("idTipoObjeto"));
                tipo.setTipo(rs.getString("tipo"));

                ReferenciaDeObjeto ref = new ReferenciaDeObjeto();
                ref.setIdReferenciaObjeto(rs.getInt("idReferenciaObjeto"));
                ref.setReferencia(rs.getString("referencia"));

                Objeto obj = new Objeto();
                obj.setIdObjeto(rs.getInt("idObjeto"));
                obj.setTipoObjeto(tipo);
                obj.setReferenciaDeObjeto(ref);

                lista.add(obj);
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar objetos del inventario: " + idInventario);
            e.printStackTrace();
        }
        return lista;
    }

    public void eliminarInventario(int idInventario, int idConcierto, int idHorario, List<Integer> idsObjetos) {
        String sqlRelConcierto = "DELETE FROM ConciertoDocumentoInventario WHERE idDocumentoInventario = ? AND idConcierto = ?";
        String sqlRelHorario = "DELETE FROM DocumentoInventarioHorario WHERE idDocumentoInventario = ? AND idHorario = ?";
        String sqlRelObjetos = "DELETE FROM ObjetoDocumentoInventario WHERE idInventario = ? AND idObjeto = ?";
        String sqlDoc = "DELETE FROM DocumentoInventario WHERE idDocumentoInventario = ?";

        try (Connection conn = h2.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtRelC = conn.prepareStatement(sqlRelConcierto);
                 PreparedStatement stmtRelH = conn.prepareStatement(sqlRelHorario);
                 PreparedStatement stmtRelO = conn.prepareStatement(sqlRelObjetos);
                 PreparedStatement stmtSqlDoc = conn.prepareStatement(sqlDoc)) {

                stmtRelC.setInt(1, idInventario);
                stmtRelC.setInt(2, idConcierto);
                stmtRelC.executeUpdate();

                stmtRelH.setInt(1, idInventario);
                stmtRelH.setInt(2, idHorario);
                stmtRelH.executeUpdate();

                if (idsObjetos != null && !idsObjetos.isEmpty()) {
                    for (Integer idObjeto : idsObjetos) {
                        stmtRelO.setInt(1, idInventario);
                        stmtRelO.setInt(2, idObjeto);
                        stmtRelO.addBatch();
                    }
                    stmtRelO.executeBatch();
                }

                stmtSqlDoc.setInt(1, idInventario);
                int filasAfectadas = stmtSqlDoc.executeUpdate();

                if (filasAfectadas > 0) {
                    conn.commit();
                    System.out.println("Eliminación exitosa en la base de datos.");
                } else {
                    conn.rollback();
                    System.out.println("No se encontró el documento para eliminar.");
                }

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int obtenerDocumentoInventarioConcierto(int idConcierto) {
        String sqlRelConcierto = "SELECT idDocumentoInventario FROM ConciertoDocumentoInventario WHERE idConcierto = ?";
        int idInventarioConcierto = 0;
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlRelConcierto)) {
            stmt.setInt(1, idConcierto);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    idInventarioConcierto = rs.getInt("idDocumentoInventario");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idInventarioConcierto;
    }

    public List<Integer> obtenerInventariosSinConcierto() {
        List<Integer> ids = new ArrayList<>();
        String sql = """
        SELECT d.idDocumentoInventario 
        FROM DocumentoInventario d
        LEFT JOIN ConciertoDocumentoInventario cdi ON d.idDocumentoInventario = cdi.idDocumentoInventario
        WHERE cdi.idConcierto IS NULL OR cdi.idConcierto = 0
    """;
        try (Connection conn = h2.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ids.add(rs.getInt("idDocumentoInventario"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ids;
    }

    public int obtenerIdHorarioPorInventario(int idInventario) {
        String sql = "SELECT idHorario FROM DocumentoInventarioHorario WHERE idDocumentoInventario = ?";
        try (java.sql.Connection conn = h2.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idInventario);
            java.sql.ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("idHorario");
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void romperRelacionesHorario(int idHorario) {
        String sqlRelHorario = "DELETE FROM DocumentoInventarioHorario WHERE idHorario = ?";

        try (Connection conn = h2.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sqlRelHorario)) {
                stmt.setInt(1, idHorario);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

