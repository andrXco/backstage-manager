package org.example.ax0006.Service;

import org.example.ax0006.Entity.Contrato;
import org.example.ax0006.Entity.Clausula;
import org.example.ax0006.Repository.ContratoRepository;

import java.util.List;

public class ContratoService {

    private ContratoRepository contratoRepo;

    // CONSTRUCTOR
    public ContratoService(ContratoRepository contratoRepo) {
        this.contratoRepo = contratoRepo;
    }

    // =========================================
    // CREAR CONTRATO CON SUS CLAUSULAS
    // =========================================
    public int crearContrato(Contrato contrato) {

    if (contrato == null || contrato.getFecha() == null) {
        return 0;
    }

    if (contrato.getClausulas() == null || contrato.getClausulas().isEmpty()) {
        return 0;
    }

    // Guardar contrato
    int idContrato = contratoRepo.guardar(contrato);

    if (idContrato == 0) return 0;

    // Guardar cláusulas
    for (Clausula cl : contrato.getClausulas()) {
        cl.setIdContrato(idContrato);

        boolean guardado = contratoRepo.guardarClausula(cl);

        if (!guardado) {
            return 0;
        }
    }
    return idContrato; 
    }

    // =========================================
    // OBTENER CONTRATO COMPLETO (CON CLAUSULAS)
    // =========================================
    public Contrato obtenerContratoCompleto(int idContrato) {

        // 1. Obtener contrato
        Contrato contrato = contratoRepo.buscarPorId(idContrato);

        if (contrato == null) return null;

        // 2. Obtener cláusulas
        List<Clausula> clausulas = contratoRepo.obtenerClausulasPorContrato(idContrato);

        // 3. Asignarlas al contrato
        contrato.setClausulas(clausulas);

        return contrato;
    }

    // =========================================
    // LISTAR TODOS LOS CONTRATOS
    // =========================================
    public List<Contrato> obtenerContratos() {
        return contratoRepo.obtenerContratos();
    }

    // =========================================
    // OBTENER SOLO CLAUSULAS DE UN CONTRATO
    // =========================================
    public List<Clausula> obtenerClausulas(int idContrato) {
        return contratoRepo.obtenerClausulasPorContrato(idContrato);
    }
}