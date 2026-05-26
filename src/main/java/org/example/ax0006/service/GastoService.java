package org.example.ax0006.service;

import org.example.ax0006.entity.Gasto;
import org.example.ax0006.repository.GastoRepository;

import java.util.List;

public class GastoService {

    private GastoRepository gastoRepo;

    public GastoService(GastoRepository gastoRepo) {
        this.gastoRepo = gastoRepo;
    }

    // =========================
    // AGREGAR GASTO
    // =========================
    public int agregarGasto(
            String descripcion,
            int valor,
            int idAnalisisF
    ) {

        if (descripcion == null || descripcion.isBlank()) {
            return 0;
        }

        if (valor <= 0) {
            return 0;
        }

        Gasto gasto = new Gasto();

        gasto.setDescripcion(descripcion);
        gasto.setValor(valor);
        gasto.setIdAnalisisF(idAnalisisF);

        return gastoRepo.guardar(gasto);
    }

    // =========================
    // ELIMINAR GASTO
    // =========================
    public void eliminarGasto(int idGasto) {

        gastoRepo.eliminar(idGasto);
    }

    // =========================
    // LISTAR GASTOS
    // =========================
    public List<Gasto> listarGastos(
            int idAnalisisF
    ) {

        return gastoRepo.listarPorAnalisis(
                idAnalisisF
        );
    }

    // =========================
    // TOTAL GASTOS
    // =========================
    public int obtenerTotalGastos(
            int idAnalisisF
    ) {

        return gastoRepo.calcularTotalGastos(
                idAnalisisF
        );
    }
}