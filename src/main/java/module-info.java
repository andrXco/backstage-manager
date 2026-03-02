module com.example.ax0006 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.ax0006 to javafx.fxml;
    exports com.example.ax0006;
}