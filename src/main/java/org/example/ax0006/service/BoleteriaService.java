package org.example.ax0006.service;

import org.example.ax0006.entity.Boleteria;
import org.example.ax0006.repository.BoleteriaRepository;

import java.util.List;

public class BoleteriaService {

    private BoleteriaRepository boleteriaRepo;

    public BoleteriaService(
            BoleteriaRepository boleteriaRepo
    ) {

        this.boleteriaRepo = boleteriaRepo;
    }

    // =========================
    // AGREGAR BOLETERIA
    // =========================
    public int agregarBoleteria(
            String seccion,
            int cantidad,
            int precioBoleta,
            int idAnalisisF
    ) {

        if (seccion == null || seccion.isBlank()) {
            return 0;
        }

        if (cantidad <= 0 || precioBoleta <= 0) {
            return 0;
        }

        int ingresoTotal =
                cantidad * precioBoleta;

        Boleteria boleteria =
                new Boleteria();

        boleteria.setSeccion(seccion);
        boleteria.setCantidad(cantidad);
        boleteria.setPrecioBoleta(precioBoleta);
        boleteria.setIngresoTotal(ingresoTotal);
        boleteria.setIdAnalisisF(idAnalisisF);

        return boleteriaRepo.guardar(
                boleteria
        );
    }

    // =========================
    // ELIMINAR BOLETERIA
    // =========================
    public void eliminarBoleteria(
            int idBoleteria
    ) {

        boleteriaRepo.eliminar(
                idBoleteria
        );
    }

    // =========================
    // LISTAR BOLETERIA
    // =========================
    public List<Boleteria> listarBoleteria(
            int idAnalisisF
    ) {

        return boleteriaRepo.listarPorAnalisis(
                idAnalisisF
        );
    }

    // =========================
    // TOTAL BOLETERIA
    // =========================
    public int obtenerTotalBoleteria(
            int idAnalisisF
    ) {

        return boleteriaRepo.calcularIngresoBoleteria(
                idAnalisisF
        );
    }
}