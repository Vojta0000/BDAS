package upce.javafx;

import javafx.application.Application;
import upce.javafx.utils.ScriptRunner;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Entry point for the application to bypass JavaFX module system restrictions.
 */
public class Launcher {
    /**
     * Set this to true to run the database setup scripts and then exit.
     */
    public static final boolean IS_SETUP_MODE = false;

    /**
     * Main method to launch the application.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        if (IS_SETUP_MODE) {
            runSetup();
            return;
        }

        for (String arg : args) {
            if ("--setup".equalsIgnoreCase(arg)) {
                runSetup();
                return;
            }
        }

        Application.launch(HelloApplication.class, args);
    }

    private static void runSetup() {
        System.out.println("Starting application in setup mode...");
        ScriptRunner runner = new ScriptRunner();
        try {
            System.out.println("Executing destroyScript.sql...");
            runner.runScript("src/main/SQLScripts/destroyScript.sql");
            
            System.out.println("Executing CreateScript.sql...");
            runner.runScript("src/main/SQLScripts/CreateScript.sql");

            System.out.println("Executing RealInsertScript.sql...");
            runner.runScript("src/main/SQLScripts/RealInsertScript.sql");

            System.out.println("Executing PlSql.sql...");
            runner.runScript("src/main/SQLScripts/PlSql.sql");

            System.out.println("Executing Views.sql...");
            runner.runScript("src/main/SQLScripts/Views.sql");

            System.out.println("Setup completed successfully.");
        } catch (Exception e) {
            System.err.println("Setup failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }
}
