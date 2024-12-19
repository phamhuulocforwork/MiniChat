module com.example.minichat {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens com.example.minichat to javafx.fxml;
    exports com.example.minichat;
}