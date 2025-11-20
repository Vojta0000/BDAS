package upce.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PaymentViewController {

    @FXML
    private TextField amountField;

    @FXML
    private TextField recipientAccountField;

    @FXML
    private TextArea messageSenderField;

    @FXML
    private TextArea messageRecipientField;

    @FXML
    private Button confirmButton;

    @FXML
    private Button cancelButton;

    @FXML
    public void initialize() {
        // Set up button handlers
        confirmButton.setOnAction(event -> handleConfirm());
        cancelButton.setOnAction(event -> handleCancel());
    }

    /**
     * Handles the confirm button click
     */
    private void handleConfirm() {
        System.out.println("=== PAYMENT CONFIRMATION ===");
        System.out.println("Amount: " + amountField.getText());
        System.out.println("Recipient Account: " + recipientAccountField.getText());
        System.out.println("Message for Sender: " + messageSenderField.getText());
        System.out.println("Message for Recipient: " + messageRecipientField.getText());
        System.out.println("============================");

        // Close the window
        closeWindow();
    }

    /**
     * Handles the cancel button click
     */
    private void handleCancel() {
        System.out.println("Payment cancelled");
        closeWindow();
    }

    /**
     * Closes the payment window
     */
    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}