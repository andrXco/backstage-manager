module org.example.gestorconciertos {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires com.h2database;
    requires jbcrypt;

    opens org.example.ax0006 to javafx.fxml;

    exports org.example.ax0006.entity;
    exports org.example.ax0006.repository;
    exports org.example.ax0006.service;
    exports org.example.ax0006.controller;
    exports org.example.ax0006.db;

    opens org.example.ax0006.controller to javafx.fxml;
    opens org.example.ax0006.entity to javafx.fxml;
}