package upce.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal; // Import BigDecimal
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentViewController {

    @FXML private TextField fromAccountField;
    @FXML private TextField toAccountField;
    @FXML private TextField amountField;
    @FXML private TextArea messageToField; // For recipient
    @FXML private TextArea messageFromField; // For sender
    @FXML private Label errorLabel;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    private int fromAccountId;
    private Runnable onPaymentSuccess; // Callback for success

    @FXML
    public void initialize() {
        cancelButton.setOnAction(e -> closeWindow());
        confirmButton.setOnAction(e -> processPayment());
    }

    public void setFromAccount(int accountId, String accountNumber) {
        this.fromAccountId = accountId;
        this.fromAccountField.setText(accountNumber);
    }

    public void setOnPaymentSuccess(Runnable onPaymentSuccess) {
        this.onPaymentSuccess = onPaymentSuccess;
    }

    private void processPayment() {
        String toAccountNum = toAccountField.getText();
        String amountStr = amountField.getText();
        // Ensure we don't pass null
        String msgRecipient = messageToField.getText() == null ? " " : messageToField.getText();
        String msgSender = messageFromField.getText() == null ? " " : messageFromField.getText();

        if (toAccountNum == null || toAccountNum.trim().isEmpty() ||
                amountStr == null || amountStr.trim().isEmpty()) {
            showError("Please fill in 'To Account' and 'Amount'.");
            return;
        }

        BigDecimal amount;
        try {
            // Use BigDecimal constructor from String for precision
            amount = new BigDecimal(amountStr);
            
            // Check if positive: amount.compareTo(BigDecimal.ZERO) > 0
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showError("Amount must be positive.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Invalid amount format.");
            return;
        }

        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            // 1. Resolve destination account ID from number
            int toAccountId = -1;
            String sqlFindAccount = "SELECT ACCOUNT_ID FROM ACCOUNT WHERE ACCOUNT_NUMBER = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlFindAccount)) {
                pstmt.setString(1, toAccountNum);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        toAccountId = rs.getInt("ACCOUNT_ID");
                    } else {
                        showError("Destination account '" + toAccountNum + "' not found.");
                        return;
                    }
                }
            }

            if (toAccountId == fromAccountId) {
                showError("Cannot transfer money to the same account.");
                return;
            }

            // 2. Call the PL/SQL procedure
            // execute_transfer(p_from_acc_id, p_to_acc_id, p_amount, p_msg_sender, p_msg_recipient)
            String sqlCall = "{call execute_transfer(?, ?, ?, ?, ?)}";
            try (CallableStatement cstmt = conn.prepareCall(sqlCall)) {
                cstmt.setInt(1, fromAccountId);
                cstmt.setInt(2, toAccountId);
                
                // Set BigDecimal parameter
                cstmt.setBigDecimal(3, amount);
                
                cstmt.setString(4, msgSender);
                cstmt.setString(5, msgRecipient);
                
                cstmt.execute();
            }

            // Success
            System.out.println("Transfer successful.");
            
            // Trigger callback to refresh parent UI
            if (onPaymentSuccess != null) {
                onPaymentSuccess.run();
            }
            
            closeWindow();

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle PL/SQL errors (like insufficient funds)
            // PL/SQL errors usually start with "ORA-20xxx: message"
            String msg = e.getMessage();
            if (msg.contains("ORA-20001")) {
                showError("Insufficient funds.");
            } else if (msg.contains("ORA-20003")) {
                showError("Transaction rejected: Negative balance not allowed.");
            } else {
                showError("Transaction failed: " + msg);
            }
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}