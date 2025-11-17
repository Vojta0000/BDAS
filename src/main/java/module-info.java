module upce.javafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens upce.javafx to javafx.fxml;
    exports upce.javafx;
}