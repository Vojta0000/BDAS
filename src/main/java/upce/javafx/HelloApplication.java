package upce.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import upce.javafx.scheduler.MonthlyInterestScheduler;

import java.io.IOException;

public class HelloApplication extends Application {
    public static int userId = -1;
    private MonthlyInterestScheduler interestScheduler;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("app-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
        stage.setTitle("BankIS");
        stage.setScene(scene);
        stage.show();

        // Start monthly interest scheduler (runs on the 1st day of month at 00:15)
        interestScheduler = new MonthlyInterestScheduler();
        interestScheduler.start();
    }

    @Override
    public void stop() {
        if (interestScheduler != null) {
            interestScheduler.shutdown();
        }
    }
}
