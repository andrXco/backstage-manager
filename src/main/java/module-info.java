module org.example.gestorconciertos {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires com.h2database;
    requires jbcrypt;

    opens org.example.ax0006 to javafx.fxml;

    exports org.example.ax0006.Entity;
    exports org.example.ax0006.Repository;
    exports org.example.ax0006.Service;
    exports org.example.ax0006.Controller;
    exports org.example.ax0006.db;

    opens org.example.ax0006.Controller to javafx.fxml;
    opens org.example.ax0006.Entity to javafx.fxml;
}