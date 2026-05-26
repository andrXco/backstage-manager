package org.example.ax0006.service;

import java.util.List;

import org.example.ax0006.entity.AnalisisFinanciero;
import org.example.ax0006.repository.AnalisisFinancieroRepository;

public class AnalisisFinancieroService {

    private AnalisisFinancieroRepository analisisRepo;

    public AnalisisFinancieroService(
            AnalisisFinancieroRepository analisisRepo
    ) {

        this.analisisRepo = analisisRepo;
    }

    // =========================
    // CREAR PRESUPUESTO
    // =========================
    public int crearPresupuesto(int presupuesto) {

        if (presupuesto <= 0) {
            return 0;
        }

        AnalisisFinanciero af =
                new AnalisisFinanciero();

        af.setPresupuesto(presupuesto);
        af.setAprobado(false);

        return analisisRepo.guardar(af);
    }

    // =========================
    // EDITAR PRESUPUESTO
    // =========================
    public void editarPresupuesto(
            int idAnalisis,
            int nuevoPresupuesto
    ) {

        if (nuevoPresupuesto <= 0) {
            return;
        }

        analisisRepo.actualizarPresupuesto(
                idAnalisis,
                nuevoPresupuesto
        );
    }

    // =========================
    // APROBAR
    // =========================
    public void aprobarPresupuesto(int idAnalisis) {

        analisisRepo.aprobar(idAnalisis);
    }

    // =========================
    // DESAPROBAR
    // =========================
    public void desaprobarPresupuesto(int idAnalisis) {

        analisisRepo.desaprobar(idAnalisis);
    }

    // =========================
    // OBTENER ANALISIS
    // =========================
    public AnalisisFinanciero obtenerAnalisis(
            int idAnalisis
    ) {

        return analisisRepo.buscarPorId(
                idAnalisis
        );
    }

    // =========================
    // ELIMINAR ANALISIS
    // =========================
    public void eliminarAnalisis(int idAnalisis) {

        analisisRepo.eliminar(idAnalisis);
    }


    public List<AnalisisFinanciero> listarAnalisis() {

    return analisisRepo.listar();
    }

    
}