package org.example.ax0006.service;

import org.example.ax0006.entity.Rol;
import org.example.ax0006.entity.Usuario;
import org.example.ax0006.repository.RolRepository;
import org.example.ax0006.repository.UsuarioRepository;

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


    public String obtenerNombreRol(int idRol) {
        return rolRepo.obtenerNombreRol(idRol);
    }
}
