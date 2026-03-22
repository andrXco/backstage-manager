/*
 * MARTIN SANMIGUEL
 * JULIAN LEON
 * ANDRES
 */



package org.example.ax0006.Repository;

import org.example.ax0006.db.H2;
import org.example.ax0006.Entity.Usuario;

import java.sql.*;

//import java.util.ArrayList;
//import java.util.List;

public class UsuarioRepository {

    //SE HACE QUE LA CLASE SEA UN SINGLETON, QUE SOLO HAYA UN OBJETO DE ESTE TIPO EN TODO EL CODIGO
    private static UsuarioRepository instance;

    //DONDE SE GUARDAN LOS USUARIOS, ESTA PENDIENTE LA IMPLEMNTACION DE H2, COMO PARA QUE CARGE LOS DATOS DE LA DB
    //private List<Usuario> usuarios = new ArrayList<>();

    //CONSTRUCTOR
    private UsuarioRepository() {}

    //OBTIENE LA INSTANCIA O LA CREA SI ES LA PRIMERA VEZ
    public static UsuarioRepository getInstance() {
        if (instance == null) {
            instance = new UsuarioRepository();
        }
        return instance;
    }

    //INSERTA USUARIOS A LA BASE SE DATOS CON AYUDA DEL INSERT INTO A USUARIO:
    public void guardar(Usuario u) {
        String sql = "INSERT INTO Usuario (nombre, contrasena) VALUES (?, ?)";
        try (Connection conn = H2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, u.getNombre());
            stmt.setString(2, u.getContrasena());
            stmt.executeUpdate();
            System.out.println("Usuario guardado en BD: " + u.getNombre());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //SE HACE LA CONSULTA AL NOMBRE QUE SE RECIBE COMO PARAMETRO A LA BASE DE DATOS.
    public Usuario buscarPorNombre(String nombre) {
        String sql = "SELECT nombre, contrasena FROM Usuario WHERE nombre = ?";
        try (Connection conn = H2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Usuario(rs.getString("nombre"), rs.getString("contrasena"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

