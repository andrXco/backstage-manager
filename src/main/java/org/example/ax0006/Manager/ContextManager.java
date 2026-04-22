package org.example.ax0006.Manager;

import org.example.ax0006.Repository.ConciertoRepository;
import org.example.ax0006.Repository.ContratoRepository;
import org.example.ax0006.Repository.HorarioRepository;
import org.example.ax0006.Repository.RolRepository;
import org.example.ax0006.Repository.UsuarioRepository;
import org.example.ax0006.Service.*;
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

    private InventarioService inventarioService;
    private crearTipoObjetoService crearTipoObjetoService;
    private InventarioObjetoService inventarioObjetoService;
    private consultarInventarioService consultarInventarioService;
    private ContratoService contratoService;
    private ContratoRepository contratoRepo;

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
            InventarioService inventarioService,
            crearTipoObjetoService crearTipoObjetoService,
            InventarioObjetoService inventarioObjetoService,
            consultarInventarioService consultarInventarioService,
            StaffService service, ConciertoRepository conciertoRepoExtra,
            ContratoService contratoService,
            ContratoRepository contratoRepo
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

        this.inventarioService = inventarioService;
        this.crearTipoObjetoService = crearTipoObjetoService;
        this.inventarioObjetoService = inventarioObjetoService;
        this.consultarInventarioService = consultarInventarioService;
    }


//
//    public ProfileService getProfileService() {
//        return profileService;
//        this.contratoService = contratoService;
//        this.contratoRepo = contratoRepo;
//    }


    public InventarioService getInventarioService() {
        return inventarioService;
    }

    public crearTipoObjetoService getCrearTipoObjetoService() {
        return crearTipoObjetoService;
    }

    public InventarioObjetoService getInventarioObjetoService() {
        return inventarioObjetoService;
    }

    public consultarInventarioService getConsultarInventarioService() {
        return consultarInventarioService;
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
}
