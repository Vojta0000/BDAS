module upce.javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens upce.javafx to javafx.fxml;
    exports upce.javafx;
}