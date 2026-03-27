/*
 * MARTIN SANMIGUEL
 * JULIAN LEON
 * ANDRES
 */



package org.example.ax0006.Repository;

import org.example.ax0006.db.H2;
import org.example.ax0006.Entity.Usuario;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

public class UsuarioRepository {

    private H2 h2;
    private List<Usuario> Usuarios = new ArrayList<>();

    //CONSTRUCTOR
    public UsuarioRepository(H2 h2) {
        this.h2 = h2;
    }

    //INSERTA USUARIOS A LA BASE SE DATOS CON AYUDA DEL INSERT INTO A USUARIO:
    public void guardar(Usuario u) {
        String sql = "INSERT INTO Usuario (nombre, contrasena, gmail) VALUES (?, ?, ?)";
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, u.getNombre());
            stmt.setString(2, u.getContrasena());
            stmt.setString(3, u.getEmail());
            stmt.executeUpdate();
            System.out.println("Usuario guardado en BD: " + u.getNombre());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //SE HACE LA CONSULTA AL NOMBRE QUE SE RECIBE COMO PARAMETRO A LA BASE DE DATOS.
    public Usuario buscarPorNombre(String nombre) {
        String sql = "SELECT nombre, contrasena, gmail FROM Usuario WHERE nombre = ?";
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Usuario(rs.getString("nombre"), rs.getString("contrasena"),rs.getString("gmail"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}

