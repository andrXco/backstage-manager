package org.example.ax0006.service;

import org.example.ax0006.entity.TipoObjeto;
import org.example.ax0006.repository.ObjetoRepository;
import java.sql.SQLException;
import java.util.List;

public class ObjetoService {
    private final ObjetoRepository objetoRepo;

    public ObjetoService(ObjetoRepository objetoRepo) {
        this.objetoRepo = objetoRepo;
    }

    public List<TipoObjeto> obtenerTipos() throws SQLException {
        return objetoRepo.listarTipos();
    }

    public void crearObjetoConReferencia(String referencia, int idTipoObjeto) throws SQLException {
        objetoRepo.crearObjetoConReferencia(referencia, idTipoObjeto);
    }

    public List<String> obtenerListaObjetosFormateada() {
        return objetoRepo.obtenerListaObjetosFormateada();
    }
}