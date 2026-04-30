package org.example.ax0006.service;

import org.example.ax0006.entity.Usuario;
import org.example.ax0006.repository.AsignacionStaffRepository;
import org.example.ax0006.repository.UsuarioRepository;

import java.util.List;
import java.util.stream.Collectors;

public class StaffService {

    private final UsuarioRepository usuarioRepository;
    private final AsignacionStaffRepository asignacionStaffRepository;

    //Correcion de recibir repositorios ya creados.



    public StaffService(UsuarioRepository usuarioRepository, AsignacionStaffRepository asignacionStaffRepository) {
        this.usuarioRepository = usuarioRepository;
        this.asignacionStaffRepository = asignacionStaffRepository;
    }

    public boolean crearEmpleado(String nombre, String contrasena, String gmail) {
        if (usuarioRepository.buscarPorNombre(nombre) != null) {
            return false;
        }
        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre);
        nuevo.setContrasena(contrasena);
        nuevo.setGmail(gmail);

        usuarioRepository.guardar(nuevo);
        return true;
    }

    public List<Usuario> listarEmpleados() {
        return usuarioRepository.obtenerUsuarios().stream()
                .collect(Collectors.toList());
    }

    public boolean asignarStaffAConcierto(int idUsuario, int idConcierto, int idRol) {
        if (asignacionStaffRepository.existeAsignacion(idUsuario, idConcierto, idRol)) {
            return false;
        }
        asignacionStaffRepository.asignarStaffAConcierto(idUsuario, idConcierto, idRol);
        return true;
    }

    public void eliminarAsignacion(int idUsuario, int idConcierto, int idRol) {
        asignacionStaffRepository.eliminarAsignacion(idUsuario, idConcierto, idRol);
    }

    public double generarNomina(int idConcierto) {
        // TODO: implementar cálculo real
        return 0.0;
    }



    // Retorna los ids de todos los usuarios que tienen al menos una asignación en RolConciertoUsuario
    public List<Integer> obtenerIdsUsuariosAsignados() {
        return asignacionStaffRepository.obtenerIdsUsuariosAsignados();
    }

    // Retorna los usuarios asignados a un concierto específico
    public List<Usuario> obtenerUsuariosPorConcierto(int idConcierto) {
        return asignacionStaffRepository.obtenerUsuariosPorConcierto(idConcierto);
    }

    // Retorna el nombre del rol del usuario en un concierto específico
    public String obtenerNombreRolEnConcierto(int idUsuario, int idConcierto) {
        return asignacionStaffRepository.obtenerNombreRolEnConcierto(idUsuario, idConcierto);
    }
}