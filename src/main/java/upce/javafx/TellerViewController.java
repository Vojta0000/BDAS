package upce.javafx;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TellerViewController {
    public Button registerNewUserButton;
    // Sidebar buttons
    @FXML
    private Button profileButton;
    @FXML
    private Button clientsButton;
    @FXML
    private Button clientsOverviewButton;
    @FXML
    private VBox clientsSubMenu;
    @FXML
    private Button notificationsButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button stopEmulateButton;

    // Reference to the main controller to trigger screen switching
    private AppViewController appViewController;

    public void setAppViewController(AppViewController appViewController) {
        this.appViewController = appViewController;
    }

    public void setEmulationMode(boolean emulating) {
        if (stopEmulateButton != null) {
            stopEmulateButton.setVisible(emulating);
            stopEmulateButton.setManaged(emulating);
        }
    }

    @FXML
    private void onStopEmulate() {
        if (appViewController != null) {
            appViewController.stopEmulation();
        }
    }

    // Sections
    @FXML
    private VBox profileSection;
    @FXML
    private VBox clientsOverviewSection;
    @FXML
    private VBox clientDetailSection;
    @FXML
    private VBox accountDetailSection;
    @FXML
    private VBox clientChatSection;
    @FXML
    private VBox notificationsSection;
    @FXML
    private VBox registerClientSection;
    @FXML
    private VBox clientDocumentsSection;

    @FXML
    private TableView<ClientOverviewRow> clientsOverviewTable;
    @FXML
    private TableColumn<ClientOverviewRow, String> overviewNameCol;
    @FXML
    private TableColumn<ClientOverviewRow, String> overviewAccountCol;
    @FXML
    private TableColumn<ClientOverviewRow, String> overviewTotalCol;

    // Registration fields
    @FXML
    private TextField regNameField;
    @FXML
    private TextField regSurnameField;
    @FXML
    private TextField regBirthNumberField;
    @FXML
    private TextField regPhoneField;
    @FXML
    private TextField regEmailField;
    @FXML
    private PasswordField regPasswordField;
    @FXML
    private TextField regStreetField;
    @FXML
    private TextField regHouseNumberField;
    @FXML
    private TextField regCityField;
    @FXML
    private TextField regZipField;
    @FXML
    private TextField regCountryField;
    @FXML
    private Button confirmRegistrationButton;

    // Profile fields
    @FXML
    private TextField nameField;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField emailField;

    // Client details
    @FXML
    private Label clientHeader;
    @FXML
    private Label clientNameLabel;
    @FXML
    private Label clientIdLabel;
    @FXML
    private VBox clientAccountsContainer;
    @FXML
    private Button openClientChatButton;
    @FXML
    private Button openClientDocsButton;
    @FXML
    private Button createAccountButton;

    // If present in FXML as examples
    @FXML
    private Button sampleAccountBtn1;
    @FXML
    private Button sampleAccountBtn2;

    // Account details
    @FXML
    private Label accountHeader;
    @FXML
    private Label accountNumberLabel;
    @FXML
    private Label accountBalanceLabel;
    @FXML
    private Label accountCurrencyLabel;
    @FXML
    private TableView<TransactionRow> transactionsTable;
    @FXML
    private TableColumn<TransactionRow, String> dateColumn;
    @FXML
    private TableColumn<TransactionRow, String> descriptionColumn;
    @FXML
    private TableColumn<TransactionRow, String> amountColumn;
    @FXML
    private Button backToClientButton;
    @FXML
    private Button historyButton;

    // Documents
    @FXML
    private Label clientDocsHeader;
    @FXML
    private StackPane fileDropArea;
    @FXML
    private ListView<HBox> documentsListView;
    @FXML
    private Button backToClientFromDocsButton;

    // Client chat
    @FXML
    private Label clientChatHeader;
    @FXML
    private VBox clientChatMessagesContainer;
    @FXML
    private TextField clientMessageInput;
    @FXML
    private Button sendClientMessageButton;
    @FXML
    private Button backToClientFromChatButton;

    // Notifications (read-only feed container)
    @FXML
    private VBox notificationsMessagesContainer;

    // State
    private String currentClientName;
    private String currentClientId;
    private String currentAccountNumber;
    private int currentAccountIdForHistory;

    private boolean showSent = false;

    @FXML
    public void initialize() {
        // Sidebar navigation
        profileButton.setOnAction(e -> showSection("profile"));
        clientsOverviewButton.setOnAction(e -> showSection("overview"));
        notificationsButton.setOnAction(e -> showSection("notifications"));

        if (logoutButton != null) {
            logoutButton.setOnAction(e -> onLogout());
        }

        // Wire up new registration button
        if (registerNewUserButton != null) {
            registerNewUserButton.setOnAction(e -> showSection("register"));
        }

        // Wire up confirm button
        if (confirmRegistrationButton != null) {
            confirmRegistrationButton.setOnAction(e -> registerClient());
        }

        // Pending requests button
        if (pendingRequestsButton != null) {
            pendingRequestsButton.setOnAction(e -> showSection("pending"));
        }
        // Prepare pending table columns if present
        setupPendingTable();

        // Clients submenu toggle
        clientsButton.setOnAction(e -> toggleClientsMenu());

        // Client detail actions
        openClientChatButton.setOnAction(e -> openClientChat());
        if (openClientDocsButton != null) {
            openClientDocsButton.setOnAction(e -> openClientDocs());
        }
        if (createAccountButton != null) {
            createAccountButton.setOnAction(e -> {
                if (currentClientId == null || currentClientId.isEmpty()) {
                    showAlert("Error", "Please select a client first.");
                    return;
                }
                try {
                    int clientId = Integer.parseInt(currentClientId);
                    addNewAccountForClient(clientId);
                } catch (NumberFormatException ex) {
                    showAlert("Error", "Invalid current client context.");
                }
            });
        }

        // Account detail actions
        backToClientButton.setOnAction(e -> showSection("client"));
        backToClientButton.setOnAction(e -> showSection("client"));
        if (historyButton != null) {
            historyButton.setOnAction(e -> {
                if (currentAccountIdForHistory > 0) {
                    openAccountHistoryWindow(currentAccountIdForHistory);
                }
            });
        }

        // Documents actions
        if (backToClientFromDocsButton != null) {
            backToClientFromDocsButton.setOnAction(e -> showSection("client"));
        }

        if (fileDropArea != null) {
            setupFileDragAndDrop();
        }

        // Chat actions
        sendClientMessageButton.setOnAction(e -> sendClientMessage());
        backToClientFromChatButton.setOnAction(e -> showSection("client"));

        // Table columns setup
        setupTransactionTable();
        setupOverviewTable();

//        // Client detail actions
//        openClientChatButton.setOnAction(e -> openClientChat());
//
//        // Wire sample account buttons from FXML, if they exist
//        if (sampleAccountBtn1 != null) sampleAccountBtn1.setOnAction(e -> openAccount("123456/0100", "$5,000.00", "USD"));
//        if (sampleAccountBtn2 != null) sampleAccountBtn2.setOnAction(e -> openAccount("987654/0100", "$1,250.40", "USD"));
//
//        // Account detail actions
//        backToClientButton.setOnAction(e -> showSection("client"));
//
//        // Chat actions
//        sendClientMessageButton.setOnAction(e -> sendClientMessage());
//        backToClientFromChatButton.setOnAction(e -> showSection("client"));

        // Table columns setup
        setupTransactionTable();

        // Initial UI state
        setClientsMenuVisible(false);
        showSection("profile");

        // Optionally populate static profile demo data (can be replaced by real data wiring)
//        setProfileDemoData();
    }

    private void setupOverviewTable() {
        overviewNameCol.setCellValueFactory(d -> d.getValue().name);
        overviewAccountCol.setCellValueFactory(d -> d.getValue().accountNumber);
        overviewTotalCol.setCellValueFactory(d -> d.getValue().totalAmount);

        // Create a button to serve as the clickable header
        Button toggleBtn = new Button("Total Received");
        toggleBtn.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-cursor: hand;");

        // Remove the default text to avoid "Total Received" appearing twice
        overviewTotalCol.setText("");
        overviewTotalCol.setGraphic(toggleBtn);

        toggleBtn.setOnAction(e -> {
            showSent = !showSent;
            toggleBtn.setText(showSent ? "Total Sent" : "Total Received");
            loadClientsOverview();
        });
    }

    private void loadClientsOverview() {
        clientsOverviewTable.getItems().clear();
        int tellerId = HelloApplication.userId;
        int sentFlag = showSent ? 1 : 0;

        String sql = """
                SELECT u.NAME, u.SURNAME, a.ACCOUNT_NUMBER, a.ACCOUNT_ID
                FROM Client c
                JOIN "User" u ON c.USER_ID = u.USER_ID
                JOIN Account a ON a.CLIENT_ID = c.USER_ID
                WHERE c.TELLER_ID = ? AND u.APPROVED = 'Y'
                """;

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("NAME") + " " + rs.getString("SURNAME");
                    String accNum = rs.getString("ACCOUNT_NUMBER");
                    int accId = rs.getInt("ACCOUNT_ID");

                    // Call your PL/SQL function
                    double total = 0;
                    try (PreparedStatement fPs = conn.prepareStatement("SELECT total_amount(?, ?) FROM DUAL")) {
                        fPs.setInt(1, accId);
                        fPs.setInt(2, sentFlag);
                        try (ResultSet fRs = fPs.executeQuery()) {
                            if (fRs.next()) total = fRs.getDouble(1);
                        }
                    }

                    clientsOverviewTable.getItems().add(new ClientOverviewRow(name, accNum, total + " CZK"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class ClientOverviewRow {
        public final javafx.beans.property.SimpleStringProperty name;
        public final javafx.beans.property.SimpleStringProperty accountNumber;
        public final javafx.beans.property.SimpleStringProperty totalAmount;

        public ClientOverviewRow(String n, String acc, String total) {
            this.name = new javafx.beans.property.SimpleStringProperty(n);
            this.accountNumber = new javafx.beans.property.SimpleStringProperty(acc);
            this.totalAmount = new javafx.beans.property.SimpleStringProperty(total);
        }
    }

    private void openClientChat() {
        clientChatHeader.setText("Chat with " + (currentClientName != null ? currentClientName : "Client"));
        showSection("chat");

        // Load chat history specifically for this client
        if (currentClientId != null) {
            loadClientChatHistory(Integer.parseInt(currentClientId));
        }
    }

    private void loadClientChatHistory(int clientId) {
        clientChatMessagesContainer.getChildren().clear();
        int myId = HelloApplication.userId;

        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            // 1. Mark incoming messages from this client as read
            String updateSql = "UPDATE Message SET Message_read = 'Y' WHERE User_from_id = ? AND User_to_id = ? AND Message_read = 'N'";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setInt(1, clientId);
                pstmt.setInt(2, myId);
                pstmt.executeUpdate();
            }

            // 2. Load message history
            String sql = "SELECT * FROM Message WHERE (User_from_id = ? AND User_to_id = ?) OR (User_from_id = ? AND User_to_id = ?) ORDER BY Message_sent_at ASC";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, myId);
                pstmt.setInt(2, clientId);
                pstmt.setInt(3, clientId);
                pstmt.setInt(4, myId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String text = rs.getString("Message_text");
                        int fromId = rs.getInt("User_from_id");
                        boolean isRead = "Y".equals(rs.getString("Message_read"));

                        // If sender is me (Teller), it's outgoing. Otherwise it's incoming.
                        boolean isOutgoing = (fromId == myId);
                        String author = isOutgoing ? "You" : (currentClientName != null ? currentClientName : "Client");

                        addChatBubble(author, text, isOutgoing, isRead);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void sendClientMessage() {
        String msg = clientMessageInput.getText();
        if (msg == null || msg.trim().isEmpty()) {
            return;
        }

        if (currentClientId == null) {
            System.out.println("Error: No client selected for chat.");
            return;
        }

        int myId = HelloApplication.userId;
        int clientId = Integer.parseInt(currentClientId);

        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            String sql = "INSERT INTO Message (Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id) VALUES (MESSAGE_SEQ.NEXTVAL, ?, 'N', ?, SYSDATE, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, msg);
                pstmt.setInt(2, myId);
                pstmt.setInt(3, clientId);
                pstmt.executeUpdate();
            }

            clientMessageInput.clear();
            // Refresh chat to show new message
            loadClientChatHistory(clientId);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to send message.");
        }
    }

    public void loadMyClients() {
        clientsSubMenu.getChildren().clear();
        int tellerId = HelloApplication.userId;

        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            // Using the PL/SQL function get_teller_clients which returns a SYS_REFCURSOR
            String sql = "{ ? = call get_teller_clients(?) }";

            try (CallableStatement cstmt = conn.prepareCall(sql)) {
                // Register the out parameter as a Cursor
                cstmt.registerOutParameter(1, java.sql.Types.REF_CURSOR);
                cstmt.setInt(2, tellerId);
                cstmt.execute();

                try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                    while (rs.next()) {
                        int clientId = rs.getInt("User_id");
                        String name = rs.getString("Name") + " " + rs.getString("Surname");

                        Button clientBtn = new Button(name);
                        clientBtn.setMaxWidth(Double.MAX_VALUE);
                        clientBtn.setStyle("-fx-background-color: #5dade2; -fx-text-fill: white; -fx-alignment: center-left; -fx-padding: 8;");

                        clientBtn.setOnAction(e -> openClient(name, String.valueOf(clientId)));

                        clientsSubMenu.getChildren().add(clientBtn);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load clients using database function: " + e.getMessage());
        }
    }

    private void openClient(String name, String id) {
        currentClientName = name;
        currentClientId = id;
        clientHeader.setText("Client Details — " + name);
        clientNameLabel.setText(name);
        clientIdLabel.setText(id);

        // Populate accounts list dynamically
        populateClientAccounts(Integer.parseInt(id));

        showSection("client");
    }

// --- Account History Logic ---

    private void populateClientAccounts(int clientId) {
        clientAccountsContainer.getChildren().clear();

        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            String sql = "SELECT Account_id, Account_number, Account_balance FROM Account WHERE Client_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, clientId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        int accId = rs.getInt("Account_id"); // Get ID for history
                        String accNum = rs.getString("Account_number");
                        String balance = rs.getString("Account_balance");
                        String currency = "CZK";

                        HBox row = new HBox(10);
                        Button openBtn = new Button("Account " + accNum);
                        openBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 6 12;");

                        Label bal = new Label("Balance: " + balance + " " + currency);
                        bal.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-alignment: center;");
                        bal.setPadding(new Insets(5, 0, 0, 0));

                        openBtn.setOnAction(e -> {
                            currentAccountIdForHistory = accId; // Store ID
                            openAccount(accNum, balance, currency);
                        });

                        row.getChildren().addAll(openBtn, bal);
                        clientAccountsContainer.getChildren().add(row);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // ... Add Account Button ...
    }

    private void openAccountHistoryWindow(int accountId) {
        Stage stage = new Stage();
        stage.setTitle("Transaction History");
        stage.setWidth(700);
        stage.setHeight(500);

        TableView<ClientViewController.TransactionHistoryRow> table = new TableView<>();

        TableColumn<ClientViewController.TransactionHistoryRow, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> data.getValue().dateProperty());
        dateCol.setPrefWidth(140);

        TableColumn<ClientViewController.TransactionHistoryRow, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data -> data.getValue().typeProperty());
        typeCol.setPrefWidth(100);

        TableColumn<ClientViewController.TransactionHistoryRow, String> descCol = new TableColumn<>("From/To");
        descCol.setCellValueFactory(data -> data.getValue().descriptionProperty());
        descCol.setPrefWidth(250);

        TableColumn<ClientViewController.TransactionHistoryRow, String> amountCol = new TableColumn<>("Amount");
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

        Task<List<ClientViewController.TransactionHistoryRow>> task = new Task<>() {
            @Override
            protected List<ClientViewController.TransactionHistoryRow> call() throws Exception {
                List<ClientViewController.TransactionHistoryRow> rows = new ArrayList<>();
                // Reusing query logic from ClientViewController
                String sql = """
                            SELECT t.Transaction_time, tt.Transaction_type_name, t.Transfer_amount,
                                CASE WHEN t.Account_from_id = a.Account_id THEN COALESCE(act_to.Account_number, 'External/Bank') ELSE COALESCE(act_from.Account_number, 'External/Bank') END as counterparty_account,
                                CASE WHEN t.Account_from_id = a.Account_id THEN 'Outgoing' ELSE 'Incoming' END as direction,
                                CASE WHEN t.Account_from_id = a.Account_id THEN -t.Transfer_amount ELSE t.Transfer_amount END as signed_amount
                            FROM Account a
                            JOIN Transaction t ON t.Account_from_id = a.Account_id OR t.Account_to_id = a.Account_id
                            JOIN Transaction_type tt ON t.Transaction_type_id = tt.Transaction_type_id
                            LEFT JOIN Account act_to ON t.Account_to_id = act_to.Account_id
                            LEFT JOIN Account act_from ON t.Account_from_id = act_from.Account_id
                            WHERE a.Account_id = ?
                            ORDER BY t.Transaction_time DESC
                        """;

                try (Connection conn = ConnectionSingleton.getInstance().getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, accountId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            String date = rs.getTimestamp("Transaction_time").toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                            String typeDisplay = rs.getString("Transaction_type_name") + " (" + rs.getString("direction") + ")";
                            String counterparty = rs.getString("counterparty_account");
                            BigDecimal amountVal = rs.getBigDecimal("signed_amount");
                            String amount = (amountVal.compareTo(BigDecimal.ZERO) > 0 ? "+ " : "- ") + amountVal.abs() + " CZK";
                            rows.add(new ClientViewController.TransactionHistoryRow(date, typeDisplay, counterparty, amount));
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

    private void addNewAccountForClient(int clientId) {
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            createInitialAccount(conn, clientId);
            // Refresh list
            populateClientAccounts(clientId);
            showAlert("Success", "New account created for client.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to create account: " + e.getMessage());
        }
    }

    private void toggleClientsMenu() {
        boolean visible = clientsSubMenu.isVisible();
        setClientsMenuVisible(!visible);
    }

    private void setClientsMenuVisible(boolean visible) {
        clientsSubMenu.setVisible(visible);
        clientsSubMenu.setManaged(visible);
        clientsButton.setText((visible ? "▼" : "►") + " Clients");
    }

    public void showSection(String section) {
        // Hide all
        profileSection.setVisible(false);
        profileSection.setManaged(false);
        clientDetailSection.setVisible(false);
        clientDetailSection.setManaged(false);
        accountDetailSection.setVisible(false);
        accountDetailSection.setManaged(false);
        clientChatSection.setVisible(false);
        clientChatSection.setManaged(false);
        notificationsSection.setVisible(false);
        notificationsSection.setManaged(false);
        profileSection.setVisible(false);
        profileSection.setManaged(false);
        if (clientsOverviewSection != null) {
            clientsOverviewSection.setVisible(false);
            clientsOverviewSection.setManaged(false);
        }
        if (registerClientSection != null) {
            registerClientSection.setVisible(false);
            registerClientSection.setManaged(false);
        }
        if (clientDocumentsSection != null) {
            clientDocumentsSection.setVisible(false);
            clientDocumentsSection.setManaged(false);
        }
        if (pendingRequestsSection != null) {
            pendingRequestsSection.setVisible(false);
            pendingRequestsSection.setManaged(false);
        }

        switch (section) {
            case "profile":
                profileSection.setVisible(true);
                profileSection.setManaged(true);
                break;
            case "overview":
                clientsOverviewSection.setVisible(true);
                clientsOverviewSection.setManaged(true);
                loadClientsOverview();
                break;
            case "client":
                clientDetailSection.setVisible(true);
                clientDetailSection.setManaged(true);
                break;
            case "account":
                accountDetailSection.setVisible(true);
                accountDetailSection.setManaged(true);
                break;
            case "chat":
                clientChatSection.setVisible(true);
                clientChatSection.setManaged(true);
                break;
            case "documents":
                if (clientDocumentsSection != null) {
                    clientDocumentsSection.setVisible(true);
                    clientDocumentsSection.setManaged(true);
                    loadClientDocuments();
                }
                break;
            case "notifications":
                notificationsSection.setVisible(true);
                notificationsSection.setManaged(true);
                loadNotifications();
                break;
            case "register":
                if (registerClientSection != null) {
                    registerClientSection.setVisible(true);
                    registerClientSection.setManaged(true);
                }
                break;
            case "pending":
                if (pendingRequestsSection != null) {
                    pendingRequestsSection.setVisible(true);
                    pendingRequestsSection.setManaged(true);
                    loadPendingRequests();
                }
                break;
            default:
                profileSection.setVisible(true);
                profileSection.setManaged(true);
        }
    }

    // ----- Pending Requests -----
    @FXML
    private Button pendingRequestsButton;
    @FXML
    private VBox pendingRequestsSection;
    @FXML
    private TableView<PendingRow> pendingTable;

    @FXML
    public void initializePending() { /* kept for potential FXML hooks; logic moved into initialize() */ }

    private boolean pendingColumnsInitialized = false;

    private void setupPendingTable() {
        if (pendingTable == null || pendingColumnsInitialized) return;
        TableColumn<PendingRow, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(d -> d.getValue().name);
        nameCol.setPrefWidth(160);

        TableColumn<PendingRow, String> birthCol = new TableColumn<>("Birth #");
        birthCol.setCellValueFactory(d -> d.getValue().birthNumber);
        birthCol.setPrefWidth(140);

        TableColumn<PendingRow, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(d -> d.getValue().phone);
        phoneCol.setPrefWidth(120);

        TableColumn<PendingRow, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(d -> d.getValue().email);
        emailCol.setPrefWidth(220);

        TableColumn<PendingRow, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(d -> d.getValue().address);
        addressCol.setPrefWidth(320);

        TableColumn<PendingRow, String> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button approveBtn = new Button("Approve");
            private final Button declineBtn = new Button("Decline");

            {
                approveBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
                declineBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                approveBtn.setOnAction(e -> {
                    PendingRow row = getTableView().getItems().get(getIndex());
                    approvePending(row.userId.get());
                });
                declineBtn.setOnAction(e -> {
                    PendingRow row = getTableView().getItems().get(getIndex());
                    declinePending(row.userId.get());
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(6, approveBtn, declineBtn));
            }
        });
        actionsCol.setPrefWidth(200);

        pendingTable.getColumns().addAll(nameCol, birthCol, phoneCol, emailCol, addressCol, actionsCol);
        pendingColumnsInitialized = true;
    }

    private void loadPendingRequests() {
        setupPendingTable();
        if (pendingTable == null) return;
        javafx.collections.ObservableList<PendingRow> rows = FXCollections.observableArrayList();
        int tellerId = HelloApplication.userId;
        String sql = """
                SELECT u.USER_ID, u.NAME, u.SURNAME,
                       c.BIRTH_NUMBER, c.PHONE_NUMBER, c.EMAIL_ADDRESS,
                       a.COUNTRY, a.STATE, a.CITY, a.STREET, a.HOUSE_NUMBER, a.ZIP_CODE
                FROM "User" u
                JOIN CLIENT c ON c.USER_ID = u.USER_ID
                JOIN ADDRESS a ON a.ADDRESS_ID = u.ADDRESS_ID
                WHERE u.APPROVED = 'N' AND c.TELLER_ID = ?
                ORDER BY u.SURNAME, u.NAME
                """;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int uid = rs.getInt("USER_ID");
                    String name = rs.getString("NAME") + " " + rs.getString("SURNAME");
                    String birth = rs.getString("BIRTH_NUMBER");
                    String phone = rs.getString("PHONE_NUMBER");
                    String email = rs.getString("EMAIL_ADDRESS");
                    String address = formatAddress(
                            rs.getString("COUNTRY"), rs.getString("STATE"), rs.getString("CITY"),
                            rs.getString("STREET"), rs.getInt("HOUSE_NUMBER"), rs.getInt("ZIP_CODE")
                    );
                    rows.add(new PendingRow(uid, name, birth, phone, email, address));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        pendingTable.setItems(rows);
    }

    private String formatAddress(String country, String state, String city, String street, int house, int zip) {
        java.util.List<String> parts = new java.util.ArrayList<>();
        if (street != null && !street.isEmpty()) parts.add(street + " " + house);
        if (city != null && !city.isEmpty()) parts.add(city);
        if (state != null && !state.isEmpty()) parts.add(state);
        if (country != null && !country.isEmpty()) parts.add(country);
        if (zip > 0) parts.add(String.valueOf(zip));
        return String.join(", ", parts);
    }

    private void approvePending(int userId) {
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE \"User\" SET APPROVED='Y' WHERE USER_ID = ?")) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        loadPendingRequests();
        loadMyClients();
    }

    private void declinePending(int userId) {
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE \"User\" SET APPROVED='R' WHERE USER_ID = ?")) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        loadPendingRequests();
    }

    public static class PendingRow {
        final javafx.beans.property.SimpleIntegerProperty userId = new javafx.beans.property.SimpleIntegerProperty();
        final javafx.beans.property.SimpleStringProperty name = new javafx.beans.property.SimpleStringProperty();
        final javafx.beans.property.SimpleStringProperty birthNumber = new javafx.beans.property.SimpleStringProperty();
        final javafx.beans.property.SimpleStringProperty phone = new javafx.beans.property.SimpleStringProperty();
        final javafx.beans.property.SimpleStringProperty email = new javafx.beans.property.SimpleStringProperty();
        final javafx.beans.property.SimpleStringProperty address = new javafx.beans.property.SimpleStringProperty();

        public PendingRow(int id, String n, String b, String p, String e, String a) {
            userId.set(id);
            name.set(n);
            birthNumber.set(b);
            phone.set(p);
            email.set(e);
            address.set(a);
        }
    }

