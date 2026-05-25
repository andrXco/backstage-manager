package org.example.ax0006.service;

import org.example.ax0006.entity.Concierto;
import org.example.ax0006.entity.Contrato;
import org.example.ax0006.repository.AsignacionStaffRepository;
import org.example.ax0006.repository.ConciertoRepository;
import org.example.ax0006.repository.HorarioRepository;
import org.example.ax0006.service.ContratoService;
import org.example.ax0006.service.InventarioService;
import org.example.ax0006.validator.ConciertoValidator;

import java.util.ArrayList;
import java.util.List;

public class ConciertoService {

    private ConciertoRepository conciertoRepo;
    private HorarioRepository horarioRepo;
    private ConciertoValidator conciertoValidator;
    private ContratoService contratoService;
    private InventarioService inventarioService;
    private AsignacionStaffRepository asignacionStaffRepo;

    public ConciertoService(ConciertoRepository conciertoRepo, InventarioService inventarioService, HorarioRepository horarioRepo, ConciertoValidator conciertoValidator, ContratoService contratoService, AsignacionStaffRepository asignacionStaffRepo) {
        this.conciertoRepo = conciertoRepo;
        this.horarioRepo = horarioRepo;
        this.conciertoValidator = conciertoValidator;
        this.contratoService = contratoService;
        this.inventarioService = inventarioService;
        this.asignacionStaffRepo = asignacionStaffRepo;
    }

    public void crearConcierto(Concierto c) {

        Contrato contrato = contratoService.obtenerContratoCompleto(c.getIdContrato());
        conciertoValidator.validar(c, contrato);

        // 1. Guardar horario
        int idHorario = horarioRepo.guardar(c.getHorario());

        // 2. Guardar concierto
        int idConcierto = conciertoRepo.guardar(c, idHorario);

        // 3. Guardar relación artista
        conciertoRepo.guardarRelacionArtista(c.getArtista().getIdUsuario(), idConcierto, 3);


        inventarioService.crearDocumentoInventario(idConcierto, idHorario, new ArrayList<>());
    }

    public List<Concierto> obtenerConciertosSolos() {
        return conciertoRepo.obtenerConciertosSolos();
    }

    /*El atributo del programado = true */
    public void aprobarConcierto(int idConcierto) {
        conciertoRepo.aprobarConcierto(idConcierto);
    }

    /*Elimina un concierto y su horario de la base de datos*/
    public void eliminarConcierto(int idConcierto, int idHorario) {
        conciertoRepo.eliminarConcierto(idConcierto);
        try{
            inventarioService.EliminarHorarioInventario(idHorario);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        horarioRepo.eliminarHorario(idHorario);
    }

    public void eliminarInventarioConcierto(int idInventario, int idConcierto, int idHorario, List<Integer> idsObjetos){
        inventarioService.eliminarDocumentoInventario(idInventario, idConcierto, idHorario, idsObjetos);
    }

    public List<Concierto> obtenerConciertosPorUsuarioYRol(int idUsuario, int idRol) {
        return conciertoRepo.obtenerConciertosPorUsuarioYRol(idUsuario, idRol);
    }


    public List<Concierto> listarConciertos() {
        return conciertoRepo.obtenerConciertos();
    }


    public void asignarPresupuesto(int idConcierto,int idAnalisisF) {
    conciertoRepo.asignarAnalisisFinanciero(
            idConcierto,
            idAnalisisF
    );
}
}