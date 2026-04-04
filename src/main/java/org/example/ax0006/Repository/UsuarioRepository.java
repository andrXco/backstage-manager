/*
 * MARTIN SANMIGUEL
 * JULIAN LEON
 * ANDRES
 */



package org.example.ax0006.Repository;

import org.example.ax0006.Entity.Rol;
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
    public boolean guardar(Usuario u) {
        String sql = "INSERT INTO Usuario (nombre, contrasena, gmail) VALUES (?, ?, ?)";
        try (Connection conn = h2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, u.getNombre());
            stmt.setString(2, u.getContrasena());
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
                SELECT u.idUsuario, u.nombre, u.contrasena, u.gmail, u.idRol,
                       u.telefono, u.direccion,
                       u.contactoEmergenciaNombre, u.contactoEmergenciaTelefono, u.contactoEmergenciaRelacion,
                       r.idRol AS rol_id, r.rol AS rol_nombre
                FROM Usuario u
                LEFT JOIN Rol r ON u.idRol = r.idRol
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

                String rolNombre = rs.getString("rol_nombre");
                if (rolNombre != null) {
                    u.setRol(new Rol(rs.getInt("rol_id"), rolNombre));
                }

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
    //PERMITE BUSCAR EL USUARIO Y MOSTRARLO EN SU PERFIL
    public Usuario buscarCompletoPorId(int idUsuario) {
        String sql = """
        SELECT 
            u.idUsuario,
            u.nombre,
            u.gmail,
            u.contrasena,
            u.idRol,
            u.telefono,
            u.direccion,
            u.contactoEmergenciaNombre,
            u.contactoEmergenciaTelefono,
            u.contactoEmergenciaRelacion,
            r.idRol AS rolId,
            r.rol AS nombreRol
        FROM Usuario u
        LEFT JOIN Rol r ON u.idRol = r.idRol
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

                int rolId = rs.getInt("rolId");
                String nombreRol = rs.getString("nombreRol");

                if (nombreRol != null) {
                    u.setRol(new Rol(rolId, nombreRol));
                }

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

}