// --- Documents Logic ---

    private void openClientDocs() {
        if (currentClientId == null) return;
        clientDocsHeader.setText("Documents for " + (currentClientName != null ? currentClientName : "Client"));
        showSection("documents");
    }

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

    private void saveFileToDatabase(File file) {
        if (currentClientId == null) return;
        int clientId = Integer.parseInt(currentClientId);

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
                pstmt.setInt(4, clientId); // Save for selected client

                pstmt.executeUpdate();
                loadClientDocuments(); // Refresh list
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to upload file: " + e.getMessage());
        }
    }

    private void loadClientDocuments() {
        if (documentsListView == null || currentClientId == null) return;
        documentsListView.getItems().clear();
        int clientId = Integer.parseInt(currentClientId);

        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            String sql = "SELECT Document_id, File_name FROM Document WHERE User_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, clientId);
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

    private void openDocument(int docId, String fileName) {
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            String sql = "SELECT File_data FROM Document WHERE Document_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, docId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        InputStream is = rs.getBinaryStream("File_data");

                        String extension = "";
                        int i = fileName.lastIndexOf('.');
                        if (i > 0) extension = fileName.substring(i);
                        else extension = ".dat";

                        File tempFile = File.createTempFile("bankis_doc_", extension);
                        tempFile.deleteOnExit();

                        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = is.read(buffer)) != -1) {
                                fos.write(buffer, 0, bytesRead);
                            }
                        }

                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().open(tempFile);
                        } else {
                            showAlert("Info", "File saved to: " + tempFile.getAbsolutePath());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open file.");
        }
    }

    private void registerClient() {
        if (isEmpty(regNameField) || isEmpty(regSurnameField) || isEmpty(regBirthNumberField) ||
                isEmpty(regPhoneField) || isEmpty(regEmailField) || isEmpty(regPasswordField) ||
                isEmpty(regStreetField) || isEmpty(regHouseNumberField) || isEmpty(regCityField) || isEmpty(regZipField)) {
            showAlert("Validation Error", "All fields are required.");
            return;
        }

        int houseNum;
        int zipCode;
        try {
            houseNum = Integer.parseInt(regHouseNumberField.getText().trim());
            zipCode = Integer.parseInt(regZipField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "House number and ZIP code must be numbers.");
            return;
        }

        int tellerId = HelloApplication.userId;

        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            // 1. Call onboard_new_client procedure
            String sqlCall = "{call onboard_new_client(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
            try (CallableStatement cstmt = conn.prepareCall(sqlCall)) {
                cstmt.setInt(1, tellerId);
                cstmt.setString(2, regNameField.getText().trim());
                cstmt.setString(3, regSurnameField.getText().trim());
                cstmt.setString(4, regPasswordField.getText());
                cstmt.setString(5, regBirthNumberField.getText().trim());
                cstmt.setString(6, regPhoneField.getText().trim());
                cstmt.setString(7, regEmailField.getText().trim());
                cstmt.setString(8, regCountryField.getText().trim());
                cstmt.setString(9, regCityField.getText().trim());
                cstmt.setString(10, regStreetField.getText().trim());
                cstmt.setInt(11, houseNum);
                cstmt.setInt(12, zipCode);

                cstmt.execute();
            }

            // 2. Auto-create a bank account for the new client
            int newClientId = -1;
            String findUserSql = "SELECT u.USER_ID FROM \"User\" u JOIN CLIENT c ON u.USER_ID = c.USER_ID WHERE c.BIRTH_NUMBER = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(findUserSql)) {
                pstmt.setString(1, regBirthNumberField.getText().trim());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        newClientId = rs.getInt("USER_ID");
                    }
                }
            }

            if (newClientId != -1) {
                createInitialAccount(conn, newClientId);
                showAlert("Success", "Client registered and account created successfully.");
                clearRegistrationFields();
                // Optionally return to profile or client list
                showSection("profile");
            } else {
                showAlert("Error", "Client registered but failed to retrieve ID for account creation.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Registration Error", "Registration failed: " + e.getMessage());
        }
    }

    private void createInitialAccount(Connection conn, int clientId) throws SQLException {
        // Generate random unique account number
        String accNum = "CZ" + (1000000000L + new Random().nextInt(900000000));

        String sql = "INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, ?, 0, 'Y', ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accNum);
            pstmt.setInt(2, clientId);
            pstmt.executeUpdate();
        }
    }

    private boolean isEmpty(TextField field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }

    private void clearRegistrationFields() {
        regNameField.clear();
        regSurnameField.clear();
        regBirthNumberField.clear();
        regPhoneField.clear();
        regEmailField.clear();
        regPasswordField.clear();
        regStreetField.clear();
        regHouseNumberField.clear();
        regCityField.clear();
        regZipField.clear();
        regCountryField.setText("Czechia");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (title.contains("Error")) alert.setAlertType(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void loadNotifications() {
        notificationsMessagesContainer.getChildren().clear();
        int myId = HelloApplication.userId;

        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            // Query for messages to the teller with NO SENDER (User_from_id is NULL)
            String sql = "SELECT Message_text, Message_sent_at FROM Message WHERE User_to_id = ? AND User_from_id IS NULL ORDER BY Message_sent_at DESC";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, myId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String text = rs.getString("Message_text");
                        java.sql.Timestamp sentAt = rs.getTimestamp("Message_sent_at");
                        String timeStr = sentAt.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                        addNotificationBubble(text, timeStr);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addNotificationBubble(String message, String timeString) {
        HBox container = new HBox();
        container.setPadding(new Insets(0, 0, 0, 0));
        container.setSpacing(0);
        container.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox bubble = new VBox(5);
        bubble.setMaxWidth(450);
        bubble.setStyle("-fx-background-color: #e8f4f8; -fx-background-radius: 10; -fx-padding: 10; -fx-border-color: #bdc3c7; -fx-border-radius: 10;");

        Label authorLabel = new Label("System Notification");
        authorLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #e74c3c;");

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 14px;");

        Label timeLabel = new Label(timeString);
        timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #95a5a6;");

        bubble.getChildren().addAll(authorLabel, messageLabel, timeLabel);
        container.getChildren().add(bubble);

        notificationsMessagesContainer.getChildren().add(container);
    }


    private void openAccount(String number, String balance, String currency) {
        currentAccountNumber = number;
        accountHeader.setText("Account Details — " + number);
        accountNumberLabel.setText(number);
        accountBalanceLabel.setText(balance);
        accountCurrencyLabel.setText(currency);

        showSection("account");
    }

    private void setupTransactionTable() {
        if (dateColumn != null) {
            dateColumn.setCellValueFactory(param -> param.getValue().dateProperty());
        }
        if (descriptionColumn != null) {
            descriptionColumn.setCellValueFactory(param -> param.getValue().descriptionProperty());
        }
        if (amountColumn != null) {
            amountColumn.setCellValueFactory(param -> param.getValue().amountProperty());
        }
    }

    /**
     * Updates the teller profile information from the database
     */
    public void updateProfileInfo(String name, String surname, String phone, String email) {
        nameField.setText(name);
        surnameField.setText(surname);
        phoneField.setText(phone);
        emailField.setText(email);
    }


    private void onLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Are you sure you want to log out?");
        alert.setContentText("You will be returned to the login screen.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (appViewController != null) {
                appViewController.logout();
            } else {
                System.out.println("Logout requested, but AppViewController is not linked.");
            }
        }
    }

    private void addChatBubble(String author, String message, boolean outgoing, boolean isRead) {
        HBox container = new HBox();
        container.setPadding(new Insets(0, 0, 0, 0));
        container.setSpacing(0);
        container.setStyle("");
        container.setFillHeight(true);
        container.setAlignment(outgoing ? javafx.geometry.Pos.CENTER_RIGHT : javafx.geometry.Pos.CENTER_LEFT);

        VBox bubble = new VBox(5);
        bubble.setMaxWidth(420);
        bubble.setStyle(outgoing
                ? "-fx-background-color: #d4edda; -fx-background-radius: 10; -fx-padding: 10;"
                : "-fx-background-color: #e8f4f8; -fx-background-radius: 10; -fx-padding: 10;");

        Label authorLabel = new Label(author);
        authorLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 14px;");
        Label timeLabel = new Label(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #95a5a6;");

        bubble.getChildren().addAll(authorLabel, messageLabel, timeLabel);

        if (outgoing) {
            Label readStatus = new Label("✔");
            if (isRead) {
                readStatus.setStyle("-fx-text-fill: green; -fx-font-size: 12px;");
            } else {
                readStatus.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");
            }
            HBox metaContainer = new HBox(readStatus);
            metaContainer.setAlignment(javafx.geometry.Pos.BOTTOM_RIGHT);
            bubble.getChildren().add(metaContainer);
        }

        container.getChildren().add(bubble);

        clientChatMessagesContainer.getChildren().add(container);
    }

    // Helper data structures
    private static class AccountEntry {
        final String number;
        final String balance;
        final String currency;

        AccountEntry(String number, String balance, String currency) {
            this.number = number;
            this.balance = balance;
            this.currency = currency;
        }
    }

    // Table row model with simple string properties
    public static class TransactionRow {
        private final javafx.beans.property.SimpleStringProperty date = new javafx.beans.property.SimpleStringProperty();
        private final javafx.beans.property.SimpleStringProperty description = new javafx.beans.property.SimpleStringProperty();
        private final javafx.beans.property.SimpleStringProperty amount = new javafx.beans.property.SimpleStringProperty();

        public TransactionRow(String date, String description, String amount) {
            this.date.set(date);
            this.description.set(description);
            this.amount.set(amount);
        }

        public javafx.beans.property.SimpleStringProperty dateProperty() {
            return date;
        }

        public javafx.beans.property.SimpleStringProperty descriptionProperty() {
            return description;
        }

        public javafx.beans.property.SimpleStringProperty amountProperty() {
            return amount;
        }
    }
}
