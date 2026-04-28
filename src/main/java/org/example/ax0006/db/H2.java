package org.example.ax0006.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class H2 {
    private static final String URL = "jdbc:h2:./data/eventosdb;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASS = "";

    public H2() {}; //Constructor

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public void inicializarDB() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Rol (
                    idRol INT AUTO_INCREMENT PRIMARY KEY,
                    rol VARCHAR(255) NOT NULL
                )
            """);

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS Usuario (
                idUsuario INT AUTO_INCREMENT PRIMARY KEY,
                nombre VARCHAR(255) NOT NULL,
                gmail VARCHAR(255),
                contrasena VARCHAR(255),           
                telefono VARCHAR(10),
                direccion VARCHAR(255),
                contactoEmergenciaNombre VARCHAR(255),
                contactoEmergenciaTelefono VARCHAR(20),
                contactoEmergenciaRelacion VARCHAR(100)
            )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Contrato (
                    idContrato INT AUTO_INCREMENT PRIMARY KEY,
                    fecha DATE NOT NULL
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Clausula (
                    idClausula INT AUTO_INCREMENT PRIMARY KEY,
                    clausula VARCHAR(255) NOT NULL,
                    idContrato INT,
                    FOREIGN KEY (idContrato) REFERENCES Contrato(idContrato)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS AnalisisFinanciero (
                    idAnalisisF INT AUTO_INCREMENT PRIMARY KEY,
                    presupuesto INT NOT NULL,
                    gastos INT NOT NULL
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Horario (
                    idHorario INT AUTO_INCREMENT PRIMARY KEY,
                    fechaInc DATE NOT NULL,
                    fechaFin DATE NOT NULL,
                    horaInc TIME NOT NULL,
                    horaFin TIME NOT NULL
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS TipoObjeto (
                    idTipoObjeto INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(255)
                )
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Inventario(
                    idInventario INT AUTO_INCREMENT PRIMARY KEY
                )
            """);
//            stmt.execute("""
//            DROP TABLE ObjetoInventario;
//            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS ObjetoInventario (
                    idInventario INT,
                    idTipoObjeto INT,
                    PRIMARY KEY (idInventario, idTipoObjeto),
                    FOREIGN KEY (idInventario) REFERENCES Inventario(idInventario),
                    FOREIGN KEY (idTipoObjeto) REFERENCES TipoObjeto(idTipoObjeto)
                )
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS InventarioHorario (
                    PRIMARY KEY (idInventario, idHorario),
                    idInventario INT,
                    FOREIGN KEY (idInventario) REFERENCES Inventario(idInventario),
                    idHorario INT,
                    FOREIGN KEY (idHorario) REFERENCES Horario(idHorario)
                )
            """);


            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Concierto (
                    idConcierto INT AUTO_INCREMENT PRIMARY KEY,
                    nombreConcierto VARCHAR(255) NOT NULL,
                    idHorario INT,
                    aforo INT NOT NULL,
                    idContrato INT,
                    programado BOOLEAN NOT NULL,
                    idAnalisisF INT,
                    FOREIGN KEY (idHorario) REFERENCES Horario(idHorario),
                    FOREIGN KEY (idContrato) REFERENCES Contrato(idContrato),
                    FOREIGN KEY (idAnalisisF) REFERENCES AnalisisFinanciero(idAnalisisF)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS HorarioUsuario (
                    idUsuario INT,
                    idHorario INT,
                    PRIMARY KEY (idUsuario, idHorario),
                    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario),
                    FOREIGN KEY (idHorario) REFERENCES Horario(idHorario)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS ConciertoInventario (
                    idInventario INT,
                    idConcierto INT,
                    PRIMARY KEY (idInventario, idConcierto),
                    FOREIGN KEY (idInventario) REFERENCES Inventario(idInventario),
                    FOREIGN KEY (idConcierto) REFERENCES Concierto(idConcierto)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS RolConciertoUsuario (
                    idRol INT,
                    idUsuario INT,
                    idConcierto INT,
                    subrol VARCHAR(100),
                    PRIMARY KEY (idRol, idUsuario, idConcierto),
                    FOREIGN KEY (idRol) REFERENCES Rol(idRol),
                    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario),
                    FOREIGN KEY (idConcierto) REFERENCES Concierto(idConcierto)
                )
            """);

            // Por si la base ya existía sin la columna subrol
            stmt.execute("""
                ALTER TABLE RolConciertoUsuario
                ADD COLUMN IF NOT EXISTS subrol VARCHAR(100)
            """);

            //Crear roles con el idRol para eso toca mergear tablas para colocar los roles dentro de rol con los ids.
            //con merge into se evitan duplicados cada vez que se ejecute el programa.
            stmt.execute("""
               MERGE INTO Rol (idRol, rol) KEY(idRol)   
                  VALUES (1, 'Administrador'), (2, 'Tecnico'), (3, 'Artista'), (4, 'Staff')
                """);

            org.h2.tools.Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();
            System.out.println("Base de datos inicializada correctamente");

            /*
            PARA QUE HAGAN EL INGRESO BASE DE DATOS H2:
            JDBC URL: jdbc:h2:./data/eventosdb
            User: sa
            Password: vacío
           */
// PARA BORRAR LA BASE DE DATOS
//            stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
//
//            stmt.execute("DROP ALL OBJECTS");
//
//            stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
//
//            System.out.println("Base de datos limpiada");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}