package org.example.ax0006.Manager;

import org.example.ax0006.Service.AutenticacionService;
import org.example.ax0006.Service.RolService;
import org.example.ax0006.db.H2;


/*ES LA CLASE QUE DEJA OBTENER LA INFORMACION DE LOS SERIVICIOS Y DE LA SESION*/
public class ContextManager {

    //informacion de la sesion
    private SesionManager sesion;

    //Servicios
    private AutenticacionService autenService;
    private RolService rolService;
    private H2 h2;

    /*CONSTRUCTOR*/
    public ContextManager(AutenticacionService autenService, RolService rolService, SesionManager sesion, H2 h2) {
        this.autenService = autenService;
        this.rolService = rolService;
        this.sesion = sesion;
        this.h2 = h2;


    }

    /*RETORNA EL SERVICIO DE AUTENTICACION*/
    public AutenticacionService getAutenService() {
        return autenService;
    }



    public RolService getRolService() { return rolService; }

    /*RETORNA LA INFORMACION DE LA SESION*/
    public SesionManager getSesion() {
        return sesion;
    }

    public H2 getH2() {
        return h2;
    }
}
