/*
 * MARTIN SANMIGUEL
 */


package org.example.pruebafismsd.Service;

import org.example.pruebafismsd.Entity.Usuario;
import org.example.pruebafismsd.Repository.UsuarioRepository;

public class AutenticacionService {

    //LA CLASE ES UN SINGLETON
    private static AutenticacionService instance;

    //OBTIENE EL SINGLETON DE USUARIOREPOSITORY
    private UsuarioRepository UsuarioRepo = UsuarioRepository.getInstance();

    private AutenticacionService() {}

    public static AutenticacionService getInstance() {
        if (instance == null) {
            instance = new AutenticacionService();
        }
        return instance;
    }

    // SIGN UP
    public boolean register(String nombre, String contrasena) {
        if (UsuarioRepo.buscarPorNombre(nombre) != null) {
            System.out.println("Usuario ya existe");
            return false;
        }
        Usuario nuevo = new Usuario(nombre, contrasena);
        UsuarioRepo.guardar(nuevo);

        return true;
    }

    // LOGIN
    public boolean login(String nombre, String contrasena) {

        Usuario u = UsuarioRepo.buscarPorNombre(nombre);
        if (u == null) return false;
        return u.getContrasena().equals(contrasena);
    }
}