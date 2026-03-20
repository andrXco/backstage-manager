/*
 * MARTIN SANMIGUEL
 */



package org.example.pruebafismsd.Repository;

import org.example.pruebafismsd.Entity.Usuario;

import java.util.ArrayList;
import java.util.List;

public class UsuarioRepository {

    //SE HACE QUE LA CLASE SEA UN SINGLETON, QUE SOLO HAYA UN OBJETO DE ESTE TIPO EN TODO EL CODIGO
    private static UsuarioRepository instance;

    //DONDE SE GUARDAN LOS USUARIOS, ESTA PENDIENTE LA IMPLEMNTACION DE H2, COMO PARA QUE CARGE LOS DATOS DE LA DB
    private List<Usuario> usuarios = new ArrayList<>();

    //CONSTRUCTOR
    private UsuarioRepository() {}

    //OBTIENE LA INSTANCIA O LA CREA SI ES LA PRIMERA VEZ
    public static UsuarioRepository getInstance() {
        if (instance == null) {
            instance = new UsuarioRepository();
        }
        return instance;
    }

    //ANADE USUARIOS AL ARRAYLIST, se deberia agregar un metodo que carge los usuarios de H2 utilizando este metodo para guardarlos
    public void guardar(Usuario u) {
        usuarios.add(u);
    }

    public Usuario buscarPorNombre(String nombre) {
        for (Usuario u : usuarios) {
            if (u.getNombre().equals(nombre)) {
                return u;
            }
        }
        return null;
    }
}