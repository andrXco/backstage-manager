package org.example.ax0006.Manager;

import org.example.ax0006.Repository.ConciertoRepository;
import org.example.ax0006.Repository.HorarioRepository;
import org.example.ax0006.Repository.RolRepository;
import org.example.ax0006.Repository.UsuarioRepository;
import org.example.ax0006.Service.AutenticacionService;
import org.example.ax0006.Service.ConciertoService;
import org.example.ax0006.Service.ProfileService;
import org.example.ax0006.Service.RolService;
import org.example.ax0006.db.H2;

public class ContextManager {

    private H2 h2;
    private UsuarioRepository usuarioRepository;
    private RolRepository rolRepository;
    private AutenticacionService autenService;
    private ProfileService profileService;
    private RolService rolService;
    private SesionManager sesion;
    private ConciertoService conciertoService;
    private HorarioRepository horarioRepo;
    private ConciertoRepository conciertoRepo;

    public ContextManager(
            H2 h2,
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            HorarioRepository horarioRepo,
            ConciertoRepository conciertoRepo,
            AutenticacionService autenService,
            ProfileService profileService,
            RolService rolService,
            ConciertoService conciertoService,
            SesionManager sesion
    ) {
        this.h2 = h2;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.horarioRepo = horarioRepo;
        this.conciertoRepo = conciertoRepo;
        this.autenService = autenService;
        this.profileService = profileService;
        this.conciertoService = conciertoService;
        this.rolService = rolService;
        this.sesion = sesion;
    }

    public H2 getH2() {
        return h2;
    }

    public UsuarioRepository getUsuarioRepository() {
        return usuarioRepository;
    }

    public RolRepository getRolRepository() {
        return rolRepository;
    }

    public AutenticacionService getAutenService() {
        return autenService;
    }

    public ProfileService getProfileService() {
        return profileService;
    }

    public RolService getRolService() {
        return rolService;
    }

    public SesionManager getSesion() {
        return sesion;
    }

    public ConciertoService getConciertoService() {return conciertoService;}
}