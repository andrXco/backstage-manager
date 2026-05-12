package org.example.ax0006.manager;


import org.example.ax0006.repository.*;
import org.example.ax0006.service.*;
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
    private StaffService staffService;
    private ContratoService contratoService;
    private ContratoRepository contratoRepo;
    private ActividadRepository actividadRepository;
    private ActividadService actividadService;

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
            SesionManager sesion,
            StaffService staffService,
            ContratoService contratoService,
            ContratoRepository contratoRepo,
            ActividadRepository actividadRepository,
            ActividadService actividadService
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
        this.staffService = staffService;
        this.contratoService = contratoService;
        this.contratoRepo = contratoRepo;
        this.actividadRepository = actividadRepository;
        this.actividadService = actividadService;
    }




    public H2 getH2() { return h2; }
    public UsuarioRepository getUsuarioRepository() { return usuarioRepository; }
    public RolRepository getRolRepository() { return rolRepository; }
    public AutenticacionService getAutenService() { return autenService; }
    public ProfileService getProfileService() { return profileService; }
    public RolService getRolService() { return rolService; }
    public SesionManager getSesion() { return sesion; }
    public ConciertoService getConciertoService() { return conciertoService; }
    public StaffService getStaffService() { return staffService; }
    public ContratoService getContratoService() { return contratoService; }
    public ContratoRepository getContratoRepository() { return contratoRepo; }
    public ActividadRepository getActividadRepository() { return actividadRepository; }
    public ActividadService getActividadService() { return actividadService; }
}
