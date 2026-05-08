package org.example.ax0006.service;

import org.example.ax0006.entity.Usuario;
import org.example.ax0006.repository.UsuarioRepository;

public class ProfileService {

    private UsuarioRepository usuarioRepository;

    public ProfileService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario obtenerPerfilCompleto(int idUsuario) {
        return usuarioRepository.buscarCompletoPorId(idUsuario);
    }

    public String obtenerRolesDelUsuario(int idUsuario) {
        return usuarioRepository.obtenerRolesDelUsuario(idUsuario);
    }

    public void actualizarPerfil(Usuario usuario) {
        usuarioRepository.actualizarPerfil(usuario);
    }

    public boolean cambiarContrasena(int idUsuario, String nuevaContrasena) {
        return usuarioRepository.actualizarContrasena(idUsuario, nuevaContrasena);
    }
}