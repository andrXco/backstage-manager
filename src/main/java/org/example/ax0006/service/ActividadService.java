package org.example.ax0006.service;

import org.example.ax0006.entity.Actividad;
import org.example.ax0006.entity.Usuario;
import org.example.ax0006.repository.ActividadRepository;
import org.example.ax0006.repository.UsuarioRepository;

import java.util.List;

public class ActividadService {
    private static final String ROL_ADMIN = "Administrador";

    private final ActividadRepository actividadRepository;
    private final UsuarioRepository usuarioRepository;

    public ActividadService(ActividadRepository actividadRepository, UsuarioRepository usuarioRepository) {
        this.actividadRepository = actividadRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public void registrarLogin(Usuario usuario) {
        if (usuario == null) return;

        actividadRepository.registrarActividad(
                "ACCESO",
                "Inicio de sesión",
                "Sistema",
                "El usuario " + usuario.getNombre() + " inició sesión correctamente.",
                usuario.getIdUsuario(),
                ROL_ADMIN
        );
    }

    public void registrarLogout(Usuario usuario) {
        if (usuario == null) return;

        actividadRepository.registrarActividad(
                "ACCESO",
                "Cierre de sesión",
                "Sistema",
                "El usuario " + usuario.getNombre() + " cerró sesión.",
                usuario.getIdUsuario(),
                ROL_ADMIN
        );
    }

    public void registrarAccion(String tipo, String modulo, String descripcion, Usuario actor, String rolDestino) {
        actividadRepository.registrarActividad(
                tipo,
                modulo,
                "Sistema",
                descripcion,
                actor == null ? null : actor.getIdUsuario(),
                rolDestino
        );
    }

    public List<Actividad> listarParaUsuario(Usuario usuario, String filtroTipo) {
        if (usuario == null) {
            return List.of();
        }

        String rol = obtenerRolPrincipal(usuario);
        return actividadRepository.listarParaUsuario(usuario.getIdUsuario(), rol, filtroTipo);
    }

    public int contarPendientes(Usuario usuario) {
        if (usuario == null) return 0;

        String rol = obtenerRolPrincipal(usuario);
        return actividadRepository.contarPendientesParaUsuario(usuario.getIdUsuario(), rol);
    }

    public void marcarRevisado(int idActividad, Usuario usuario) {
        if (usuario == null) return;

        actividadRepository.marcarRevisado(idActividad, usuario.getIdUsuario());
    }

    private String obtenerRolPrincipal(Usuario usuario) {
        if (usuario == null) return "Sin rol";

        if (usuario.getIdUsuario() == 1 || "admin".equalsIgnoreCase(usuario.getNombre())) {
            return ROL_ADMIN;
        }

        String roles = usuarioRepository.obtenerRolesDelUsuario(usuario.getIdUsuario());
        if (roles == null || roles.isBlank() || "Sin rol".equalsIgnoreCase(roles)) {
            return "Sin rol";
        }
        if (roles.contains(ROL_ADMIN)) {
            return ROL_ADMIN;
        }
        return roles.split(",")[0].trim();
    }
}