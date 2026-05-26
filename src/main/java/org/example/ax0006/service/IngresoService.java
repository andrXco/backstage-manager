package org.example.ax0006.service;

import org.example.ax0006.entity.Ingreso;
import org.example.ax0006.repository.IngresoRepository;

import java.util.List;

public class IngresoService {

    private IngresoRepository ingresoRepo;

    public IngresoService(
            IngresoRepository ingresoRepo
    ) {

        this.ingresoRepo = ingresoRepo;
    }

    // =========================
    // AGREGAR INGRESO
    // =========================
    public int agregarIngreso(
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

        Ingreso ingreso =
                new Ingreso();

        ingreso.setDescripcion(
                descripcion
        );

        ingreso.setValor(
                valor
        );

        ingreso.setIdAnalisisF(
                idAnalisisF
        );

        return ingresoRepo.guardar(
                ingreso
        );
    }

    // =========================
    // ELIMINAR INGRESO
    // =========================
    public void eliminarIngreso(
            int idIngreso
    ) {

        ingresoRepo.eliminar(
                idIngreso
        );
    }

    // =========================
    // LISTAR INGRESOS
    // =========================
    public List<Ingreso> listarIngresos(
            int idAnalisisF
    ) {

        return ingresoRepo.listarPorAnalisis(
                idAnalisisF
        );
    }

    // =========================
    // TOTAL INGRESOS
    // =========================
    public int obtenerTotalIngresos(
            int idAnalisisF
    ) {

        return ingresoRepo.calcularTotalIngresos(
                idAnalisisF
        );
    }
}