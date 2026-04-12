package org.example.ax0006.Repository;

import org.example.ax0006.Entity.Inventario;
import org.example.ax0006.Entity.TipoObjeto;
import org.example.ax0006.Entity.Usuario;
import org.example.ax0006.db.H2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoObjetoRepository {

    private H2 h2;
    private List<TipoObjeto> tiposObjetos = new ArrayList<>();

    //CONSTRUCTOR

    public TipoObjetoRepository(H2 h2) {
        this.h2 = h2;
    }

    //INSERTA TIPOS DE OBJETOS A LA BASE SE DATOS CON AYUDA DEL INSERT INTO A TipoObjeto:
    public int guardarTipoObjeto(TipoObjeto TO) {
        String sql = "INSERT INTO TipoObjeto (nombre) VALUES (?)";
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
            stmt.setString(1, TO.getNombre());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int idGenerado = rs.getInt(1);
                System.out.println("Tipo Objeto guardado en BD: " + TO.getNombre());
                return idGenerado;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //SE HACE LA CONSULTA AL NOMBRE QUE SE RECIBE COMO PARAMETRO A LA BASE DE DATOS.
    public TipoObjeto buscarPorNombreTipoObjeto(String nombre) {
        String sql = "SELECT nombre, idTipoObjeto FROM TipoObjeto WHERE nombre = ?";
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new TipoObjeto(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
