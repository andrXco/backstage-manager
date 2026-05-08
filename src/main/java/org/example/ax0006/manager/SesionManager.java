package org.example.ax0006.manager;

import org.example.ax0006.entity.Concierto;
import org.example.ax0006.entity.Usuario;

/*ESTA CLASE CONTIENE LA INFORMACION DE LA SESION
* POR AHORA ESTA CONTIENE EL USUARIO QUE ACTUALMENTE ESTA LOGEADO A LA APLICACION */
public class SesionManager {

    /*ATRIBUTO*/
    private Usuario usuarioActual;
    private Concierto conciertoTemporal;

    /*METODO PARA OBTENER EL USUARIO ACTUALMENTE LOGEADO EN EL PROGRAMA, SI NADIE SE HA LOGEADO PUES ESTE ESTA EN NULL*/
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /*METODO PARA SETTEAR EL USUARIO ACTUAL, ESTE SE USA EN EL LOGIN*/
    public void setUsuarioActual(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
    }

    /*METODO PARA PONER EL USUARIO ACTUAL EN NULL PARA SIMBOLIZAR QUE SE CERRO LA SESION*/
    public void cerrarSesion() {
        this.usuarioActual = null;
    }

   private Integer idContratoTemporal;

   public void setConciertoTemporal(Concierto conciertoTemporal) {
    this.conciertoTemporal = conciertoTemporal;
}

    public Concierto getConciertoTemporal() {
        return conciertoTemporal;
    }

    public void setIdContratoTemporal(Integer idContratoTemporal) {
        this.idContratoTemporal = idContratoTemporal;
    }

    public Integer getIdContratoTemporal() {
        return idContratoTemporal;
    }

    private String pantallaOrigen;

    public void setPantallaOrigen(String pantallaOrigen) {
        this.pantallaOrigen = pantallaOrigen;
    }

    public String getPantallaOrigen() {
        return pantallaOrigen;
    }

}
