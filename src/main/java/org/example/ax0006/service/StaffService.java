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

    public List<Usuario> obtenerStaffPorConcierto(int idConcierto) {
        return asignacionStaffRepository.obtenerStaffPorConcierto(idConcierto);
    }

    public List<Usuario> obtenerUsuariosPorConcierto(int idConcierto) {
        return asignacionStaffRepository.obtenerUsuariosPorConcierto(idConcierto);
    }

    public String obtenerNombreRolEnConcierto(int idUsuario, int idConcierto) {
        return asignacionStaffRepository.obtenerNombreRolEnConcierto(idUsuario, idConcierto);
    }

    public String obtenerSubrolStaffEnConcierto(int idUsuario, int idConcierto) {
        return asignacionStaffRepository.obtenerSubrolStaffEnConcierto(idUsuario, idConcierto);
    }

    public boolean actualizarSubrolStaffEnConcierto(int idUsuario, int idConcierto, String subrol) {
        return asignacionStaffRepository.actualizarSubrolStaffEnConcierto(idUsuario, idConcierto, subrol);
    }

    public List<String> obtenerSubrolesDisponibles() {
        return asignacionStaffRepository.obtenerSubrolesDisponibles();
    }

    public int obtenerIdConciertoDelUsuario(int idUsuario) {
        return asignacionStaffRepository.obtenerIdConciertoDelUsuario(idUsuario);
    }

    public List<Integer> obtenerIdsUsuariosAsignados() {
        return asignacionStaffRepository.obtenerIdsUsuariosAsignados();
    }

    /**
     * Elimina un empleado del sistema (solo debe ser llamado por el Administrador)
     */
    public boolean eliminarEmpleado(int idUsuario) {
        // Primero eliminamos todas sus asignaciones
        asignacionStaffRepository.eliminarAsignacionesPorUsuario(idUsuario);

        // Luego eliminamos el usuario
        return usuarioRepository.eliminarPorId(idUsuario);
    }
}