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

    public UsuarioRepository() {
    }

    //INSERTA USUARIOS A LA BASE SE DATOS CON AYUDA DEL INSERT INTO A USUARIO:
    public void guardar(Usuario u) {
        String sql = "INSERT INTO Usuario (nombre, contrasena, gmail) VALUES (?, ?, ?)";
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, u.getNombre());
            stmt.setString(2, u.getContrasena());
            stmt.setString(3, u.getGmail());
            stmt.executeUpdate();
            System.out.println("Usuario guardado en BD: " + u.getNombre());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //SE HACE LA CONSULTA AL NOMBRE QUE SE RECIBE COMO PARAMETRO A LA BASE DE DATOS.
    public Usuario buscarPorNombre(String nombre) {
        String sql = "SELECT idUsuario, nombre, contrasena, gmail, idRol FROM Usuario WHERE nombre = ?";
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("idUsuario"));
                u.setNombre(rs.getString("nombre"));
                u.setContrasena(rs.getString("contrasena"));
                u.setGmail(rs.getString("gmail"));
                u.setIdRol(rs.getInt("idRol"));
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

//METODO PARA OBTENER TODOS LOS USUARIOS REGISTRADOS EN LA BASE DE DATOS
    public List<Usuario> obtenerUsuarios() {
        List<Usuario> lista = new ArrayList<>();

        String sql = "SELECT * FROM Usuario";
        try (Connection conn = new H2().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("idUsuario"));
                u.setNombre(rs.getString("nombre"));
                u.setGmail(rs.getString("gmail"));
                u.setIdRol(rs.getInt("idRol"));

                lista.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    //PERMITE ACTUALIZAR EL ROL DEL USUARIO POR MEDIO DEL IDROL Y IDUSUARIO.
    public void actualizarRol(int idUsuario, int idRol) {
        String sql = "UPDATE Usuario SET idRol = ? WHERE idUsuario = ?";
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRol);
            stmt.setInt(2, idUsuario);
            stmt.executeUpdate();
            System.out.println("Rol actualizado para usuario id: " + idUsuario);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

