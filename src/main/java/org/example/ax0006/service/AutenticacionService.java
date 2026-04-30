package org.example.ax0006.service;

import org.example.ax0006.entity.Usuario;
import org.example.ax0006.repository.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;

public class AutenticacionService {

    /*ATRIBUTO*/
    private UsuarioRepository usuarioRepo;

    /*CONSTRUCTOR*/
    public AutenticacionService(UsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    /*METODO PARA SIGN UP, OSEA PARA CREAR EL USUARIOM ADEMAS ESTE CUANDO TIENE EXITO AÑADE EL USUARIO A LA BASE DE DATOS
    * ADEMAS PORQUE ESTE RETORNA UN BOOL, ESTE NOS SIRVE PARA VERIFICAR LAS CONDICIONES DEL SIGN UP*/
    public boolean signUp(String nombre, String contrasena, String gmail) {

        if (usuarioRepo.buscarPorNombre(nombre) != null) {
            return false;
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre);
        nuevo.setContrasena(contrasena);
        nuevo.setGmail(gmail);



        usuarioRepo.guardar(nuevo);

        return true;
    }

    /*METODO PARA EL LOGIN, ESTE SE UTILIZA PARA QUE EN CASO DE QUE EL USUARIO Y CONTRASEÑA SEAN VALIDOS, RETORNE EL USUARIO SINO RETORNARA NULL*/
    public Usuario login(String nombre, String contrasena) {

    Usuario u = usuarioRepo.buscarPorNombre(nombre);

    if (u == null) return null;

    String passwordLimpia = contrasena.trim();

    if (!BCrypt.checkpw(passwordLimpia, u.getContrasena())) return null;

    return u;
    }
}