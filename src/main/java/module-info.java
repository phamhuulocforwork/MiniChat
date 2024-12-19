module com.example.minichat {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens minichat to javafx.fxml;
    exports minichat;
}