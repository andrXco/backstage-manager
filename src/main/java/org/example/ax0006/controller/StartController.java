package org.example.ax0006.controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.example.ax0006.manager.ContextManager;
import org.example.ax0006.manager.SesionManager;
import org.example.ax0006.repository.*;
import org.example.ax0006.service.*;
import org.example.ax0006.manager.SceneManager;
import org.example.ax0006.validator.ConciertoValidator;
import org.example.ax0006.validator.HorarioValidator;
import org.example.ax0006.db.H2;

import java.io.IOException;

//ver base de datos:
// PAGINA: http://localhost:8082
// URL: jdbc:h2:./data/eventosdb

public class StartController extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        // BASE DE DATOS
        H2 h2 = new H2();
        h2.inicializarDB();

        // VALIDATORS
        HorarioValidator horarioValidator = new HorarioValidator();
        ConciertoValidator conciertoValidator = new ConciertoValidator(horarioValidator);

        // REPOSITORIOS
        UsuarioRepository usuarioRepo = new UsuarioRepository(h2);
        RolRepository rolRepo = new RolRepository(h2);
        HorarioRepository horarioRepo = new HorarioRepository(h2);
        ConciertoRepository conciertoRepo = new ConciertoRepository(h2);
        AsignacionStaffRepository asignacionStaffRepo = new AsignacionStaffRepository(h2);
        ContratoRepository contratoRepo = new ContratoRepository(h2);

        // SERVICIOS
        AutenticacionService autenService = new AutenticacionService(usuarioRepo);
        ProfileService profileService = new ProfileService(usuarioRepo);
        RolService rolService = new RolService(rolRepo, usuarioRepo);
        ContratoService contratoService = new ContratoService(contratoRepo);
        ConciertoService conciertoService = new ConciertoService(conciertoRepo, horarioRepo, conciertoValidator, contratoService);
        StaffService staffService = new StaffService(usuarioRepo, asignacionStaffRepo);

        // MANAGERS
        SesionManager sesion = new SesionManager();
        ContextManager context = new ContextManager(
                h2,
                usuarioRepo,
                rolRepo,
                horarioRepo,
                conciertoRepo,
                autenService,
                profileService,
                rolService,
                conciertoService,
                sesion,
                staffService,
                conciertoRepo,
                contratoService,
                contratoRepo
        );


        SceneManager sceneManager = new SceneManager(stage, context);

        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

        /*SE REALIZA DE ESTA MANERA PARA QUE EL PROGRAMA NO MUERA EN CASO DE UNA EXCEPCION*/
        try {
            /*CAMBIA DE ESCENA AL LOGIN*/
            sceneManager.showLogin();
        } catch (IOException e) {
            e.printStackTrace();
        }

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }


}