package org.example.ax0006.service;

import org.example.ax0006.entity.Concierto;
import org.example.ax0006.entity.Usuario;
import org.example.ax0006.repository.AsignacionStaffRepository;
import org.example.ax0006.repository.ConciertoRepository;
import org.example.ax0006.repository.UsuarioRepository;

import java.util.List;

public class StaffService {

    private final UsuarioRepository usuarioRepository;
    private final AsignacionStaffRepository asignacionStaffRepository;
    private final ConciertoRepository conciertoRepository;

    // Constructor que recibe los repositorios por inyección de dependencias
    public StaffService(UsuarioRepository usuarioRepository,
                        AsignacionStaffRepository asignacionStaffRepository,
                        ConciertoRepository conciertoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.asignacionStaffRepository = asignacionStaffRepository;
        this.conciertoRepository = conciertoRepository;
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
        return usuarioRepository.obtenerUsuarios();
    }

    // Método que permite asignar un rol a un usuario dentro de un concierto
    // No elimina roles anteriores, para permitir que un usuario tenga varios roles en el mismo concierto
    public boolean asignarStaffAConcierto(int idUsuario, int idConcierto, int idRol, String subrol) {
        if (asignacionStaffRepository.existeAsignacion(idUsuario, idConcierto, idRol)) {
            return false;
        }
        asignacionStaffRepository.asignarStaffAConcierto(idUsuario, idConcierto, idRol, subrol);
        return true;
    }

    public void eliminarAsignacion(int idUsuario, int idConcierto, int idRol) {
        asignacionStaffRepository.eliminarAsignacion(idUsuario, idConcierto, idRol);
    }

    public double generarNomina(int idConcierto) {
        return 0.0;
    }

    // Método que permite obtener el staff que fue asignado a un concierto
    public List<Usuario> obtenerStaffPorConcierto(int idConcierto) {
        return asignacionStaffRepository.obtenerStaffPorConcierto(idConcierto);
    }

    // Método que permite obtener la lista de conciertos registrados
    public List<Concierto> listarConciertos() {
        return conciertoRepository.obtenerConciertos();
    }

    // Retorna los ids de todos los usuarios que tienen al menos una asignación
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

    // Retorna el subrol del usuario con rol Staff en un concierto específico desde la base de datos
    public String obtenerSubrolStaffEnConcierto(int idUsuario, int idConcierto) {
        return asignacionStaffRepository.obtenerSubrolStaffEnConcierto(idUsuario, idConcierto);
    }

    // Actualiza el subrol del usuario con rol Staff en un concierto específico en la base de datos
    public boolean actualizarSubrolStaffEnConcierto(int idUsuario, int idConcierto, String subrol) {
        if (subrol == null || subrol.isBlank()) {
            return false;
        }

        return asignacionStaffRepository.actualizarSubrolStaffEnConcierto(idUsuario, idConcierto, subrol);
    }

    // Retorna la lista de subroles disponibles registrados en la base de datos
    public List<String> obtenerSubrolesDisponibles() {
        return asignacionStaffRepository.obtenerSubrolesDisponibles();
    }

    public int obtenerIdConciertoDelUsuario(int idUsuario) {
        return asignacionStaffRepository.obtenerIdConciertoDelUsuario(idUsuario);
    }
}