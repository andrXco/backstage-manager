module org.example.pruebafismsd {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens org.example.pruebafismsd to javafx.fxml;

    //todos los paquetes que hay
    exports org.example.pruebafismsd.Entity;
    exports org.example.pruebafismsd.Repository;
    exports org.example.pruebafismsd.Service;
    exports org.example.pruebafismsd.Controller;
    //fin de los paquetes que hay

    opens org.example.pruebafismsd.Controller to javafx.fxml;
    opens org.example.pruebafismsd.Entity to javafx.fxml;
}