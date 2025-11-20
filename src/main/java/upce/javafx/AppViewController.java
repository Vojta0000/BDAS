package upce.javafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class AppViewController {

    // Sidebar buttons
    @FXML
    private Button profileButton;
    @FXML
    private Button accountsButton;
    @FXML
    private Button account1Button;
    @FXML
    private Button account2Button;
    @FXML
    private Button tellerButton;
    @FXML
    private Button supportButton;

    // Accounts submenu
    @FXML
    private VBox accountsSubMenu;

    // Content sections
    @FXML
    private VBox profileSection;
    @FXML
    private VBox account1Section;
    @FXML
    private VBox account2Section;
    @FXML
    private VBox tellerSection;
    @FXML
    private VBox supportSection;

    // Profile fields
    @FXML
    private TextField nameField;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField birthNumberField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField emailField;

    // Account fields
    @FXML
    private Label account1Balance;
    @FXML
    private Label account2Balance;

    // Payment buttons
    @FXML
    private Button createPayment1Button;
    @FXML
    private Button createPayment2Button;

    // Support chat
    @FXML
    private VBox chatMessagesContainer;
    @FXML
    private TextField messageInput;
    @FXML
    private Button sendMessageButton;

    @FXML
    public void initialize() {
        // Set up navigation button handlers
        profileButton.setOnAction(event -> showSection("profile"));
        account1Button.setOnAction(event -> showSection("account1"));
        account2Button.setOnAction(event -> showSection("account2"));
        tellerButton.setOnAction(event -> showSection("teller"));
        supportButton.setOnAction(event -> showSection("support"));

        // Set up accounts toggle button
        accountsButton.setOnAction(event -> toggleAccountsMenu());

        // Set up payment buttons
        createPayment1Button.setOnAction(event -> openPaymentWindow("Account 1"));
        createPayment2Button.setOnAction(event -> openPaymentWindow("Account 2"));

        // Set up support chat send button
        sendMessageButton.setOnAction(event -> sendMessage());

        // Start with profile section visible
        showSection("profile");

        // Start with accounts menu expanded (as per your requirement)
        accountsSubMenu.setVisible(false);
        accountsSubMenu.setManaged(false);
        accountsButton.setText("► Accounts");
    }

    /**
     * Toggles the visibility of the accounts submenu
     */
    private void toggleAccountsMenu() {
        boolean isVisible = accountsSubMenu.isVisible();
        accountsSubMenu.setVisible(!isVisible);
        accountsSubMenu.setManaged(!isVisible);

        // Update button text with arrow
        if (!isVisible) {
            accountsButton.setText("▼ Accounts");
            updateButtonStyle(accountsButton, true);
        } else {
            accountsButton.setText("► Accounts");
            updateButtonStyle(accountsButton, false);
        }
    }

    /**
     * Shows the specified section and hides all others
     * @param section The section to show: "profile", "account1", "account2", "teller", or "support"
     */
    private void showSection(String section) {
        // Hide all sections
        profileSection.setVisible(false);
        account1Section.setVisible(false);
        account2Section.setVisible(false);
        tellerSection.setVisible(false);
        supportSection.setVisible(false);

        // Reset all button styles
        resetButtonStyles();

        // Show the selected section and highlight the button
        switch (section) {
            case "profile":
                profileSection.setVisible(true);
                updateButtonStyle(profileButton, true);
                break;
            case "account1":
                account1Section.setVisible(true);
                updateButtonStyle(account1Button, true);
                break;
            case "account2":
                account2Section.setVisible(true);
                updateButtonStyle(account2Button, true);
                break;
            case "teller":
                tellerSection.setVisible(true);
                updateButtonStyle(tellerButton, true);
                break;
            case "support":
                supportSection.setVisible(true);
                updateButtonStyle(supportButton, true);
                break;
        }
    }

    /**
     * Resets all navigation button styles to default
     */
    private void resetButtonStyles() {
        String defaultStyle = "-fx-background-color: #34495e; -fx-text-fill: white; -fx-alignment: center-left; -fx-padding: 10;";
        String accountSubStyle = "-fx-background-color: #5dade2; -fx-text-fill: white; -fx-alignment: center-left; -fx-padding: 8;";

        profileButton.setStyle(defaultStyle);
        tellerButton.setStyle(defaultStyle);
        supportButton.setStyle(defaultStyle);
        account1Button.setStyle(accountSubStyle);
        account2Button.setStyle(accountSubStyle);

        // Keep accounts button style if it's expanded
        if (accountsSubMenu.isVisible()) {
            accountsButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-alignment: center-left; -fx-padding: 10;");
        } else {
            accountsButton.setStyle(defaultStyle);
        }
    }

    /**
     * Updates the style of a button to highlight or unhighlight it
     * @param button The button to update
     * @param active Whether the button should be highlighted
     */
    private void updateButtonStyle(Button button, boolean active) {
        if (active) {
            if (button == account1Button || button == account2Button) {
                button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-alignment: center-left; -fx-padding: 8; -fx-font-weight: bold;");
            } else {
                button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-alignment: center-left; -fx-padding: 10; -fx-font-weight: bold;");
            }
        }
    }

    /**
     * Opens the payment window
     * @param accountName The name of the account from which the payment is being made
     */
    private void openPaymentWindow(String accountName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("payment-view.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the account name if needed
            // PaymentViewController controller = loader.getController();
            // controller.setAccountName(accountName);

            Stage stage = new Stage();
            stage.setTitle("Create Payment - " + accountName);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Makes it a modal window
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open payment window.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Sends a message in the support chat
     */
    private void sendMessage() {
        String message = messageInput.getText().trim();

        if (message.isEmpty()) {
            return;
        }

        // Here you would add the message to the chat
        // For now, just clear the input
        messageInput.clear();

        // TODO: Add message to chatMessagesContainer
        // You can create message UI elements dynamically here
    }

    /**
     * Shows an alert dialog
     * @param title The title of the alert
     * @param content The content text
     * @param type The type of alert
     */
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Public methods for updating data from database

    /**
     * Updates the profile information
     */
    public void updateProfileInfo(String name, String surname, String birthNumber, String phone, String email) {
        nameField.setText(name);
        surnameField.setText(surname);
        birthNumberField.setText(birthNumber);
        phoneField.setText(phone);
        emailField.setText(email);
    }

    /**
     * Updates account 1 balance
     */
    public void updateAccount1Balance(String balance) {
        account1Balance.setText(balance);
    }

    /**
     * Updates account 2 balance
     */
    public void updateAccount2Balance(String balance) {
        account2Balance.setText(balance);
    }
}