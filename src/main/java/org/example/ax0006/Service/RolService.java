package org.example.ax0006.Service;

import org.example.ax0006.Entity.Rol;
import org.example.ax0006.Entity.Usuario;
import org.example.ax0006.Repository.RolRepository;
import org.example.ax0006.Repository.UsuarioRepository;

import java.util.List;
import java.util.stream.Collectors;

public class RolService {

    private RolRepository rolRepo;
    private UsuarioRepository usuarioRepo;

    public RolService() {
    }

    /*CONSTRUCTOR*/
    public RolService(RolRepository rolRepo, UsuarioRepository usuarioRepo) {
        this.rolRepo = rolRepo;
        this.usuarioRepo = usuarioRepo;
    }

    /*RETORNA LOS ROLES ASIGNABLES */
    public List<Rol> obtenerRolesAsignables() {
        return rolRepo.obtenerRoles()
                .stream()
                .filter(r -> r.getIdRol() != 1)
                .collect(Collectors.toList());
    }


    public List<Usuario> obtenerUsuarios() {
        return usuarioRepo.obtenerUsuarios();
    }


    public void asignarRol(int idUsuario, int idRol) {
        usuarioRepo.actualizarRol(idUsuario, idRol);
    }


    public String obtenerNombreRol(int idRol) {
        return rolRepo.obtenerNombreRol(idRol);
    }
}
