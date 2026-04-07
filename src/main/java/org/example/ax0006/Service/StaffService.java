package org.example.ax0006.Service;

import org.example.ax0006.Entity.Usuario;
import org.example.ax0006.Repository.AsignacionStaffRepository;
import org.example.ax0006.Repository.UsuarioRepository;
import org.example.ax0006.db.H2;
import java.util.List;
import org.example.ax0006.Entity.Concierto;
import org.example.ax0006.Repository.ConciertoRepository;
import java.util.stream.Collectors;

public class StaffService {

    private final UsuarioRepository usuarioRepository;
    private final AsignacionStaffRepository asignacionStaffRepository;
    private final ConciertoRepository conciertoRepository;

    public StaffService(H2 h2) {
        this.usuarioRepository = new UsuarioRepository(h2);
        this.asignacionStaffRepository = new AsignacionStaffRepository(h2);
        this.conciertoRepository = new ConciertoRepository(h2);
    }

    public boolean crearEmpleado(String nombre, String contrasena, String gmail) {
        if (usuarioRepository.buscarPorNombre(nombre) != null) {
            return false;
        }
        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre);
        nuevo.setContrasena(contrasena);
        nuevo.setGmail(gmail);
        nuevo.setIdRol(4); // Staff
        usuarioRepository.guardar(nuevo);
        return true;
    }

    public List<Usuario> listarEmpleados() {
        return usuarioRepository.obtenerUsuarios().stream()
                .filter(u -> u.getIdRol() == 4)
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
    //Metodo que permite obtener el staff que fue asignado a un concierto.
    //Este metodo lo que hace es llamar al REPOSITORY para consultar la informacion en la DB.
    public List<Usuario> obtenerStaffPorConcierto(int idConcierto) {
        return asignacionStaffRepository.obtenerStaffPorConcierto(idConcierto);
    }
    //Metodo que permite obtener la lista de conciertos registrados.
    //Este metodo llama al repository para consultar los conciertos en la base de datos.
    public List<Concierto> listarConciertos() {
        return conciertoRepository.obtenerConciertos();
    }
}