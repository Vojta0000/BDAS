package upce.javafx;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard; // Import needed
import javafx.scene.input.TransferMode; // Import needed
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane; // Import needed
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.Desktop; // Import needed
import java.io.File; // Import needed
import java.io.FileInputStream; // Import needed
import java.io.FileOutputStream; // Import needed
import java.io.IOException;
import java.io.InputStream; // Import needed
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ClientViewController {

    // Sidebar buttons
    // Sidebar buttons
    @FXML private Button profileButton;
    @FXML private Button accountsButton;
    @FXML private Button tellerButton;
    @FXML private Button supportButton;
    @FXML private Button documentsButton; // New button
    @FXML private Button logoutButton;
    @FXML private Button stopEmulateButton;

    // Reference to the main controller to trigger screen switching
    private AppViewController appViewController;

    /**
     * Sets the application view controller reference.
     * @param appViewController The parent application view controller.
     */
    public void setAppViewController(AppViewController appViewController) {
        this.appViewController = appViewController;
    }

    /**
     * Sets whether the controller is in emulation mode.
     * @param emulating True if in emulation mode.
     */
    public void setEmulationMode(boolean emulating) {
        if (stopEmulateButton != null) {
            stopEmulateButton.setVisible(emulating);
            stopEmulateButton.setManaged(emulating);
        }
    }

    /**
     * Handles the stop emulation action.
     */
    @FXML
    private void onStopEmulate() {
        if (appViewController != null) {
            appViewController.stopEmulation();
        }
    }

    // Accounts submenu
    @FXML
    private VBox accountsSubMenu;

    // Content sections
    @FXML private VBox profileSection;
    @FXML private VBox accountDetailSection;
    @FXML private VBox tellerSectionTop;
    @FXML private GridPane tellerSection;
    @FXML private VBox supportSection;
    @FXML private VBox documentsSection; // New section

    // Documents UI
    @FXML private StackPane fileDropArea;
    @FXML private ListView<HBox> documentsListView;

    // Profile fields
    @FXML private TextField nameField;
    @FXML private TextField surnameField;
    @FXML private TextField birthNumberField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;

    // Account fields
    @FXML private Label accountBalance;
    @FXML private Label accountNumberLabel;
    // Payment buttons
    @FXML private Button createPaymentButton;
    
    // History button
    private Button historyButton;
    private int currentAccountId = -1;

    // Support chat
    @FXML private VBox chatMessagesContainer;
    @FXML private TextField messageInput;
    @FXML private Button sendMessageButton;

    /**
     * Initializes the controller, sets up UI components and event handlers.
     */
    @FXML
    public void initialize() {
        // Initialize dynamic history button
        historyButton = new Button("Show Transaction History");
        historyButton.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        historyButton.setOnAction(event -> {
            if (currentAccountId != -1) {
                openAccountHistoryWindow(currentAccountId);
            }
        });
        
        // Add the history button to the UI if it's not already there
        if (accountDetailSection != null) {
            // Based on FXML structure: VBox -> [Label, Label, HBox(Button), Separator, Label]
            // We want to find the HBox that contains the "createPaymentButton" and add this new button there.
            for (Node node : ((VBox) accountDetailSection.getChildren().get(1)).getChildren()) {
                if (node instanceof HBox) {
                    ((HBox) node).getChildren().add(historyButton);
                    break;
                }
            }
        }

        // Set up navigation button handlers
        profileButton.setOnAction(event -> showSection("profile"));
        tellerButton.setOnAction(event -> showSection("teller"));
        supportButton.setOnAction(event -> showSection("support"));

        if (documentsButton != null) {
            documentsButton.setOnAction(event -> showSection("documents"));
        }

        if (logoutButton != null) {
            logoutButton.setOnAction(event -> onLogout());
        }

        // Set up accounts toggle button
        accountsButton.setOnAction(event -> toggleAccountsMenu());

        // Set up payment buttons
        if (createPaymentButton != null) {
            createPaymentButton.setOnAction(event -> {
                if (currentAccountId != -1) {
                    openPaymentWindow(currentAccountId, accountNumberLabel.getText());
                } else {
                    showAlert("Error", "No account selected.", Alert.AlertType.ERROR);
                }
            });
        }
        // Set up support chat send button
        sendMessageButton.setOnAction(event -> sendMessage());

        // Start with profile section visible
        showSection("profile");

        // Start with accounts menu expanded (as per your requirement)
        accountsSubMenu.setVisible(false);
        accountsSubMenu.setManaged(false);
        accountsButton.setText("► Accounts");

        // Initialize Documents Drop Area
        if (fileDropArea != null) {
            setupFileDragAndDrop();
        }
    }

    /**
     * Sets up drag and drop functionality for document uploads.
     */
    private void setupFileDragAndDrop() {
        fileDropArea.setOnDragOver(event -> {
            if (event.getGestureSource() != fileDropArea && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        fileDropArea.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                for (File file : db.getFiles()) {
                    saveFileToDatabase(file);
                }
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    /**
     * Saves a file to the database.
     * @param file The file to save.
     */
    private void saveFileToDatabase(File file) {
        int userId = HelloApplication.userId;
        String name = file.getName();
        String extension = "";
        int i = name.lastIndexOf('.');
        if (i > 0) {
            extension = name.substring(i + 1);
        }

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             FileInputStream fis = new FileInputStream(file)) {

            String sql = "INSERT INTO Document (Document_id, File_name, File_extension, File_data, User_id) VALUES (DOCUMENT_SEQ.NEXTVAL, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, extension);
                pstmt.setBinaryStream(3, fis, (int) file.length());
                pstmt.setInt(4, userId);

                pstmt.executeUpdate();
                System.out.println("File uploaded: " + name);
                loadDocuments(); // Refresh list
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to upload file: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Loads documents for the current user from the database.
     */
    private void loadDocuments() {
        if (documentsListView == null) return;
        documentsListView.getItems().clear();
        int userId = HelloApplication.userId;

        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            String sql = "SELECT Document_id, File_name FROM Document WHERE User_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        int docId = rs.getInt("Document_id");
                        String fileName = rs.getString("File_name");

                        HBox row = new HBox(10);
                        row.setAlignment(Pos.CENTER_LEFT);

                        Label nameLabel = new Label(fileName);
                        nameLabel.setStyle("-fx-font-size: 14px;");
                        HBox.setHgrow(nameLabel, javafx.scene.layout.Priority.ALWAYS);
                        nameLabel.setMaxWidth(Double.MAX_VALUE);

                        Button openBtn = new Button("Open");
                        openBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                        openBtn.setOnAction(e -> openDocument(docId, fileName));

                        row.getChildren().addAll(nameLabel, openBtn);
                        documentsListView.getItems().add(row);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens a document from the database and allows the user to save it locally.
     * @param docId The ID of the document.
     * @param fileName The name of the file.
     */
    private void openDocument(int docId, String fileName) {
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            String sql = "SELECT File_data FROM Document WHERE Document_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, docId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        InputStream is = rs.getBinaryStream("File_data");

                        // Create temp file
                        String extension = "";
                        int i = fileName.lastIndexOf('.');
                        if (i > 0) {
                            extension = fileName.substring(i); // includes dot
                        } else {
                            extension = ".dat"; // fallback
                        }

                        File tempFile = File.createTempFile("bankis_doc_", extension);
                        tempFile.deleteOnExit(); // Clean up on exit

                        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = is.read(buffer)) != -1) {
                                fos.write(buffer, 0, bytesRead);
                            }
                        }

                        // Open file
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().open(tempFile);
                        } else {
                            showAlert("Info", "File saved to: " + tempFile.getAbsolutePath(), Alert.AlertType.INFORMATION);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open file.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Clears all account buttons from the side menu.
     */
    public void clearAccountButtons() {
        accountsSubMenu.getChildren().clear();
    }

    /**
     * Adds a dynamic account button to the side menu.
     * @param accountName The name (number) of the account.
     * @param accountId The ID of the account.
     */
    public void addAccountButton(String accountName, int accountId) {
        Button btn = new Button(accountName);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: #5dade2; -fx-text-fill: white; -fx-alignment: center-left; -fx-padding: 8;");

        btn.setOnAction(e -> {
            // Logic to show account details.
            System.out.println("Clicked account: " + accountId);
            currentAccountId = accountId; // Store the ID so the history button knows which one to open
            showSection("account_generic"); // You will need to make this dynamic later to load real data

            loadAccountData(accountId);

            // Update styles manually since we don't have fields for these dynamic buttons
            resetButtonStyles();
            btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-alignment: center-left; -fx-padding: 8; -fx-font-weight: bold;");
        });

        accountsSubMenu.getChildren().add(btn);
    }

    /**
     * Loads details for a specific account from the database.
     * @param accountId The ID of the account.
     */
    private void loadAccountData(int accountId) {
        String sql = "SELECT * FROM ACCOUNT WHERE ACCOUNT_ID = ?";
        try(Connection conn = ConnectionSingleton.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    accountBalance.setText(rs.getString("ACCOUNT_BALANCE") + " CZK");
                    accountNumberLabel.setText(rs.getString("ACCOUNT_NUMBER"));
                }
            }
        }catch(SQLException ex) {
            System.out.println("SQL error loading account data");
            ex.printStackTrace();
        }
    }

    /**
     * Toggles the visibility of the accounts sub-menu.
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
     * Shows the specified section and hides all others.
     * @param section The section to show: "profile", "account_generic", "teller", "support", or "documents".
     */
    public void showSection(String section) {
        // Hide all sections
        profileSection.setVisible(false);
        if (accountDetailSection != null) accountDetailSection.setVisible(false);
        if (tellerSectionTop != null) tellerSectionTop.setVisible(false);
        supportSection.setVisible(false);
        if (documentsSection != null) documentsSection.setVisible(false); // New section handling

        // Reset all button styles
        resetButtonStyles();

        // Show the selected section and highlight the button
        switch (section) {
            case "profile":
                profileSection.setVisible(true);
                updateButtonStyle(profileButton, true);
                break;
            case "account_generic":
                if (accountDetailSection != null) accountDetailSection.setVisible(true);
                // Style is handled in the button click itself for dynamic buttons
                break;
            case "teller":
                tellerSectionTop.setVisible(true);
                updateButtonStyle(tellerButton, true);
                break;
            case "support":
                supportSection.setVisible(true);
                updateButtonStyle(supportButton, true);
                loadSupportMessages();
                break;
            case "documents":
                if (documentsSection != null) {
                    documentsSection.setVisible(true);
                    updateButtonStyle(documentsButton, true);
                    loadDocuments();
                }
                break;
        }
    }

    /**
     * Opens a new window displaying the transaction history for a specific account.
     * @param accountId The ID of the account.
     */
    private void openAccountHistoryWindow(int accountId) {
        Stage stage = new Stage();
        stage.setTitle("Transaction History");
        stage.setWidth(700);
        stage.setHeight(500);

        TableView<TransactionHistoryRow> table = new TableView<>();
        
        TableColumn<TransactionHistoryRow, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> data.getValue().dateProperty());
        dateCol.setPrefWidth(140);

        TableColumn<TransactionHistoryRow, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data -> data.getValue().typeProperty());
        typeCol.setPrefWidth(200);
        
        TableColumn<TransactionHistoryRow, String> descCol = new TableColumn<>("From/To");
        descCol.setCellValueFactory(data -> data.getValue().descriptionProperty());
        descCol.setPrefWidth(225);
        
        TableColumn<TransactionHistoryRow, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(data -> data.getValue().amountProperty());
        amountCol.setPrefWidth(100);

        table.getColumns().addAll(dateCol, typeCol, descCol, amountCol);

        BorderPane layout = new BorderPane();
        layout.setCenter(table);
        layout.setPadding(new Insets(10));

        Label statusLabel = new Label("Loading history...");
        layout.setBottom(statusLabel);

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.show();

        // Fetch data in background thread
        Task<List<TransactionHistoryRow>> task = new Task<>() {
            @Override
            protected List<TransactionHistoryRow> call() throws Exception {
                List<TransactionHistoryRow> rows = new ArrayList<>();
                // Query to get transactions for the specific account ID
                // Replaces IDs with Account Numbers
                String sql = """
                    SELECT
                        t.Transaction_time,
                        tt.Transaction_type_name,
                        t.Transfer_amount,
                        CASE
                            WHEN t.Account_from_id = a.Account_id THEN COALESCE(act_to.Account_number, 'External/Bank')
                            ELSE COALESCE(act_from.Account_number, 'External/Bank')
                        END as counterparty_account,
                        CASE
                            WHEN t.Account_from_id = a.Account_id THEN 'Outgoing'
                            ELSE 'Incoming'
                        END as direction,
                        CASE
                            WHEN t.Account_from_id = a.Account_id THEN -t.Transfer_amount
                            ELSE t.Transfer_amount
                        END as signed_amount
                    FROM Account a
                    JOIN Transaction t ON t.Account_from_id = a.Account_id OR t.Account_to_id = a.Account_id
                    JOIN Transaction_type tt ON t.Transaction_type_id = tt.Transaction_type_id
                    LEFT JOIN Account act_to ON t.Account_to_id = act_to.Account_id
                    LEFT JOIN Account act_from ON t.Account_from_id = act_from.Account_id
                    WHERE a.Account_id = ?
                    ORDER BY t.Transaction_time
                """;

                try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, accountId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            String date = rs.getTimestamp("Transaction_time").toLocalDateTime()
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                            
                            String typeName = rs.getString("Transaction_type_name");
                            String direction = rs.getString("direction"); // Incoming/Outgoing
                            
                            // Combine for better readability e.g., "TRANSFER (Outgoing)"
                            String typeDisplay = typeName + " (" + direction + ")";

                            String counterparty = rs.getString("counterparty_account");
                            
                            // Use getBigDecimal
                            BigDecimal amountVal = rs.getBigDecimal("signed_amount");
                            
                            // Compare using compareTo and abs()
                            String amount = (amountVal.compareTo(BigDecimal.ZERO) > 0 ? "+ " : "- ") + amountVal.abs() + " CZK";
                            
                            rows.add(new TransactionHistoryRow(date, typeDisplay, counterparty, amount));
                        }
                    }
                }
                return rows;
            }
        };

        task.setOnSucceeded(e -> {
            table.setItems(FXCollections.observableArrayList(task.getValue()));
            statusLabel.setText("History loaded.");
        });

        task.setOnFailed(e -> {
            statusLabel.setText("Error loading history.");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    // Simple inner class for the history table
    /**
     * Inner class representing a row in the transaction history table.
     */
    public static class TransactionHistoryRow {
        private final SimpleStringProperty date;
        private final SimpleStringProperty type;
        private final SimpleStringProperty description;
        private final SimpleStringProperty amount;

        /**
         * Constructor for TransactionHistoryRow.
         * @param date Date of the transaction.
         * @param type Type of the transaction.
         * @param description Description or counterparty account.
         * @param amount Amount of the transaction.
         */
        public TransactionHistoryRow(String date, String type, String description, String amount) {
            this.date = new SimpleStringProperty(date);
            this.type = new SimpleStringProperty(type);
            this.description = new SimpleStringProperty(description);
            this.amount = new SimpleStringProperty(amount);
        }

        /**
         * @return Date property.
         */
        public SimpleStringProperty dateProperty() { return date; }
        /**
         * @return Type property.
         */
        public SimpleStringProperty typeProperty() { return type; }
        /**
         * @return Description property.
         */
        public SimpleStringProperty descriptionProperty() { return description; }
        /**
         * @return Amount property.
         */
        public SimpleStringProperty amountProperty() { return amount; }
    }

    /**
     * Sends a message in the support chat and saves it to the database.
     */
    private void sendMessage() {
        String message = messageInput.getText().trim();

        if (message.isEmpty()) {
            return;
        }

        loadSupportMessages();

        int userId = HelloApplication.userId;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            // 1. Get Teller ID
            int tellerId = -1;
            String sqlTeller = "SELECT Teller_id FROM Client WHERE User_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlTeller)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        tellerId = rs.getInt("Teller_id");
                    }
                }
            }

            if (tellerId != -1) {
                String sqlInsert = "INSERT INTO Message (Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id) VALUES (MESSAGE_SEQ.NEXTVAL, ?, 'N', ?, SYSDATE, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                    pstmt.setString(1, message);
                    pstmt.setInt(2, userId);
                    pstmt.setInt(3, tellerId);
                    pstmt.executeUpdate();
                }

                messageInput.clear();
                loadSupportMessages();
            } else {
                showAlert("Error", "No teller assigned to this client.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to send message.", Alert.AlertType.ERROR);
        }
    }

    private void loadSupportMessages() {

        chatMessagesContainer.getChildren().clear();
        int userId = HelloApplication.userId;

        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            int tellerId = -1;
            String sqlTeller = "SELECT Teller_id FROM Client WHERE User_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlTeller)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        tellerId = rs.getInt("Teller_id");
                    }
                }
            }

            if (tellerId == -1) return;

            String updateSql = "UPDATE Message SET Message_read = 'Y' WHERE User_from_id = ? AND User_to_id = ? AND Message_read = 'N'";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setInt(1, tellerId);
                pstmt.setInt(2, userId);
                pstmt.executeUpdate();
            }

            String sqlMessages = "SELECT * FROM Message WHERE (User_from_id = ? AND User_to_id = ?) OR (User_from_id = ? AND User_to_id = ?) ORDER BY Message_sent_at ASC";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlMessages)) {
                pstmt.setInt(1, userId);
                pstmt.setInt(2, tellerId);
                pstmt.setInt(3, tellerId);
                pstmt.setInt(4, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while(rs.next()) {
                        String text = rs.getString("Message_text");
                        int fromId = rs.getInt("User_from_id");
                        boolean isRead = "Y".equals(rs.getString("Message_read"));
                        java.sql.Timestamp sentAt = rs.getTimestamp("Message_sent_at");
                        String timeStr = sentAt.toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                        addMessageBubble(text, fromId == userId, isRead, timeStr);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addMessageBubble(String text, boolean isOutgoing, boolean isRead, String timeString) {
        HBox container = new HBox();
        container.setFillHeight(true);
        container.setAlignment(isOutgoing ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        container.setPadding(new Insets(5));

        VBox bubble = new VBox(5);
        bubble.setMaxWidth(300);
        bubble.setPadding(new Insets(10));
        bubble.setStyle(isOutgoing
                ? "-fx-background-color: #d4edda; -fx-background-radius: 10;"
                : "-fx-background-color: #e8f4f8; -fx-background-radius: 10;");

        Label messageLabel = new Label(text);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 14px;");

        Label timeLabel = new Label(timeString);
        timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #95a5a6;");

        bubble.getChildren().addAll(messageLabel, timeLabel);

        if (isOutgoing) {
            Label readStatus = new Label("✔"); // Checkmark
            if (isRead) {
                readStatus.setStyle("-fx-text-fill: green; -fx-font-size: 12px;");
            } else {
                readStatus.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");
            }
            HBox metaContainer = new HBox(readStatus);
            metaContainer.setAlignment(Pos.BOTTOM_RIGHT);
            bubble.getChildren().add(metaContainer);
        }

        container.getChildren().add(bubble);
        chatMessagesContainer.getChildren().add(container);
    }

    /**
     * Resets the styles of all navigation buttons to their default state.
     */
    private void resetButtonStyles() {
        String defaultStyle = "-fx-background-color: #34495e; -fx-text-fill: white; -fx-alignment: center-left; -fx-padding: 10;";
        String accountSubStyle = "-fx-background-color: #5dade2; -fx-text-fill: white; -fx-alignment: center-left; -fx-padding: 8;";

        profileButton.setStyle(defaultStyle);
        tellerButton.setStyle(defaultStyle);
        supportButton.setStyle(defaultStyle);

        if (documentsButton != null) documentsButton.setStyle(defaultStyle);

        // Keep accounts button style if it's expanded
        if (accountsSubMenu.isVisible()) {
            accountsButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-alignment: center-left; -fx-padding: 10;");
        } else {
            accountsButton.setStyle(defaultStyle);
        }
    }

    /**
     * Updates the style of a specific button based on whether it's active.
     * @param button The button to update.
     * @param active True if the button should be styled as active.
     */
    private void updateButtonStyle(Button button, boolean active) {
        if (active) {
//            if (button == account1Button || button == account2Button) {
//                button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-alignment: center-left; -fx-padding: 8; -fx-font-weight: bold;");
//            } else {
                button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-alignment: center-left; -fx-padding: 10; -fx-font-weight: bold;");
//            }
        }
    }

    /**
     * Opens the payment window for a specific account.
     * @param accountId The ID of the source account.
     * @param accountNumber The number of the source account.
     */
    private void openPaymentWindow(int accountId, String accountNumber) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("payment-view.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the account info
            PaymentViewController controller = loader.getController();
            controller.setFromAccount(accountId, accountNumber);
            
            // Set callback to refresh balance on success
            controller.setOnPaymentSuccess(() -> {
                // Reload account data
                loadAccountData(accountId);
            });

            // Get the controller and pass the account name if needed
            Stage stage = new Stage();
            stage.setTitle("Create Payment");
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
     * Handles the logout process.
     */
    private void onLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Are you sure you want to log out?");
        alert.setContentText("You will be returned to the login screen.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (appViewController != null) {
                appViewController.logout();
            } else {
                // Fallback if controller isn't linked (e.g. during isolated testing)
                System.out.println("Logout requested, but AppViewController is not linked.");
            }
        }
    }

    /**
     * Shows an alert dialog to the user.
     * @param title The title of the alert.
     * @param content The content text of the alert.
     * @param type The type of the alert.
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
     * Updates the profile information in the UI.
     * @param name Name of the client.
     * @param surname Surname of the client.
     * @param birthNumber Birth number of the client.
     * @param phone Phone number of the client.
     * @param email Email address of the client.
     */
    public void updateProfileInfo(String name, String surname, String birthNumber, String phone, String email) {
        nameField.setText(name);
        surnameField.setText(surname);
        birthNumberField.setText(birthNumber);
        phoneField.setText(phone);
        emailField.setText(email);
    }

    /**
     * Updates the teller information section in the UI.
     * @param name Name of the teller.
     * @param surname Surname of the teller.
     * @param phone Phone number of the teller.
     * @param email Email address of the teller.
     * @param branch Branch where the teller is located.
     */
    public void updateTellerSection(String name, String surname, String phone, String email, String branch) {
        ((Label) tellerSection.getChildren().get(1)).setText(name + " " + surname);
        ((Label) tellerSection.getChildren().get(3)).setText(phone);
        ((Label) tellerSection.getChildren().get(5)).setText(email);
        ((Label) tellerSection.getChildren().get(7)).setText(branch);
    }
}