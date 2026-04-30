/*
 * MARTIN SANMIGUEL
 * JULIAN LEON
 * ANDRES
 */



package org.example.ax0006.repository;

import org.mindrot.jbcrypt.BCrypt;
import org.example.ax0006.db.H2;
import org.example.ax0006.entity.Usuario;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

public class UsuarioRepository {

    private H2 h2;

    

    //CONSTRUCTOR
    public UsuarioRepository(H2 h2) {
        this.h2 = h2;
    }


    //INSERTA USUARIOS A LA BASE SE DATOS CON AYUDA DEL INSERT INTO A USUARIO:
    public boolean guardar(Usuario u) {
        String sql = "INSERT INTO Usuario (nombre, contrasena, gmail) VALUES (?, ?, ?)";
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
                String hash = BCrypt.hashpw(u.getContrasena(), BCrypt.gensalt());
            stmt.setString(1, u.getNombre());
            stmt.setString(2, hash);
            stmt.setString(3, u.getGmail());
            stmt.executeUpdate();
            System.out.println("Usuario guardado en BD: " + u.getNombre());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //SE HACE LA CONSULTA AL NOMBRE QUE SE RECIBE COMO PARAMETRO A LA BASE DE DATOS.
    public Usuario buscarPorNombre(String nombre) {
        String sql = """
                SELECT u.idUsuario, u.nombre, u.contrasena, u.gmail,
                       u.telefono, u.direccion,
                       u.contactoEmergenciaNombre, u.contactoEmergenciaTelefono, u.contactoEmergenciaRelacion
                FROM Usuario u
                WHERE u.nombre = ?
                """;

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
                u.setTelefono(rs.getString("telefono"));
                u.setDireccion(rs.getString("direccion"));
                u.setContactoEmergenciaNombre(rs.getString("contactoEmergenciaNombre"));
                u.setContactoEmergenciaTelefono(rs.getString("contactoEmergenciaTelefono"));
                u.setContactoEmergenciaRelacion(rs.getString("contactoEmergenciaRelacion"));
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
                lista.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public String obtenerRolesDelUsuario(int idUsuario) {
        String sql = """
        SELECT DISTINCT r.rol FROM RolConciertoUsuario rcu
        JOIN Rol r ON rcu.idRol = r.idRol
        WHERE rcu.idUsuario = ?
    """;
        List<String> roles = new ArrayList<>();
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                roles.add(rs.getString("rol"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles.isEmpty() ? "Sin rol" : String.join(", ", roles);
    }



    //PERMITE BUSCAR EL USUARIO Y MOSTRARLO EN SU PERFIL
    public Usuario buscarCompletoPorId(int idUsuario) {
        String sql = """
        SELECT 
            u.idUsuario,
            u.nombre,
            u.gmail,
            u.contrasena,
            u.telefono,
            u.direccion,
            u.contactoEmergenciaNombre,
            u.contactoEmergenciaTelefono,
            u.contactoEmergenciaRelacion
        FROM Usuario u
        WHERE u.idUsuario = ?
    """;

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("idUsuario"));
                u.setNombre(rs.getString("nombre"));
                u.setGmail(rs.getString("gmail"));
                u.setContrasena(rs.getString("contrasena"));
                u.setTelefono(rs.getString("telefono"));
                u.setDireccion(rs.getString("direccion"));
                u.setContactoEmergenciaNombre(rs.getString("contactoEmergenciaNombre"));
                u.setContactoEmergenciaTelefono(rs.getString("contactoEmergenciaTelefono"));
                u.setContactoEmergenciaRelacion(rs.getString("contactoEmergenciaRelacion"));
                return u;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void actualizarPerfil(Usuario u) {
        String sql = """
        UPDATE Usuario
        SET nombre = ?,
            gmail = ?,
            telefono = ?,
            direccion = ?,
            contactoEmergenciaNombre = ?,
            contactoEmergenciaTelefono = ?,
            contactoEmergenciaRelacion = ?
        WHERE idUsuario = ?
    """;

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, u.getNombre());
            stmt.setString(2, u.getGmail());
            stmt.setString(3, u.getTelefono());
            stmt.setString(4, u.getDireccion());
            stmt.setString(5, u.getContactoEmergenciaNombre());
            stmt.setString(6, u.getContactoEmergenciaTelefono());
            stmt.setString(7, u.getContactoEmergenciaRelacion());
            stmt.setInt(8, u.getIdUsuario());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean actualizarContrasena(int idUsuario, String nuevaContrasena) {
        String sql = "UPDATE Usuario SET contrasena = ? WHERE idUsuario = ?";

        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String hash = BCrypt.hashpw(nuevaContrasena, BCrypt.gensalt()); 

            stmt.setString(1, hash);
            stmt.setInt(2, idUsuario);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

