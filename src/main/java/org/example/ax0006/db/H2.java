package org.example.ax0006.db;

import org.h2.tools.RunScript;
import org.h2.tools.Server;

import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

/*
    CREDENCIALES BASE DE DATOS H2:
    JDBC URL: jdbc:h2:./data/eventosdb
    User: sa
    Password:
*/

public class H2 {
    private static final String URL = "jdbc:h2:./data/eventosdb;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASS = "";

    public H2() {}; //Constructor

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public void inicializarDB() {
        try (Connection conn = getConnection()) {

            // 1. Leer y ejecutar el script SQL
            Reader reader = new InputStreamReader(Objects.requireNonNull(
                    this.getClass().getResourceAsStream("/SQL/schema.sql"),
                    "No se pudo encontrar el archivo schema.sql"
            ));

            RunScript.execute(conn, reader);
            System.out.println("Base de datos estructurada a partir de schema.sql");

            org.h2.tools.Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();
            System.out.println("Base de datos inicializada correctamente");

            /*
            PARA QUE HAGAN EL INGRESO BASE DE DATOS H2:
            JDBC URL: jdbc:h2:./data/eventosdb
            User: sa
            Password: vacío
           */
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}