package org.example.ax0006.Manager;

import org.example.ax0006.Service.AutenticacionService;


/*ES LA CLASE QUE DEJA OBTENER LA INFORMACION DE LOS SERIVICIOS Y DE LA SESION*/
public class ContextManager {

    //informacion de la sesion
    private SesionManager sesion;

    //Servicios
    private AutenticacionService autenService;

    /*CONSTRUCTOR*/
    public ContextManager(AutenticacionService autenService, SesionManager sesion) {
        this.autenService = autenService;
        this.sesion = sesion;
    }

    /*RETORNA EL SERVICIO DE AUTENTICACION*/
    public AutenticacionService getAutenService() {
        return autenService;
    }

    /*RETORNA LA INFORMACION DE LA SESION*/
    public SesionManager getSesion() {
        return sesion;
    }
}
