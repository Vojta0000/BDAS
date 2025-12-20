package upce.javafx;

import javafx.application.Application;

/**
 * Entry point for the application to bypass JavaFX module system restrictions.
 */
public class Launcher {
    /**
     * Main method to launch the application.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        Application.launch(HelloApplication.class, args);
    }
}
