package upce.javafx;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminViewController {

    private AppViewController appViewController;

    public void setAppViewController(AppViewController appViewController) {
        this.appViewController = appViewController;
    }

    // Sections
    @FXML private VBox branchesSection;
    @FXML private VBox usersSection;
    @FXML private VBox clientsSection;
    @FXML private VBox tellersSection;
    @FXML private VBox accountsSection;
    @FXML private VBox transactionsSection;
    @FXML private VBox loginsSection;
    @FXML private VBox messagesSection;
    @FXML private VBox auditsSection;

    // Branches UI
    @FXML private TextField branchSearchField;
    @FXML private TableView<BranchRow> branchesTable;

    // Users UI
    @FXML private TextField userNameSearchField;
    @FXML private TextField userSurnameSearchField;
    @FXML private TableView<UserRow> usersTable;

    // Clients UI
    @FXML private TextField clientNameSearchField;
    @FXML private TextField clientSurnameSearchField;
    @FXML private TableView<ClientRow> clientsTable;

    // Tellers UI
    @FXML private TextField tellerNameSearchField;
    @FXML private TextField tellerSurnameSearchField;
    @FXML private TableView<TellerRow> tellersTable;

    // Accounts UI
    @FXML private TextField accountOwnerNameField;
    @FXML private TextField accountOwnerSurnameField;
    @FXML private TextField accountNumberSearchField;
    @FXML private TableView<AccountRow> accountsTable;

    // Transactions UI
    @FXML private TextField transactionAccountSearchField;
    @FXML private ComboBox<String> transactionTypeFilter;
    @FXML private ComboBox<String> transactionDirectionFilter;
    @FXML private TextField amountMinField;
    @FXML private TextField amountMaxField;
    @FXML private DatePicker dateFromPicker;
    @FXML private DatePicker dateToPicker;
    @FXML private TableView<TransactionRow> transactionsTable;

    // Messages UI
    @FXML private TableView<MessageRow> messagesTable;
    @FXML private TextField messageUserSearchField;
    @FXML private DatePicker messageDateFromPicker;
    @FXML private DatePicker messageDateToPicker;

    @FXML
    private void initialize() {
        // Show branches by default when admin view loads
        showOnly(branchesSection);
        setupBranchesTable();
        setupUsersTable();
        setupClientsTable();
        setupTellersTable();
        setupAccountsTable();
        setupTransactionsTable();
        setupMessagesTable();

        if (transactionDirectionFilter != null) {
            transactionDirectionFilter.setItems(FXCollections.observableArrayList("All", "Incoming", "Outgoing"));
            transactionDirectionFilter.getSelectionModel().selectFirst();
        }
        if (transactionTypeFilter != null) {
            transactionTypeFilter.setItems(loadTransactionTypeNames());
        }
    }

    public void onLoginLoaded() {
        // Called from AppViewController after successful admin login
        reloadBranches();
    }

    // Navigation
    @FXML private void showBranches() { showOnly(branchesSection); reloadBranches(); }
    @FXML private void showUsers() { showOnly(usersSection); reloadUsers(); }
    @FXML private void showClients() { showOnly(clientsSection); reloadClients(); }
    @FXML private void showTellers() { showOnly(tellersSection); reloadTellers(); }
    @FXML private void showAccounts() { showOnly(accountsSection); reloadAccounts(); }
    @FXML private void showTransactions() { showOnly(transactionsSection); reloadTransactions(); }
    @FXML private void showLogins() { showOnly(loginsSection); /* TODO */ }
    @FXML private void showMessages() { showOnly(messagesSection); reloadMessages(); }
    @FXML private void showAudits() { showOnly(auditsSection); /* TODO */ }

    @FXML private void onLogout() {
        if (appViewController != null) {
            appViewController.logout();
        }
    }

    private void showOnly(Node toShow) {
        // Toggle visible/managed for all sections
        setVisibleManaged(branchesSection, toShow == branchesSection);
        setVisibleManaged(usersSection, toShow == usersSection);
        setVisibleManaged(clientsSection, toShow == clientsSection);
        setVisibleManaged(tellersSection, toShow == tellersSection);
        setVisibleManaged(accountsSection, toShow == accountsSection);
        setVisibleManaged(transactionsSection, toShow == transactionsSection);
        setVisibleManaged(loginsSection, toShow == loginsSection);
        setVisibleManaged(messagesSection, toShow == messagesSection);
        setVisibleManaged(auditsSection, toShow == auditsSection);
    }

    private void setVisibleManaged(Node node, boolean value) {
        if (node != null) {
            node.setVisible(value);
            node.setManaged(value);
        }
    }

    // ----- Branches -----
    private boolean branchesColumnsInitialized = false;

    private void setupBranchesTable() {
        if (branchesTable == null || branchesColumnsInitialized) return;

        TableColumn<BranchRow, String> nameCol = new TableColumn<>("Branch");
        nameCol.setCellValueFactory(data -> data.getValue().branchNameProperty);
        nameCol.setPrefWidth(200);

        TableColumn<BranchRow, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(data -> data.getValue().addressTextProperty);
        addressCol.setPrefWidth(420);

        TableColumn<BranchRow, String> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            {
                editBtn.setOnAction(e -> {
                    BranchRow row = getTableView().getItems().get(getIndex());
                    editBranch(row);
                });
                deleteBtn.setOnAction(e -> {
                    BranchRow row = getTableView().getItems().get(getIndex());
                    deleteBranch(row.branchId);
                });
                editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(6, editBtn, deleteBtn);
                    setGraphic(box);
                }
            }
        });
        actionsCol.setPrefWidth(200);

        branchesTable.getColumns().addAll(nameCol, addressCol, actionsCol);
        branchesColumnsInitialized = true;
    }

    @FXML
    private void reloadBranches() {
        setupBranchesTable();
        ObservableList<BranchRow> rows = FXCollections.observableArrayList();
        String filter = branchSearchField != null ? branchSearchField.getText() : null;
        String sql = """
                SELECT b.BRANCH_ID, b.BRANCH_NAME,
                       a.COUNTRY, a.STATE, a.CITY, a.STREET, a.HOUSE_NUMBER, a.ZIP_CODE
                FROM BRANCH b
                JOIN ADDRESS a ON b.ADDRESS_ID = a.ADDRESS_ID
                WHERE (? IS NULL OR ? = '' OR UPPER(b.BRANCH_NAME) LIKE ?)
                ORDER BY b.BRANCH_NAME
                """;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = (filter == null || filter.isEmpty()) ? null : (filter.toUpperCase() + "%");
            ps.setString(1, filter);
            ps.setString(2, filter);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("BRANCH_ID");
                    String name = rs.getString("BRANCH_NAME");
                    String address = formatAddress(
                            rs.getString("COUNTRY"), rs.getString("STATE"), rs.getString("CITY"),
                            rs.getString("STREET"), rs.getInt("HOUSE_NUMBER"), rs.getInt("ZIP_CODE")
                    );
                    rows.add(new BranchRow(id, name, address));
                }
            }
        } catch (SQLException e) {
            showError("Error loading branches: " + e.getMessage());
        }
        branchesTable.setItems(rows);
    }

    private void editBranch(BranchRow row) {
        Dialog<BranchEditResult> dialog = new Dialog<>();
        dialog.setTitle("Edit Branch");

        Label nameL = new Label("Branch name:");
        TextField nameF = new TextField(row.branchNameProperty.get());

        Label cL = new Label("Country:");
        TextField cF = new TextField();
        Label sL = new Label("State:");
        TextField sF = new TextField();
        Label cityL = new Label("City:");
        TextField cityF = new TextField();
        Label streetL = new Label("Street:");
        TextField streetF = new TextField();
        Label houseL = new Label("House No:");
        TextField houseF = new TextField();
        Label zipL = new Label("ZIP:");
        TextField zipF = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(8); grid.setVgap(8);
        grid.addRow(0, nameL, nameF);
        grid.addRow(1, cL, cF);
        grid.addRow(2, sL, sF);
        grid.addRow(3, cityL, cityF);
        grid.addRow(4, streetL, streetF);
        grid.addRow(5, houseL, houseF);
        grid.addRow(6, zipL, zipF);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                return new BranchEditResult(nameF.getText(), cF.getText(), sF.getText(), cityF.getText(), streetF.getText(), houseF.getText(), zipF.getText());
            }
            return null;
        });

        BranchEditResult res = dialog.showAndWait().orElse(null);
        if (res == null) return;

        // Insert new address row and update branch
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            int newAddressId;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO ADDRESS(ADDRESS_ID, COUNTRY, STATE, CITY, STREET, HOUSE_NUMBER, ZIP_CODE) VALUES(ADDRESS_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?)",
                    new String[]{"ADDRESS_ID"})) {
                ps.setString(1, res.country);
                ps.setString(2, res.state);
                ps.setString(3, res.city);
                ps.setString(4, res.street);
                ps.setInt(5, Integer.parseInt(res.houseNumber));
                ps.setInt(6, Integer.parseInt(res.zip));
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("SELECT ADDRESS_SEQ.currval AS ID FROM dual")) {
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    newAddressId = rs.getInt("ID");
                }
            }

            try (PreparedStatement ps = conn.prepareStatement("UPDATE BRANCH SET BRANCH_NAME = ?, ADDRESS_ID = ? WHERE BRANCH_ID = ?")) {
                ps.setString(1, res.branchName);
                ps.setInt(2, newAddressId);
                ps.setInt(3, row.branchId.get());
                ps.executeUpdate();
            }
            conn.commit();
        } catch (SQLException ex) {
            showError("Error updating branch: " + ex.getMessage());
        }
        reloadBranches();
    }

    private void deleteBranch(SimpleIntegerProperty branchId) {
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM BRANCH WHERE BRANCH_ID = ?")) {
            ps.setInt(1, branchId.get());
            ps.executeUpdate();
        } catch (SQLException e) {
            showError("Error deleting branch: " + e.getMessage());
        }
        reloadBranches();
    }

    private String formatAddress(String country, String state, String city, String street, int house, int zip) {
        List<String> parts = new ArrayList<>();
        if (street != null && !street.isEmpty()) parts.add(street + " " + house);
        if (city != null && !city.isEmpty()) parts.add(city);
        if (state != null && !state.isEmpty()) parts.add(state);
        if (country != null && !country.isEmpty()) parts.add(country);
        if (zip > 0) parts.add(String.valueOf(zip));
        return String.join(", ", parts);
    }

    // ----- Users -----
    private boolean usersColumnsInitialized = false;

    private void setupUsersTable() {
        if (usersTable == null || usersColumnsInitialized) return;

        TableColumn<UserRow, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(d -> d.getValue().nameProperty);
        nameCol.setPrefWidth(140);

        TableColumn<UserRow, String> surnameCol = new TableColumn<>("Surname");
        surnameCol.setCellValueFactory(d -> d.getValue().surnameProperty);
        surnameCol.setPrefWidth(160);

        TableColumn<UserRow, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(d -> d.getValue().roleNameProperty);
        roleCol.setPrefWidth(120);

        TableColumn<UserRow, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(d -> d.getValue().addressTextProperty);
        addressCol.setPrefWidth(320);

        TableColumn<UserRow, String> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final Button emulateBtn = new Button("Emulate");
            {
                editBtn.setOnAction(e -> editUser(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(e -> deleteUser(getTableView().getItems().get(getIndex()).userId));
                emulateBtn.setOnAction(e -> {
                    UserRow row = getTableView().getItems().get(getIndex());
                    if (appViewController != null) {
                        String role = row.roleNameProperty.get();
                        if ("Client".equalsIgnoreCase(role)) {
                            appViewController.startEmulateClient(row.userId.get());
                        } else if ("Teller".equalsIgnoreCase(role)) {
                            appViewController.startEmulateTeller(row.userId.get());
                        } else {
                            // no-op for Admin/other
                        }
                    }
                });
                editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                emulateBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    UserRow row = getTableView().getItems().get(getIndex());
                    boolean emulateEnabled = row != null && ("Client".equalsIgnoreCase(row.roleNameProperty.get()) || "Teller".equalsIgnoreCase(row.roleNameProperty.get()));
                    emulateBtn.setDisable(!emulateEnabled);
                    HBox box = new HBox(6, editBtn, deleteBtn, emulateBtn);
                    setGraphic(box);
                }
            }
        });
        actionsCol.setPrefWidth(260);

        usersTable.getColumns().addAll(nameCol, surnameCol, roleCol, addressCol, actionsCol);
        usersColumnsInitialized = true;
    }

    @FXML
    private void reloadUsers() {
        setupUsersTable();
        String nameFilter = userNameSearchField != null ? userNameSearchField.getText() : null;
        String surnameFilter = userSurnameSearchField != null ? userSurnameSearchField.getText() : null;
        ObservableList<UserRow> rows = FXCollections.observableArrayList();
        String sql = """
                SELECT u.USER_ID, u.NAME, u.SURNAME, r.ROLE_NAME,
                       a.COUNTRY, a.STATE, a.CITY, a.STREET, a.HOUSE_NUMBER, a.ZIP_CODE
                FROM "User" u
                JOIN ROLE r ON u.ROLE_ID = r.ROLE_ID
                JOIN ADDRESS a ON u.ADDRESS_ID = a.ADDRESS_ID
                WHERE (? IS NULL OR ? = '' OR UPPER(u.NAME) LIKE ?)
                  AND (? IS NULL OR ? = '' OR UPPER(u.SURNAME) LIKE ?)
                ORDER BY u.SURNAME, u.NAME
                """;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String likeName = (nameFilter == null || nameFilter.isEmpty()) ? null : (nameFilter.toUpperCase() + "%");
            String likeSurname = (surnameFilter == null || surnameFilter.isEmpty()) ? null : (surnameFilter.toUpperCase() + "%");
            ps.setString(1, nameFilter);
            ps.setString(2, nameFilter);
            ps.setString(3, likeName);
            ps.setString(4, surnameFilter);
            ps.setString(5, surnameFilter);
            ps.setString(6, likeSurname);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("USER_ID");
                    String name = rs.getString("NAME");
                    String surname = rs.getString("SURNAME");
                    String role = rs.getString("ROLE_NAME");
                    String address = formatAddress(
                            rs.getString("COUNTRY"), rs.getString("STATE"), rs.getString("CITY"),
                            rs.getString("STREET"), rs.getInt("HOUSE_NUMBER"), rs.getInt("ZIP_CODE")
                    );
                    rows.add(new UserRow(id, name, surname, role, address));
                }
            }
        } catch (SQLException e) {
            showError("Error loading users: " + e.getMessage());
        }
        usersTable.setItems(rows);
    }

    // Stubs for yet-to-implement sections to satisfy FXML onAction hooks
    // ----- Clients -----
    private boolean clientsColumnsInitialized = false;
    private void setupClientsTable() {
        if (clientsTable == null || clientsColumnsInitialized) return;
        TableColumn<ClientRow, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(d -> d.getValue().name);
        nameCol.setPrefWidth(140);
        TableColumn<ClientRow, String> surnameCol = new TableColumn<>("Surname");
        surnameCol.setCellValueFactory(d -> d.getValue().surname);
        surnameCol.setPrefWidth(160);
        TableColumn<ClientRow, String> contactCol = new TableColumn<>("Contact");
        contactCol.setCellValueFactory(d -> d.getValue().contact);
        contactCol.setPrefWidth(220);
        TableColumn<ClientRow, String> tellerCol = new TableColumn<>("Teller");
        tellerCol.setCellValueFactory(d -> d.getValue().tellerName);
        tellerCol.setPrefWidth(180);
        TableColumn<ClientRow, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(d -> d.getValue().address);
        addressCol.setPrefWidth(320);
        TableColumn<ClientRow, String> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final Button emulateBtn = new Button("Emulate");
            {
                editBtn.setOnAction(e -> editClient(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(e -> deleteClient(getTableView().getItems().get(getIndex()).userId));
                emulateBtn.setOnAction(e -> {
                    ClientRow row = getTableView().getItems().get(getIndex());
                    if (appViewController != null) appViewController.startEmulateClient(row.userId.get());
                });
                editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                emulateBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(new HBox(6, editBtn, deleteBtn, emulateBtn));
            }
        });
        actionsCol.setPrefWidth(260);
        clientsTable.getColumns().addAll(nameCol, surnameCol, contactCol, tellerCol, addressCol, actionsCol);
        clientsColumnsInitialized = true;
    }

    @FXML private void reloadClients() {
        setupClientsTable();
        ObservableList<ClientRow> rows = FXCollections.observableArrayList();
        String nameF = clientNameSearchField != null ? clientNameSearchField.getText() : null;
        String surF = clientSurnameSearchField != null ? clientSurnameSearchField.getText() : null;
        String sql = """
                SELECT c.USER_ID, u.NAME, u.SURNAME, c.PHONE_NUMBER, c.EMAIL_ADDRESS,
                       ut.NAME AS TELLER_NAME, ut.SURNAME AS TELLER_SURNAME,
                       a.COUNTRY, a.STATE, a.CITY, a.STREET, a.HOUSE_NUMBER, a.ZIP_CODE
                FROM CLIENT c
                JOIN "User" u ON u.USER_ID = c.USER_ID
                JOIN TELLER t ON t.USER_ID = c.TELLER_ID
                JOIN "User" ut ON ut.USER_ID = t.USER_ID
                JOIN ADDRESS a ON a.ADDRESS_ID = u.ADDRESS_ID
                WHERE (? IS NULL OR ? = '' OR UPPER(u.NAME) LIKE ?)
                  AND (? IS NULL OR ? = '' OR UPPER(u.SURNAME) LIKE ?)
                ORDER BY u.SURNAME, u.NAME
                """;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String likeN = (nameF == null || nameF.isEmpty()) ? null : nameF.toUpperCase() + "%";
            String likeS = (surF == null || surF.isEmpty()) ? null : surF.toUpperCase() + "%";
            ps.setString(1, nameF); ps.setString(2, nameF); ps.setString(3, likeN);
            ps.setString(4, surF); ps.setString(5, surF); ps.setString(6, likeS);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("USER_ID");
                    String n = rs.getString("NAME");
                    String s = rs.getString("SURNAME");
                    String contact = rs.getString("PHONE_NUMBER") + " / " + rs.getString("EMAIL_ADDRESS");
                    String teller = rs.getString("TELLER_NAME") + " " + rs.getString("TELLER_SURNAME");
                    String addr = formatAddress(rs.getString("COUNTRY"), rs.getString("STATE"), rs.getString("CITY"), rs.getString("STREET"), rs.getInt("HOUSE_NUMBER"), rs.getInt("ZIP_CODE"));
                    rows.add(new ClientRow(id, n, s, contact, teller, addr));
                }
            }
        } catch (SQLException e) { showError("Error loading clients: " + e.getMessage()); }
        clientsTable.setItems(rows);
    }

    private void editClient(ClientRow row) {
        // Implement reassignment to teller and optional address change (new address row)
        Dialog<ClientEditResult> dlg = new Dialog<>();
        dlg.setTitle("Edit Client");
        ComboBox<UserOption> tellerBox = new ComboBox<>(loadTellers());
        tellerBox.getSelectionModel().selectFirst();
        TextField cF = new TextField(); TextField sF = new TextField(); TextField cityF = new TextField(); TextField streetF = new TextField(); TextField houseF = new TextField(); TextField zipF = new TextField(); TextField countryF = new TextField();
        GridPane grid = new GridPane(); grid.setHgap(8); grid.setVgap(8);
        grid.addRow(0, new Label("Reassign Teller:"), tellerBox);
        grid.addRow(1, new Label("Country:"), countryF);
        grid.addRow(2, new Label("State:"), cF);
        grid.addRow(3, new Label("City:"), cityF);
        grid.addRow(4, new Label("Street:"), streetF);
        grid.addRow(5, new Label("House No:"), houseF);
        grid.addRow(6, new Label("ZIP:"), zipF);
        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(b -> b==ButtonType.OK ? new ClientEditResult(tellerBox.getValue(), countryF.getText(), cF.getText(), cityF.getText(), streetF.getText(), houseF.getText(), zipF.getText()) : null);
        ClientEditResult res = dlg.showAndWait().orElse(null);
        if (res == null) return;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            // Insert new address
            int newAddrId;
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO ADDRESS(ADDRESS_ID, COUNTRY, STATE, CITY, STREET, HOUSE_NUMBER, ZIP_CODE) VALUES(ADDRESS_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?)")) {
                ps.setString(1, res.country); ps.setString(2, res.state); ps.setString(3, res.city); ps.setString(4, res.street); ps.setInt(5, parseIntSafe(res.house)); ps.setInt(6, parseIntSafe(res.zip));
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("SELECT ADDRESS_SEQ.CURRVAL AS ID FROM dual"); ResultSet rs = ps.executeQuery()) { rs.next(); newAddrId = rs.getInt("ID"); }
            // Update user address
            try (PreparedStatement ps = conn.prepareStatement("UPDATE \"User\" SET ADDRESS_ID = ? WHERE USER_ID = ?")) { ps.setInt(1, newAddrId); ps.setInt(2, row.userId.get()); ps.executeUpdate(); }
            // Reassign teller
            try (PreparedStatement ps = conn.prepareStatement("UPDATE CLIENT SET TELLER_ID = ? WHERE USER_ID = ?")) { ps.setInt(1, res.teller.userId); ps.setInt(2, row.userId.get()); ps.executeUpdate(); }
            conn.commit();
        } catch (SQLException e) { showError("Error editing client: " + e.getMessage()); }
        reloadClients();
    }

    private void deleteClient(SimpleIntegerProperty clientUserId) {
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            // Delete transactions linked to client's accounts
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM TRANSACTION WHERE ACCOUNT_FROM_ID IN (SELECT ACCOUNT_ID FROM ACCOUNT WHERE CLIENT_ID = ?) OR ACCOUNT_TO_ID IN (SELECT ACCOUNT_ID FROM ACCOUNT WHERE CLIENT_ID = ?)")) {
                ps.setInt(1, clientUserId.get()); ps.setInt(2, clientUserId.get()); ps.executeUpdate();
            }
            // Delete accounts
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM ACCOUNT WHERE CLIENT_ID = ?")) { ps.setInt(1, clientUserId.get()); ps.executeUpdate(); }
            // Delete client
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM CLIENT WHERE USER_ID = ?")) { ps.setInt(1, clientUserId.get()); ps.executeUpdate(); }
            conn.commit();
        } catch (SQLException e) { showError("Error deleting client: " + e.getMessage()); }
        reloadClients();
    }

    // ----- Tellers -----
    private boolean tellersColumnsInitialized = false;
    private void setupTellersTable() {
        if (tellersTable == null || tellersColumnsInitialized) return;
        TableColumn<TellerRow, String> nameCol = new TableColumn<>("Name"); nameCol.setCellValueFactory(d -> d.getValue().name); nameCol.setPrefWidth(140);
        TableColumn<TellerRow, String> surnameCol = new TableColumn<>("Surname"); surnameCol.setCellValueFactory(d -> d.getValue().surname); surnameCol.setPrefWidth(160);
        TableColumn<TellerRow, String> contactCol = new TableColumn<>("Work contact"); contactCol.setCellValueFactory(d -> d.getValue().contact); contactCol.setPrefWidth(240);
        TableColumn<TellerRow, String> branchCol = new TableColumn<>("Branch"); branchCol.setCellValueFactory(d -> d.getValue().branchName); branchCol.setPrefWidth(200);
        TableColumn<TellerRow, String> addressCol = new TableColumn<>("Address"); addressCol.setCellValueFactory(d -> d.getValue().address); addressCol.setPrefWidth(320);
        TableColumn<TellerRow, String> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> new TableCell<>(){
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final Button emulateBtn = new Button("Emulate");
            { editBtn.setOnAction(e -> editTeller(getTableView().getItems().get(getIndex())));
              deleteBtn.setOnAction(e -> deleteTeller(getTableView().getItems().get(getIndex()).userId));
              emulateBtn.setOnAction(e -> { TellerRow r = getTableView().getItems().get(getIndex()); if (appViewController!=null) appViewController.startEmulateTeller(r.userId.get()); });
              editBtn.setStyle("-fx-background-color:#3498db;-fx-text-fill:white;");
              deleteBtn.setStyle("-fx-background-color:#e74c3c;-fx-text-fill:white;");
              emulateBtn.setStyle("-fx-background-color:#2ecc71;-fx-text-fill:white;"); }
            @Override protected void updateItem(String item, boolean empty){ super.updateItem(item, empty); setGraphic(empty? null : new HBox(6, editBtn, deleteBtn, emulateBtn)); }
        });
        actionsCol.setPrefWidth(260);
        tellersTable.getColumns().addAll(nameCol,surnameCol,contactCol,branchCol,addressCol,actionsCol);
        tellersColumnsInitialized = true;
    }

    @FXML private void reloadTellers() {
        setupTellersTable();
        ObservableList<TellerRow> rows = FXCollections.observableArrayList();
        String nameF = tellerNameSearchField != null ? tellerNameSearchField.getText() : null;
        String surF = tellerSurnameSearchField != null ? tellerSurnameSearchField.getText() : null;
        String sql = """
                SELECT t.USER_ID, ut.NAME, ut.SURNAME, t.WORK_PHONE_NUMBER, t.WORK_EMAIL_ADDRESS,
                       b.BRANCH_NAME,
                       a.COUNTRY, a.STATE, a.CITY, a.STREET, a.HOUSE_NUMBER, a.ZIP_CODE
                FROM TELLER t
                JOIN "User" ut ON ut.USER_ID = t.USER_ID
                JOIN BRANCH b ON b.BRANCH_ID = t.BRANCH_ID
                JOIN ADDRESS a ON a.ADDRESS_ID = ut.ADDRESS_ID
                WHERE (? IS NULL OR ? = '' OR UPPER(ut.NAME) LIKE ?)
                  AND (? IS NULL OR ? = '' OR UPPER(ut.SURNAME) LIKE ?)
                ORDER BY ut.SURNAME, ut.NAME
                """;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            String likeN = (nameF==null||nameF.isEmpty())? null : nameF.toUpperCase()+"%";
            String likeS = (surF==null||surF.isEmpty())? null : surF.toUpperCase()+"%";
            ps.setString(1,nameF); ps.setString(2,nameF); ps.setString(3,likeN);
            ps.setString(4,surF); ps.setString(5,surF); ps.setString(6,likeS);
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    rows.add(new TellerRow(
                            rs.getInt("USER_ID"), rs.getString("NAME"), rs.getString("SURNAME"),
                            rs.getString("WORK_PHONE_NUMBER")+" / "+rs.getString("WORK_EMAIL_ADDRESS"),
                            rs.getString("BRANCH_NAME"),
                            formatAddress(rs.getString("COUNTRY"), rs.getString("STATE"), rs.getString("CITY"), rs.getString("STREET"), rs.getInt("HOUSE_NUMBER"), rs.getInt("ZIP_CODE"))
                    ));
                }
            }
        } catch (SQLException e){ showError("Error loading tellers: "+e.getMessage()); }
        tellersTable.setItems(rows);
    }

    private void editTeller(TellerRow row) {
        Dialog<TellerEditResult> dlg = new Dialog<>(); dlg.setTitle("Edit Teller");
        ComboBox<BranchOption> branchBox = new ComboBox<>(loadBranches()); branchBox.getSelectionModel().selectFirst();
        TextField cF = new TextField(); TextField sF = new TextField(); TextField cityF = new TextField(); TextField streetF = new TextField(); TextField houseF = new TextField(); TextField zipF = new TextField(); TextField countryF = new TextField();
        GridPane grid = new GridPane(); grid.setHgap(8); grid.setVgap(8);
        grid.addRow(0, new Label("Reassign Branch:"), branchBox);
        grid.addRow(1, new Label("Country:"), countryF);
        grid.addRow(2, new Label("State:"), sF);
        grid.addRow(3, new Label("City:"), cityF);
        grid.addRow(4, new Label("Street:"), streetF);
        grid.addRow(5, new Label("House No:"), houseF);
        grid.addRow(6, new Label("ZIP:"), zipF);
        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(b -> b==ButtonType.OK? new TellerEditResult(branchBox.getValue(), countryF.getText(), sF.getText(), cityF.getText(), streetF.getText(), houseF.getText(), zipF.getText()) : null);
        TellerEditResult res = dlg.showAndWait().orElse(null); if (res==null) return;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()){
            conn.setAutoCommit(false);
            // New address
            int newAddrId;
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO ADDRESS(ADDRESS_ID, COUNTRY, STATE, CITY, STREET, HOUSE_NUMBER, ZIP_CODE) VALUES(ADDRESS_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?)")){
                ps.setString(1,res.country); ps.setString(2,res.state); ps.setString(3,res.city); ps.setString(4,res.street); ps.setInt(5, parseIntSafe(res.house)); ps.setInt(6, parseIntSafe(res.zip));
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("SELECT ADDRESS_SEQ.CURRVAL AS ID FROM dual"); ResultSet rs = ps.executeQuery()) { rs.next(); newAddrId = rs.getInt("ID"); }
            try (PreparedStatement ps = conn.prepareStatement("UPDATE \"User\" SET ADDRESS_ID = ? WHERE USER_ID = ?")) { ps.setInt(1,newAddrId); ps.setInt(2,row.userId.get()); ps.executeUpdate(); }
            try (PreparedStatement ps = conn.prepareStatement("UPDATE TELLER SET BRANCH_ID = ? WHERE USER_ID = ?")) { ps.setInt(1,res.branch.branchId); ps.setInt(2,row.userId.get()); ps.executeUpdate(); }
            conn.commit();
        } catch (SQLException e){ showError("Error editing teller: "+e.getMessage()); }
        reloadTellers();
    }

    private void deleteTeller(SimpleIntegerProperty tellerUserId) {
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()){
            conn.setAutoCommit(false);
            // Delete all clients of teller (transactions -> accounts -> client rows)
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM TRANSACTION WHERE ACCOUNT_FROM_ID IN (SELECT ACCOUNT_ID FROM ACCOUNT WHERE CLIENT_ID IN (SELECT USER_ID FROM CLIENT WHERE TELLER_ID = ?)) OR ACCOUNT_TO_ID IN (SELECT ACCOUNT_ID FROM ACCOUNT WHERE CLIENT_ID IN (SELECT USER_ID FROM CLIENT WHERE TELLER_ID = ?))")){
                ps.setInt(1,tellerUserId.get()); ps.setInt(2,tellerUserId.get()); ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM ACCOUNT WHERE CLIENT_ID IN (SELECT USER_ID FROM CLIENT WHERE TELLER_ID = ?)")) { ps.setInt(1,tellerUserId.get()); ps.executeUpdate(); }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM CLIENT WHERE TELLER_ID = ?")) { ps.setInt(1,tellerUserId.get()); ps.executeUpdate(); }
            // Delete teller row
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM TELLER WHERE USER_ID = ?")) { ps.setInt(1,tellerUserId.get()); ps.executeUpdate(); }
            conn.commit();
        } catch (SQLException e){ showError("Error deleting teller: "+e.getMessage()); }
        reloadTellers();
    }

    // ----- Accounts -----
    private boolean accountsColumnsInitialized = false;
    private void setupAccountsTable(){
        if (accountsTable==null || accountsColumnsInitialized) return;
        TableColumn<AccountRow,String> numberCol = new TableColumn<>("Account"); numberCol.setCellValueFactory(d->d.getValue().number); numberCol.setPrefWidth(200);
        TableColumn<AccountRow,String> ownerCol = new TableColumn<>("Owner"); ownerCol.setCellValueFactory(d->d.getValue().owner); ownerCol.setPrefWidth(240);
        TableColumn<AccountRow,String> balanceCol = new TableColumn<>("Balance"); balanceCol.setCellValueFactory(d->d.getValue().balance); balanceCol.setPrefWidth(140);
        TableColumn<AccountRow,String> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col-> new TableCell<>(){
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            { editBtn.setOnAction(e-> editAccount(getTableView().getItems().get(getIndex())));
              deleteBtn.setOnAction(e-> deleteAccount(getTableView().getItems().get(getIndex()).accountId));
              editBtn.setStyle("-fx-background-color:#3498db;-fx-text-fill:white;");
              deleteBtn.setStyle("-fx-background-color:#e74c3c;-fx-text-fill:white;"); }
            @Override protected void updateItem(String item, boolean empty){ super.updateItem(item, empty); setGraphic(empty? null : new HBox(6, editBtn, deleteBtn)); }
        });
        actionsCol.setPrefWidth(200);
        accountsTable.getColumns().addAll(numberCol, ownerCol, balanceCol, actionsCol);
        accountsColumnsInitialized = true;
    }

    @FXML private void reloadAccounts(){
        setupAccountsTable();
        ObservableList<AccountRow> rows = FXCollections.observableArrayList();
        String nameF = accountOwnerNameField!=null? accountOwnerNameField.getText():null;
        String surF = accountOwnerSurnameField!=null? accountOwnerSurnameField.getText():null;
        String accF = accountNumberSearchField!=null? accountNumberSearchField.getText():null;
        String sql = """
                SELECT a.ACCOUNT_ID, a.ACCOUNT_NUMBER, a.ACCOUNT_BALANCE,
                       u.NAME, u.SURNAME
                FROM ACCOUNT a
                JOIN CLIENT c ON c.USER_ID = a.CLIENT_ID
                JOIN "User" u ON u.USER_ID = c.USER_ID
                WHERE (? IS NULL OR ? = '' OR UPPER(u.NAME) LIKE ?)
                  AND (? IS NULL OR ? = '' OR UPPER(u.SURNAME) LIKE ?)
                  AND (? IS NULL OR ? = '' OR UPPER(a.ACCOUNT_NUMBER) LIKE ?)
                ORDER BY a.ACCOUNT_NUMBER
                """;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            String likeN = (nameF==null||nameF.isEmpty())? null : nameF.toUpperCase()+"%";
            String likeS = (surF==null||surF.isEmpty())? null : surF.toUpperCase()+"%";
            String likeA = (accF==null||accF.isEmpty())? null : accF.toUpperCase()+"%";
            ps.setString(1,nameF); ps.setString(2,nameF); ps.setString(3,likeN);
            ps.setString(4,surF); ps.setString(5,surF); ps.setString(6,likeS);
            ps.setString(7,accF); ps.setString(8,accF); ps.setString(9,likeA);
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    rows.add(new AccountRow(
                            rs.getInt("ACCOUNT_ID"), rs.getString("ACCOUNT_NUMBER"),
                            rs.getString("NAME")+" "+rs.getString("SURNAME"),
                            String.valueOf(rs.getBigDecimal("ACCOUNT_BALANCE"))
                    ));
                }
            }
        } catch (SQLException e){ showError("Error loading accounts: "+e.getMessage()); }
        accountsTable.setItems(rows);
    }

    private void editAccount(AccountRow row){
        Dialog<AccountEditResult> dlg = new Dialog<>(); dlg.setTitle("Edit Account");
        ComboBox<UserOption> clientBox = new ComboBox<>(loadClients()); clientBox.getSelectionModel().selectFirst();
        TextField numberF = new TextField(row.number.get());
        TextField balanceF = new TextField();
        GridPane grid = new GridPane(); grid.setHgap(8); grid.setVgap(8);
        grid.addRow(0, new Label("Client:"), clientBox);
        grid.addRow(1, new Label("Number:"), numberF);
        grid.addRow(2, new Label("Balance:"), balanceF);
        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(b-> b==ButtonType.OK? new AccountEditResult(clientBox.getValue(), numberF.getText(), balanceF.getText()) : null);
        AccountEditResult res = dlg.showAndWait().orElse(null); if (res==null) return;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement("UPDATE ACCOUNT SET CLIENT_ID = ?, ACCOUNT_NUMBER = ?, ACCOUNT_BALANCE = ? WHERE ACCOUNT_ID = ?")){
            ps.setInt(1, res.client.userId); ps.setString(2, res.number); ps.setBigDecimal(3, new java.math.BigDecimal(res.balance)); ps.setInt(4, row.accountId.get()); ps.executeUpdate();
        } catch (SQLException e){ showError("Error editing account: "+e.getMessage()); }
        reloadAccounts();
    }

    private void deleteAccount(SimpleIntegerProperty accountId){
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()){
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM TRANSACTION WHERE ACCOUNT_FROM_ID = ? OR ACCOUNT_TO_ID = ?")){
                ps.setInt(1, accountId.get()); ps.setInt(2, accountId.get()); ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM ACCOUNT WHERE ACCOUNT_ID = ?")){
                ps.setInt(1, accountId.get()); ps.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e){ showError("Error deleting account: "+e.getMessage()); }
        reloadAccounts();
    }

    // ----- Transactions -----
    private boolean transactionsColumnsInitialized = false;
    private void setupTransactionsTable(){
        if (transactionsTable==null || transactionsColumnsInitialized) return;
        TableColumn<TransactionRow,String> dateCol = new TableColumn<>("Date"); dateCol.setCellValueFactory(d->d.getValue().date); dateCol.setPrefWidth(160);
        TableColumn<TransactionRow,String> typeCol = new TableColumn<>("Type"); typeCol.setCellValueFactory(d->d.getValue().typeName); typeCol.setPrefWidth(160);
        TableColumn<TransactionRow,String> amountCol = new TableColumn<>("Amount"); amountCol.setCellValueFactory(d->d.getValue().amount); amountCol.setPrefWidth(120);
        TableColumn<TransactionRow,String> fromCol = new TableColumn<>("From"); fromCol.setCellValueFactory(d->d.getValue().fromAcc); fromCol.setPrefWidth(180);
        TableColumn<TransactionRow,String> toCol = new TableColumn<>("To"); toCol.setCellValueFactory(d->d.getValue().toAcc); toCol.setPrefWidth(180);
        TableColumn<TransactionRow,String> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col-> new TableCell<>(){
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            { editBtn.setOnAction(e-> editTransaction(getTableView().getItems().get(getIndex())));
              deleteBtn.setOnAction(e-> deleteTransaction(getTableView().getItems().get(getIndex()).transactionId));
              editBtn.setStyle("-fx-background-color:#3498db;-fx-text-fill:white;");
              deleteBtn.setStyle("-fx-background-color:#e74c3c;-fx-text-fill:white;"); }
            @Override protected void updateItem(String item, boolean empty){ super.updateItem(item, empty); setGraphic(empty? null : new HBox(6, editBtn, deleteBtn)); }
        });
        actionsCol.setPrefWidth(200);
        transactionsTable.getColumns().addAll(dateCol,typeCol,amountCol,fromCol,toCol,actionsCol);
        transactionsColumnsInitialized = true;
    }

    @FXML private void reloadTransactions(){
        setupTransactionsTable();
        ObservableList<TransactionRow> rows = FXCollections.observableArrayList();
        String accFilter = transactionAccountSearchField!=null? transactionAccountSearchField.getText():null;
        String typeFilter = transactionTypeFilter!=null && transactionTypeFilter.getValue()!=null ? transactionTypeFilter.getValue() : null;
        String dir = transactionDirectionFilter!=null && transactionDirectionFilter.getValue()!=null ? transactionDirectionFilter.getValue() : "All";
        java.math.BigDecimal min = parseDecimal(amountMinField!=null? amountMinField.getText():null);
        java.math.BigDecimal max = parseDecimal(amountMaxField!=null? amountMaxField.getText():null);
        java.sql.Date from = dateFromPicker!=null && dateFromPicker.getValue()!=null? java.sql.Date.valueOf(dateFromPicker.getValue()) : null;
        java.sql.Date to = dateToPicker!=null && dateToPicker.getValue()!=null? java.sql.Date.valueOf(dateToPicker.getValue()) : null;

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT t.TRANSACTION_ID, t.TRANSFER_AMOUNT, t.TRANSACTION_TIME, tt.TRANSACTION_TYPE_NAME, ");
        sb.append("af.ACCOUNT_NUMBER AS ACC_FROM, at.ACCOUNT_NUMBER AS ACC_TO ");
        sb.append("FROM TRANSACTION t ");
        sb.append("JOIN TRANSACTION_TYPE tt ON tt.TRANSACTION_TYPE_ID = t.TRANSACTION_TYPE_ID ");
        sb.append("LEFT JOIN ACCOUNT af ON af.ACCOUNT_ID = t.ACCOUNT_FROM_ID ");
        sb.append("LEFT JOIN ACCOUNT at ON at.ACCOUNT_ID = t.ACCOUNT_TO_ID ");
        sb.append("WHERE 1=1 ");
        if (typeFilter != null && !typeFilter.isEmpty()) sb.append("AND tt.TRANSACTION_TYPE_NAME = ? ");
        if (min != null) sb.append("AND ABS(t.TRANSFER_AMOUNT) >= ? ");
        if (max != null) sb.append("AND ABS(t.TRANSFER_AMOUNT) <= ? ");
        if (from != null) sb.append("AND t.TRANSACTION_TIME >= ? ");
        if (to != null) sb.append("AND t.TRANSACTION_TIME <= ? ");
        if (accFilter != null && !accFilter.isEmpty()) sb.append("AND (UPPER(af.ACCOUNT_NUMBER) LIKE ? OR UPPER(at.ACCOUNT_NUMBER) LIKE ?) ");
        // Direction only applies if an account filter is provided (context for direction)
        if (accFilter != null && !accFilter.isEmpty() && dir != null && !dir.equals("All")) {
            if (dir.equals("Incoming")) sb.append("AND UPPER(at.ACCOUNT_NUMBER) LIKE ? ");
            else if (dir.equals("Outgoing")) sb.append("AND UPPER(af.ACCOUNT_NUMBER) LIKE ? ");
        }
        sb.append("ORDER BY t.TRANSACTION_TIME DESC");

        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sb.toString())){
            int idx = 1;
            if (typeFilter != null && !typeFilter.isEmpty()) { ps.setString(idx++, typeFilter); }
            if (min != null) ps.setBigDecimal(idx++, min);
            if (max != null) ps.setBigDecimal(idx++, max);
            if (from != null) ps.setDate(idx++, from);
            if (to != null) ps.setDate(idx++, to);
            String accLike = (accFilter==null || accFilter.isEmpty())? null : accFilter.toUpperCase()+"%";
            if (accFilter != null && !accFilter.isEmpty()) { ps.setString(idx++, accLike); ps.setString(idx++, accLike); }
            if (accFilter != null && !accFilter.isEmpty() && dir != null && !dir.equals("All")) { ps.setString(idx++, accLike); }
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    rows.add(new TransactionRow(
                            rs.getInt("TRANSACTION_ID"),
                            new SimpleStringProperty(String.valueOf(rs.getDate("TRANSACTION_TIME"))),
                            new SimpleStringProperty(rs.getString("TRANSACTION_TYPE_NAME")),
                            new SimpleStringProperty(String.valueOf(rs.getBigDecimal("TRANSFER_AMOUNT"))),
                            new SimpleStringProperty(rs.getString("ACC_FROM")),
                            new SimpleStringProperty(rs.getString("ACC_TO"))
                    ));
                }
            }
        } catch (SQLException e){ showError("Error loading transactions: "+e.getMessage()); }
        transactionsTable.setItems(rows);
    }

    private void editTransaction(TransactionRow row){
        Dialog<TransactionEditResult> dlg = new Dialog<>(); dlg.setTitle("Edit Transaction");
        ComboBox<String> typeBox = new ComboBox<>(loadTransactionTypeNames()); typeBox.getSelectionModel().selectFirst();
        TextField amountF = new TextField(row.amount.get());
        ComboBox<AccountOption> fromBox = new ComboBox<>(loadAccounts());
        ComboBox<AccountOption> toBox = new ComboBox<>(loadAccounts());
        GridPane grid = new GridPane(); grid.setHgap(8); grid.setVgap(8);
        grid.addRow(0, new Label("Type:"), typeBox);
        grid.addRow(1, new Label("Amount:"), amountF);
        grid.addRow(2, new Label("From Account:"), fromBox);
        grid.addRow(3, new Label("To Account:"), toBox);
        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(b-> b==ButtonType.OK? new TransactionEditResult(typeBox.getValue(), amountF.getText(), fromBox.getValue(), toBox.getValue()):null);
        TransactionEditResult res = dlg.showAndWait().orElse(null); if (res==null) return;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()){
            int typeId = findTransactionTypeId(res.typeName);
            try (PreparedStatement ps = conn.prepareStatement("UPDATE TRANSACTION SET TRANSACTION_TYPE_ID = ?, TRANSFER_AMOUNT = ?, ACCOUNT_FROM_ID = ?, ACCOUNT_TO_ID = ? WHERE TRANSACTION_ID = ?")){
                ps.setInt(1,typeId); ps.setBigDecimal(2, new java.math.BigDecimal(res.amount));
                ps.setObject(3, res.fromAcc==null? null : res.fromAcc.accountId);
                ps.setObject(4, res.toAcc==null? null : res.toAcc.accountId);
                ps.setInt(5, row.transactionId.get());
                ps.executeUpdate();
            }
        } catch (SQLException e){ showError("Error editing transaction: "+e.getMessage()); }
        reloadTransactions();
    }

    private void deleteTransaction(SimpleIntegerProperty transactionId){
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM TRANSACTION WHERE TRANSACTION_ID = ?")){
            ps.setInt(1, transactionId.get()); ps.executeUpdate();
        } catch (SQLException e){ showError("Error deleting transaction: "+e.getMessage()); }
        reloadTransactions();
    }

    // ----- Messages -----
    private boolean messagesColumnsInitialized = false;
    private void setupMessagesTable(){
        if (messagesTable==null || messagesColumnsInitialized) return;
        TableColumn<MessageRow,String> fromCol = new TableColumn<>("From"); fromCol.setCellValueFactory(d->d.getValue().fromName); fromCol.setPrefWidth(200);
        TableColumn<MessageRow,String> toCol = new TableColumn<>("To"); toCol.setCellValueFactory(d->d.getValue().toName); toCol.setPrefWidth(200);
        TableColumn<MessageRow,String> textCol = new TableColumn<>("Text"); textCol.setCellValueFactory(d->d.getValue().text); textCol.setPrefWidth(420);
        TableColumn<MessageRow,String> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col-> new TableCell<>(){
            private final Button deleteBtn = new Button("Delete");
            { deleteBtn.setOnAction(e-> deleteMessage(getTableView().getItems().get(getIndex()).messageId)); deleteBtn.setStyle("-fx-background-color:#e74c3c;-fx-text-fill:white;"); }
            @Override protected void updateItem(String item, boolean empty){ super.updateItem(item, empty); setGraphic(empty? null : new HBox(6, deleteBtn)); }
        });
        actionsCol.setPrefWidth(120);
        messagesTable.getColumns().addAll(fromCol,toCol,textCol,actionsCol);
        messagesColumnsInitialized = true;
    }

    @FXML private void reloadMessages(){
        setupMessagesTable();
        ObservableList<MessageRow> rows = FXCollections.observableArrayList();

        String userTerm = messageUserSearchField != null ? messageUserSearchField.getText() : null;
        java.sql.Date from = messageDateFromPicker != null && messageDateFromPicker.getValue() != null ? java.sql.Date.valueOf(messageDateFromPicker.getValue()) : null;
        java.sql.Date to = messageDateToPicker != null && messageDateToPicker.getValue() != null ? java.sql.Date.valueOf(messageDateToPicker.getValue()) : null;

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT m.MESSAGE_ID, ");
        sb.append("uf.NAME AS FROM_NAME, uf.SURNAME AS FROM_SURNAME, ");
        sb.append("ut.NAME AS TO_NAME, ut.SURNAME AS TO_SURNAME, m.MESSAGE_TEXT ");
        sb.append("FROM MESSAGE m ");
        sb.append("LEFT JOIN \"User\" uf ON uf.USER_ID = m.USER_FROM_ID ");
        sb.append("JOIN \"User\" ut ON ut.USER_ID = m.USER_TO_ID ");
        sb.append("WHERE 1=1 ");
        if (from != null) sb.append("AND m.MESSAGE_SENT_AT >= ? ");
        if (to != null) sb.append("AND m.MESSAGE_SENT_AT <= ? ");
        if (userTerm != null && !userTerm.isEmpty()) {
            sb.append("AND (UPPER(NVL(uf.NAME,'')) LIKE ? OR UPPER(NVL(uf.SURNAME,'')) LIKE ? ");
            sb.append("OR UPPER(ut.NAME) LIKE ? OR UPPER(ut.SURNAME) LIKE ?) ");
        }
        sb.append("ORDER BY m.MESSAGE_SENT_AT DESC");

        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sb.toString())){
            int idx = 1;
            if (from != null) ps.setDate(idx++, from);
            if (to != null) ps.setDate(idx++, to);
            if (userTerm != null && !userTerm.isEmpty()) {
                String like = userTerm.toUpperCase()+"%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
            }
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    rows.add(new MessageRow(
                            rs.getInt("MESSAGE_ID"),
                            rs.getString("FROM_NAME")+" "+rs.getString("FROM_SURNAME"),
                            rs.getString("TO_NAME")+" "+rs.getString("TO_SURNAME"),
                            rs.getString("MESSAGE_TEXT")
                    ));
                }
            }
        } catch (SQLException e){ showError("Error loading messages: "+e.getMessage()); }
        messagesTable.setItems(rows);
    }

    private void deleteMessage(SimpleIntegerProperty messageId){
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM MESSAGE WHERE MESSAGE_ID = ?")){
            ps.setInt(1, messageId.get()); ps.executeUpdate();
        } catch (SQLException e){ showError("Error deleting message: "+e.getMessage()); }
        reloadMessages();
    }

    private void editUser(UserRow row) {
        Dialog<UserEditResult> dialog = new Dialog<>();
        dialog.setTitle("Edit User");

        Label nameL = new Label("Name:");
        TextField nameF = new TextField(row.nameProperty.get());
        Label surnameL = new Label("Surname:");
        TextField surnameF = new TextField(row.surnameProperty.get());
        Label roleL = new Label("Role:");
        ComboBox<RoleOption> roleBox = new ComboBox<>();
        roleBox.setItems(loadRoles());
        roleBox.getSelectionModel().select(findRole(roleBox.getItems(), row.roleNameProperty.get()));

        Label cL = new Label("Country:"); TextField cF = new TextField();
        Label sL = new Label("State:");   TextField sF = new TextField();
        Label cityL = new Label("City:");  TextField cityF = new TextField();
        Label streetL = new Label("Street:"); TextField streetF = new TextField();
        Label houseL = new Label("House No:"); TextField houseF = new TextField();
        Label zipL = new Label("ZIP:"); TextField zipF = new TextField();

        GridPane grid = new GridPane(); grid.setHgap(8); grid.setVgap(8);
        grid.addRow(0, nameL, nameF);
        grid.addRow(1, surnameL, surnameF);
        grid.addRow(2, roleL, roleBox);
        grid.addRow(3, cL, cF);
        grid.addRow(4, sL, sF);
        grid.addRow(5, cityL, cityF);
        grid.addRow(6, streetL, streetF);
        grid.addRow(7, houseL, houseF);
        grid.addRow(8, zipL, zipF);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(btn -> btn == ButtonType.OK ?
                new UserEditResult(nameF.getText(), surnameF.getText(), roleBox.getValue(), cF.getText(), sF.getText(), cityF.getText(), streetF.getText(), houseF.getText(), zipF.getText())
                : null);

        UserEditResult res = dialog.showAndWait().orElse(null);
        if (res == null) return;

        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            int newAddrId;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO ADDRESS(ADDRESS_ID, COUNTRY, STATE, CITY, STREET, HOUSE_NUMBER, ZIP_CODE) VALUES(ADDRESS_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?)")) {
                ps.setString(1, res.country);
                ps.setString(2, res.state);
                ps.setString(3, res.city);
                ps.setString(4, res.street);
                ps.setInt(5, Integer.parseInt(res.house));
                ps.setInt(6, Integer.parseInt(res.zip));
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("SELECT ADDRESS_SEQ.currval AS ID FROM dual");
                 ResultSet rs = ps.executeQuery()) {
                rs.next();
                newAddrId = rs.getInt("ID");
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE \"User\" SET NAME = ?, SURNAME = ?, ROLE_ID = ?, ADDRESS_ID = ? WHERE USER_ID = ?")) {
                ps.setString(1, res.name);
                ps.setString(2, res.surname);
                ps.setInt(3, res.role.id);
                ps.setInt(4, newAddrId);
                ps.setInt(5, row.userId.get());
                ps.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            showError("Error updating user: " + e.getMessage());
        }
        reloadUsers();
    }

    private void deleteUser(SimpleIntegerProperty userId) {
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            // Find role
            String roleName = null;
            try (PreparedStatement ps = conn.prepareStatement("SELECT r.ROLE_NAME FROM \"User\" u JOIN ROLE r ON u.ROLE_ID = r.ROLE_ID WHERE u.USER_ID = ?")) {
                ps.setInt(1, userId.get());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) roleName = rs.getString(1);
                }
            }

            // If Teller: cascade delete all clients of this teller
            if (roleName != null && roleName.equalsIgnoreCase("Teller")) {
                // Delete transactions for accounts of clients of this teller
                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM TRANSACTION t WHERE t.ACCOUNT_FROM_ID IN (SELECT a.ACCOUNT_ID FROM ACCOUNT a WHERE a.CLIENT_ID IN (SELECT c.USER_ID FROM CLIENT c WHERE c.TELLER_ID = ?)) "+
                        "OR t.ACCOUNT_TO_ID IN (SELECT a.ACCOUNT_ID FROM ACCOUNT a WHERE a.CLIENT_ID IN (SELECT c.USER_ID FROM CLIENT c WHERE c.TELLER_ID = ?))")) {
                    ps.setInt(1, userId.get());
                    ps.setInt(2, userId.get());
                    ps.executeUpdate();
                }
                // Delete accounts of clients of this teller
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM ACCOUNT WHERE CLIENT_ID IN (SELECT USER_ID FROM CLIENT WHERE TELLER_ID = ?)");) {
                    ps.setInt(1, userId.get());
                    ps.executeUpdate();
                }
                // Delete client rows
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM CLIENT WHERE TELLER_ID = ?");) {
                    ps.setInt(1, userId.get());
                    ps.executeUpdate();
                }
                // Delete teller row
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM TELLER WHERE USER_ID = ?");) {
                    ps.setInt(1, userId.get());
                    ps.executeUpdate();
                }
            }

            // If Client: cascade delete their accounts and transactions
            if (roleName != null && roleName.equalsIgnoreCase("Client")) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM TRANSACTION t WHERE t.ACCOUNT_FROM_ID IN (SELECT a.ACCOUNT_ID FROM ACCOUNT a WHERE a.CLIENT_ID = ?) " +
                                "OR t.ACCOUNT_TO_ID IN (SELECT a.ACCOUNT_ID FROM ACCOUNT a WHERE a.CLIENT_ID = ?)")) {
                    ps.setInt(1, userId.get());
                    ps.setInt(2, userId.get());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM ACCOUNT WHERE CLIENT_ID = ?")) {
                    ps.setInt(1, userId.get());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM CLIENT WHERE USER_ID = ?")) {
                    ps.setInt(1, userId.get());
                    ps.executeUpdate();
                }
            }

            // Delete messages to/from user
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM MESSAGE WHERE USER_FROM_ID = ? OR USER_TO_ID = ?")) {
                ps.setInt(1, userId.get());
                ps.setInt(2, userId.get());
                ps.executeUpdate();
            }
            // Delete documents
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM DOCUMENT WHERE USER_ID = ?")) {
                ps.setInt(1, userId.get());
                ps.executeUpdate();
            }
            // Delete login records
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM LOGIN_RECORD WHERE USER_ID = ?")) {
                ps.setInt(1, userId.get());
                ps.executeUpdate();
            }
            // Delete audit logs
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM AUDIT_LOG WHERE USER_ID = ?")) {
                ps.setInt(1, userId.get());
                ps.executeUpdate();
            }

            // Finally, delete user itself
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM \"User\" WHERE USER_ID = ?")) {
                ps.setInt(1, userId.get());
                ps.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            showError("Error deleting user cascaded: " + e.getMessage());
        }
        reloadUsers();
    }

    private ObservableList<RoleOption> loadRoles() {
        ObservableList<RoleOption> roles = FXCollections.observableArrayList();
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT ROLE_ID, ROLE_NAME FROM ROLE ORDER BY ROLE_NAME");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roles.add(new RoleOption(rs.getInt("ROLE_ID"), rs.getString("ROLE_NAME")));
            }
        } catch (SQLException e) {
            showError("Error loading roles: " + e.getMessage());
        }
        return roles;
    }

    private ObservableList<String> loadTransactionTypeNames(){
        ObservableList<String> list = FXCollections.observableArrayList();
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT TRANSACTION_TYPE_NAME FROM TRANSACTION_TYPE ORDER BY TRANSACTION_TYPE_NAME"); ResultSet rs = ps.executeQuery()){
            while (rs.next()) list.add(rs.getString(1));
        } catch (SQLException ignored) {}
        return list;
    }

    private int findTransactionTypeId(String name) throws SQLException {
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT TRANSACTION_TYPE_ID FROM TRANSACTION_TYPE WHERE TRANSACTION_TYPE_NAME = ?")){
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Transaction type not found: "+name);
    }

    private ObservableList<UserOption> loadClients(){
        ObservableList<UserOption> list = FXCollections.observableArrayList();
        String sql = "SELECT u.USER_ID, u.NAME, u.SURNAME FROM CLIENT c JOIN \"User\" u ON u.USER_ID = c.USER_ID ORDER BY u.SURNAME,u.NAME";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while (rs.next()) list.add(new UserOption(rs.getInt(1), rs.getString(2)+" "+rs.getString(3)));
        } catch (SQLException ignored) {}
        return list;
    }

    private ObservableList<UserOption> loadTellers(){
        ObservableList<UserOption> list = FXCollections.observableArrayList();
        String sql = "SELECT u.USER_ID, u.NAME, u.SURNAME FROM TELLER t JOIN \"User\" u ON u.USER_ID = t.USER_ID ORDER BY u.SURNAME,u.NAME";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while (rs.next()) list.add(new UserOption(rs.getInt(1), rs.getString(2)+" "+rs.getString(3)));
        } catch (SQLException ignored) {}
        return list;
    }

    private ObservableList<BranchOption> loadBranches(){
        ObservableList<BranchOption> list = FXCollections.observableArrayList();
        String sql = "SELECT BRANCH_ID, BRANCH_NAME FROM BRANCH ORDER BY BRANCH_NAME";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while (rs.next()) list.add(new BranchOption(rs.getInt(1), rs.getString(2)));
        } catch (SQLException ignored) {}
        return list;
    }

    private ObservableList<AccountOption> loadAccounts(){
        ObservableList<AccountOption> list = FXCollections.observableArrayList();
        String sql = "SELECT ACCOUNT_ID, ACCOUNT_NUMBER FROM ACCOUNT ORDER BY ACCOUNT_NUMBER";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while (rs.next()) list.add(new AccountOption(rs.getInt(1), rs.getString(2)));
        } catch (SQLException ignored) {}
        return list;
    }

    private RoleOption findRole(ObservableList<RoleOption> list, String roleName) {
        for (RoleOption r : list) if (r.name.equals(roleName)) return r;
        return list.isEmpty() ? null : list.get(0);
    }

    // Models
    public static class BranchRow {
        final SimpleIntegerProperty branchId = new SimpleIntegerProperty();
        final SimpleStringProperty branchNameProperty = new SimpleStringProperty();
        final SimpleStringProperty addressTextProperty = new SimpleStringProperty();
        public BranchRow(int id, String name, String addressText) {
            branchId.set(id);
            branchNameProperty.set(name);
            addressTextProperty.set(addressText);
        }
    }

    public static class UserRow {
        final SimpleIntegerProperty userId = new SimpleIntegerProperty();
        final SimpleStringProperty nameProperty = new SimpleStringProperty();
        final SimpleStringProperty surnameProperty = new SimpleStringProperty();
        final SimpleStringProperty roleNameProperty = new SimpleStringProperty();
        final SimpleStringProperty addressTextProperty = new SimpleStringProperty();
        public UserRow(int id, String name, String surname, String role, String address) {
            userId.set(id);
            nameProperty.set(name);
            surnameProperty.set(surname);
            roleNameProperty.set(role);
            addressTextProperty.set(address);
        }
    }

    public static class ClientRow {
        final SimpleIntegerProperty userId = new SimpleIntegerProperty();
        final SimpleStringProperty name = new SimpleStringProperty();
        final SimpleStringProperty surname = new SimpleStringProperty();
        final SimpleStringProperty contact = new SimpleStringProperty();
        final SimpleStringProperty tellerName = new SimpleStringProperty();
        final SimpleStringProperty address = new SimpleStringProperty();
        public ClientRow(int id, String n, String s, String contact, String tellerName, String address){ this.userId.set(id); this.name.set(n); this.surname.set(s); this.contact.set(contact); this.tellerName.set(tellerName); this.address.set(address);}    }

    public static class TellerRow {
        final SimpleIntegerProperty userId = new SimpleIntegerProperty();
        final SimpleStringProperty name = new SimpleStringProperty();
        final SimpleStringProperty surname = new SimpleStringProperty();
        final SimpleStringProperty contact = new SimpleStringProperty();
        final SimpleStringProperty branchName = new SimpleStringProperty();
        final SimpleStringProperty address = new SimpleStringProperty();
        public TellerRow(int id, String n, String s, String contact, String branch, String address){ this.userId.set(id); this.name.set(n); this.surname.set(s); this.contact.set(contact); this.branchName.set(branch); this.address.set(address);}    }

    public static class AccountRow {
        final SimpleIntegerProperty accountId = new SimpleIntegerProperty();
        final SimpleStringProperty number = new SimpleStringProperty();
        final SimpleStringProperty owner = new SimpleStringProperty();
        final SimpleStringProperty balance = new SimpleStringProperty();
        public AccountRow(int id, String number, String owner, String balance){ this.accountId.set(id); this.number.set(number); this.owner.set(owner); this.balance.set(balance);}    }

    public static class TransactionRow {
        final SimpleIntegerProperty transactionId = new SimpleIntegerProperty();
        final SimpleStringProperty date; final SimpleStringProperty typeName; final SimpleStringProperty amount; final SimpleStringProperty fromAcc; final SimpleStringProperty toAcc;
        public TransactionRow(int id, SimpleStringProperty date, SimpleStringProperty typeName, SimpleStringProperty amount, SimpleStringProperty fromAcc, SimpleStringProperty toAcc){ this.transactionId.set(id); this.date=date; this.typeName=typeName; this.amount=amount; this.fromAcc=fromAcc; this.toAcc=toAcc; }
    }

    public static class MessageRow {
        final SimpleIntegerProperty messageId = new SimpleIntegerProperty();
        final SimpleStringProperty fromName = new SimpleStringProperty();
        final SimpleStringProperty toName = new SimpleStringProperty();
        final SimpleStringProperty text = new SimpleStringProperty();
        public MessageRow(int id, String from, String to, String text){ this.messageId.set(id); this.fromName.set(from); this.toName.set(to); this.text.set(text);}    }

    private static class RoleOption {
        final int id; final String name;
        RoleOption(int id, String name) { this.id = id; this.name = name; }
        @Override public String toString() { return name; }
    }

    private static class UserOption { final int userId; final String display; UserOption(int id, String d){ this.userId = id; this.display = d; } @Override public String toString(){ return display; } }
    private static class BranchOption { final int branchId; final String display; BranchOption(int id, String d){ this.branchId = id; this.display=d;} @Override public String toString(){ return display; }}
    private static class AccountOption { final int accountId; final String number; AccountOption(int id, String n){ this.accountId=id; this.number=n;} @Override public String toString(){ return number; }}

    private static class BranchEditResult {
        final String branchName, country, state, city, street, houseNumber, zip;
        BranchEditResult(String branchName, String country, String state, String city, String street, String houseNumber, String zip) {
            this.branchName = branchName; this.country = country; this.state = state; this.city = city; this.street = street; this.houseNumber = houseNumber; this.zip = zip;
        }
    }

    private static class UserEditResult {
        final String name, surname; final RoleOption role;
        final String country, state, city, street, house, zip;
        UserEditResult(String name, String surname, RoleOption role, String country, String state, String city, String street, String house, String zip) {
            this.name = name; this.surname = surname; this.role = role;
            this.country = country; this.state = state; this.city = city; this.street = street; this.house = house; this.zip = zip;
        }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private int parseIntSafe(String s){ try { return Integer.parseInt(s); } catch (Exception e){ return 0; } }
    private java.math.BigDecimal parseDecimal(String s){ try { return (s==null||s.isEmpty())? null : new java.math.BigDecimal(s); } catch (Exception e){ return null; } }

    // ===== Add Handlers =====
    @FXML private void onAddBranch(){
        Dialog<BranchEditResult> dlg = new Dialog<>(); dlg.setTitle("Add Branch");
        TextField nameF = new TextField(); TextField cF=new TextField(); TextField sF=new TextField(); TextField cityF=new TextField(); TextField streetF=new TextField(); TextField houseF=new TextField(); TextField zipF=new TextField();
        GridPane grid=new GridPane(); grid.setHgap(8); grid.setVgap(8);
        grid.addRow(0,new Label("Branch name:"), nameF);
        grid.addRow(1,new Label("Country:"), cF);
        grid.addRow(2,new Label("State:"), sF);
        grid.addRow(3,new Label("City:"), cityF);
        grid.addRow(4,new Label("Street:"), streetF);
        grid.addRow(5,new Label("House No:"), houseF);
        grid.addRow(6,new Label("ZIP:"), zipF);
        dlg.getDialogPane().setContent(grid); dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(b-> b==ButtonType.OK? new BranchEditResult(nameF.getText(), cF.getText(), sF.getText(), cityF.getText(), streetF.getText(), houseF.getText(), zipF.getText()):null);
        BranchEditResult res = dlg.showAndWait().orElse(null); if (res==null) return;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()){
            conn.setAutoCommit(false);
            int addrId;
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO ADDRESS(ADDRESS_ID, COUNTRY, STATE, CITY, STREET, HOUSE_NUMBER, ZIP_CODE) VALUES(ADDRESS_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?)")){
                ps.setString(1,res.country); ps.setString(2,res.state); ps.setString(3,res.city); ps.setString(4,res.street); ps.setInt(5, parseIntSafe(res.houseNumber)); ps.setInt(6, parseIntSafe(res.zip)); ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("SELECT ADDRESS_SEQ.CURRVAL AS ID FROM dual"); ResultSet rs = ps.executeQuery()){ rs.next(); addrId = rs.getInt("ID"); }
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO BRANCH(BRANCH_ID, BRANCH_NAME, ADDRESS_ID) VALUES(BRANCH_SEQ.NEXTVAL, ?, ?)")){
                ps.setString(1, res.branchName); ps.setInt(2, addrId); ps.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e){ showError("Error adding branch: "+e.getMessage()); }
        reloadBranches();
    }

    @FXML private void onAddUser(){
        Dialog<UserAddResult> dlg = new Dialog<>(); dlg.setTitle("Add User");
        TextField nameF=new TextField(); TextField surnameF=new TextField(); PasswordField passF=new PasswordField(); CheckBox activeC=new CheckBox("Active"); activeC.setSelected(true);
        ComboBox<RoleOption> roleBox = new ComboBox<>(loadRoles()); roleBox.getSelectionModel().selectFirst();
        TextField countryF=new TextField(); TextField stateF=new TextField(); TextField cityF=new TextField(); TextField streetF=new TextField(); TextField houseF=new TextField(); TextField zipF=new TextField();
        GridPane grid=new GridPane(); grid.setHgap(8); grid.setVgap(8);
        grid.addRow(0,new Label("Name:"), nameF);
        grid.addRow(1,new Label("Surname:"), surnameF);
        grid.addRow(2,new Label("Password:"), passF);
        grid.addRow(3,new Label("Role:"), roleBox);
        grid.addRow(4,new Label("Active:"), activeC);
        grid.addRow(5,new Label("Country:"), countryF);
        grid.addRow(6,new Label("State:"), stateF);
        grid.addRow(7,new Label("City:"), cityF);
        grid.addRow(8,new Label("Street:"), streetF);
        grid.addRow(9,new Label("House No:"), houseF);
        grid.addRow(10,new Label("ZIP:"), zipF);
        dlg.getDialogPane().setContent(grid); dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(b-> b==ButtonType.OK? new UserAddResult(nameF.getText(), surnameF.getText(), passF.getText(), activeC.isSelected(), roleBox.getValue(), countryF.getText(), stateF.getText(), cityF.getText(), streetF.getText(), houseF.getText(), zipF.getText()) : null);
        UserAddResult res = dlg.showAndWait().orElse(null); if (res==null) return;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()){
            conn.setAutoCommit(false);
            int addrId;
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO ADDRESS(ADDRESS_ID, COUNTRY, STATE, CITY, STREET, HOUSE_NUMBER, ZIP_CODE) VALUES(ADDRESS_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?)")){
                ps.setString(1,res.country); ps.setString(2,res.state); ps.setString(3,res.city); ps.setString(4,res.street); ps.setInt(5, parseIntSafe(res.house)); ps.setInt(6, parseIntSafe(res.zip)); ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("SELECT ADDRESS_SEQ.CURRVAL AS ID FROM dual"); ResultSet rs = ps.executeQuery()){ rs.next(); addrId = rs.getInt("ID"); }
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO \"User\"(USER_ID, NAME, SURNAME, PASSWORD, ACTIVE, ROLE_ID, ADDRESS_ID) VALUES(USER_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?)")){
                ps.setString(1,res.name); ps.setString(2,res.surname); ps.setString(3,res.password); ps.setString(4, res.active? "Y":"N"); ps.setInt(5, res.role.id); ps.setInt(6, addrId);
                ps.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e){ showError("Error adding user: "+e.getMessage()); }
        reloadUsers();
    }

    @FXML private void onAddClient(){
        Dialog<ClientAddResult> dlg = new Dialog<>(); dlg.setTitle("Add Client");
        TextField nameF=new TextField(); TextField surnameF=new TextField(); PasswordField passF=new PasswordField();
        TextField birthF=new TextField(); TextField phoneF=new TextField(); TextField emailF=new TextField();
        ComboBox<UserOption> tellerBox = new ComboBox<>(loadTellers()); tellerBox.getSelectionModel().selectFirst();
        TextField countryF=new TextField(); TextField stateF=new TextField(); TextField cityF=new TextField(); TextField streetF=new TextField(); TextField houseF=new TextField(); TextField zipF=new TextField();
        GridPane g=new GridPane(); g.setHgap(8); g.setVgap(8);
        g.addRow(0,new Label("Name:"), nameF); g.addRow(1,new Label("Surname:"), surnameF); g.addRow(2,new Label("Password:"), passF);
        g.addRow(3,new Label("Birth number:"), birthF); g.addRow(4,new Label("Phone:"), phoneF); g.addRow(5,new Label("Email:"), emailF);
        g.addRow(6,new Label("Teller:"), tellerBox);
        g.addRow(7,new Label("Country:"), countryF); g.addRow(8,new Label("State:"), stateF); g.addRow(9,new Label("City:"), cityF);
        g.addRow(10,new Label("Street:"), streetF); g.addRow(11,new Label("House No:"), houseF); g.addRow(12,new Label("ZIP:"), zipF);
        dlg.getDialogPane().setContent(g); dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(b-> b==ButtonType.OK? new ClientAddResult(nameF.getText(), surnameF.getText(), passF.getText(), birthF.getText(), phoneF.getText(), emailF.getText(), tellerBox.getValue(), countryF.getText(), stateF.getText(), cityF.getText(), streetF.getText(), houseF.getText(), zipF.getText()) : null);
        ClientAddResult res = dlg.showAndWait().orElse(null); if (res==null) return;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()){
            conn.setAutoCommit(false);
            int addrId;
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO ADDRESS(ADDRESS_ID, COUNTRY, STATE, CITY, STREET, HOUSE_NUMBER, ZIP_CODE) VALUES(ADDRESS_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?)")){
                ps.setString(1,res.country); ps.setString(2,res.state); ps.setString(3,res.city); ps.setString(4,res.street); ps.setInt(5, parseIntSafe(res.house)); ps.setInt(6, parseIntSafe(res.zip)); ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("SELECT ADDRESS_SEQ.CURRVAL AS ID FROM dual"); ResultSet rs = ps.executeQuery()){ rs.next(); addrId = rs.getInt("ID"); }
            int newUserId;
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO \"User\"(USER_ID, NAME, SURNAME, PASSWORD, ACTIVE, ROLE_ID, ADDRESS_ID) VALUES(USER_SEQ.NEXTVAL, ?, ?, ?, 'Y', (SELECT ROLE_ID FROM ROLE WHERE ROLE_NAME='Client'), ?)");){
                ps.setString(1,res.name); ps.setString(2,res.surname); ps.setString(3,res.password); ps.setInt(4, addrId); ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("SELECT USER_SEQ.CURRVAL AS ID FROM dual"); ResultSet rs = ps.executeQuery()){ rs.next(); newUserId = rs.getInt("ID"); }
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO CLIENT(USER_ID, BIRTH_NUMBER, PHONE_NUMBER, EMAIL_ADDRESS, TELLER_ID) VALUES(?, ?, ?, ?, ?)")){
                ps.setInt(1,newUserId); ps.setString(2,res.birth); ps.setString(3,res.phone); ps.setString(4,res.email); ps.setInt(5, res.teller.userId); ps.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e){ showError("Error adding client: "+e.getMessage()); }
        reloadClients();
    }

    @FXML private void onAddTeller(){
        Dialog<TellerAddResult> dlg = new Dialog<>(); dlg.setTitle("Add Teller");
        TextField nameF=new TextField(); TextField surnameF=new TextField(); PasswordField passF=new PasswordField();
        TextField phoneF=new TextField(); TextField emailF=new TextField(); ComboBox<BranchOption> branchBox = new ComboBox<>(loadBranches()); branchBox.getSelectionModel().selectFirst();
        TextField countryF=new TextField(); TextField stateF=new TextField(); TextField cityF=new TextField(); TextField streetF=new TextField(); TextField houseF=new TextField(); TextField zipF=new TextField();
        GridPane g=new GridPane(); g.setHgap(8); g.setVgap(8);
        g.addRow(0,new Label("Name:"), nameF); g.addRow(1,new Label("Surname:"), surnameF); g.addRow(2,new Label("Password:"), passF);
        g.addRow(3,new Label("Work phone:"), phoneF); g.addRow(4,new Label("Work email:"), emailF); g.addRow(5,new Label("Branch:"), branchBox);
        g.addRow(6,new Label("Country:"), countryF); g.addRow(7,new Label("State:"), stateF); g.addRow(8,new Label("City:"), cityF);
        g.addRow(9,new Label("Street:"), streetF); g.addRow(10,new Label("House No:"), houseF); g.addRow(11,new Label("ZIP:"), zipF);
        dlg.getDialogPane().setContent(g); dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(b-> b==ButtonType.OK? new TellerAddResult(nameF.getText(), surnameF.getText(), passF.getText(), phoneF.getText(), emailF.getText(), branchBox.getValue(), countryF.getText(), stateF.getText(), cityF.getText(), streetF.getText(), houseF.getText(), zipF.getText()) : null);
        TellerAddResult res = dlg.showAndWait().orElse(null); if (res==null) return;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()){
            conn.setAutoCommit(false);
            int addrId;
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO ADDRESS(ADDRESS_ID, COUNTRY, STATE, CITY, STREET, HOUSE_NUMBER, ZIP_CODE) VALUES(ADDRESS_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?)")){
                ps.setString(1,res.country); ps.setString(2,res.state); ps.setString(3,res.city); ps.setString(4,res.street); ps.setInt(5, parseIntSafe(res.house)); ps.setInt(6, parseIntSafe(res.zip)); ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("SELECT ADDRESS_SEQ.CURRVAL AS ID FROM dual"); ResultSet rs = ps.executeQuery()){ rs.next(); addrId = rs.getInt("ID"); }
            int newUserId;
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO \"User\"(USER_ID, NAME, SURNAME, PASSWORD, ACTIVE, ROLE_ID, ADDRESS_ID) VALUES(USER_SEQ.NEXTVAL, ?, ?, ?, 'Y', (SELECT ROLE_ID FROM ROLE WHERE ROLE_NAME='Teller'), ?)");){
                ps.setString(1,res.name); ps.setString(2,res.surname); ps.setString(3,res.password); ps.setInt(4, addrId); ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("SELECT USER_SEQ.CURRVAL AS ID FROM dual"); ResultSet rs = ps.executeQuery()){ rs.next(); newUserId = rs.getInt("ID"); }
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO TELLER(USER_ID, WORK_PHONE_NUMBER, WORK_EMAIL_ADDRESS, BRANCH_ID) VALUES(?, ?, ?, ?)")){
                ps.setInt(1,newUserId); ps.setString(2,res.phone); ps.setString(3,res.email); ps.setInt(4,res.branch.branchId); ps.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e){ showError("Error adding teller: "+e.getMessage()); }
        reloadTellers();
    }

    @FXML private void onAddAccount(){
        Dialog<AccountEditResult> dlg = new Dialog<>(); dlg.setTitle("Add Account");
        ComboBox<UserOption> clientBox = new ComboBox<>(loadClients()); clientBox.getSelectionModel().selectFirst();
        TextField numberF = new TextField(); TextField balanceF = new TextField();
        GridPane grid = new GridPane(); grid.setHgap(8); grid.setVgap(8);
        grid.addRow(0,new Label("Client:"), clientBox); grid.addRow(1,new Label("Number:"), numberF); grid.addRow(2,new Label("Balance:"), balanceF);
        dlg.getDialogPane().setContent(grid); dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(b-> b==ButtonType.OK? new AccountEditResult(clientBox.getValue(), numberF.getText(), balanceF.getText()) : null);
        AccountEditResult res = dlg.showAndWait().orElse(null); if (res==null) return;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement("INSERT INTO ACCOUNT(ACCOUNT_ID, ACCOUNT_NUMBER, ACCOUNT_BALANCE, ACCOUNT_ACTIVE, CLIENT_ID) VALUES(ACCOUNT_SEQ.NEXTVAL, ?, ?, 'Y', ?)")){
            ps.setString(1,res.number); ps.setBigDecimal(2, new java.math.BigDecimal(res.balance)); ps.setInt(3, res.client.userId); ps.executeUpdate();
        } catch (SQLException e){ showError("Error adding account: "+e.getMessage()); }
        reloadAccounts();
    }

    @FXML private void onAddTransaction(){
        Dialog<TransactionEditResult> dlg = new Dialog<>(); dlg.setTitle("Add Transaction");
        ComboBox<String> typeBox = new ComboBox<>(loadTransactionTypeNames()); typeBox.getSelectionModel().selectFirst();
        TextField amountF = new TextField(); ComboBox<AccountOption> fromBox = new ComboBox<>(loadAccounts()); ComboBox<AccountOption> toBox = new ComboBox<>(loadAccounts());
        GridPane grid=new GridPane(); grid.setHgap(8); grid.setVgap(8);
        grid.addRow(0,new Label("Type:"), typeBox); grid.addRow(1,new Label("Amount:"), amountF); grid.addRow(2,new Label("From Account:"), fromBox); grid.addRow(3,new Label("To Account:"), toBox);
        dlg.getDialogPane().setContent(grid); dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(b-> b==ButtonType.OK? new TransactionEditResult(typeBox.getValue(), amountF.getText(), fromBox.getValue(), toBox.getValue()) : null);
        TransactionEditResult res = dlg.showAndWait().orElse(null); if (res==null) return;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()){
            int typeId = findTransactionTypeId(res.typeName);
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO TRANSACTION(TRANSACTION_ID, TRANSFER_AMOUNT, TRANSACTION_TYPE_ID, TRANSACTION_TIME, ACCOUNT_FROM_ID, ACCOUNT_TO_ID) VALUES(TRANSACTION_SEQ.NEXTVAL, ?, ?, SYSDATE, ?, ?)")){
                ps.setBigDecimal(1, new java.math.BigDecimal(res.amount)); ps.setInt(2, typeId);
                if (res.fromAcc==null) ps.setNull(3, java.sql.Types.INTEGER); else ps.setInt(3, res.fromAcc.accountId);
                if (res.toAcc==null) ps.setNull(4, java.sql.Types.INTEGER); else ps.setInt(4, res.toAcc.accountId);
                ps.executeUpdate();
            }
        } catch (SQLException e){ showError("Error adding transaction: "+e.getMessage()); }
        reloadTransactions();
    }

    @FXML private void onAddMessage(){
        Dialog<MessageAddResult> dlg = new Dialog<>(); dlg.setTitle("Add Message");
        ComboBox<UserOption> fromBox = new ComboBox<>(loadClients()); // Allow client senders; could be any user
        ComboBox<UserOption> toBox = new ComboBox<>(loadTellers()); // Or any user; simplification
        TextArea textF = new TextArea(); textF.setPrefRowCount(4);
        GridPane g=new GridPane(); g.setHgap(8); g.setVgap(8);
        g.addRow(0, new Label("From:"), fromBox); g.addRow(1, new Label("To:"), toBox); g.addRow(2, new Label("Text:"), textF);
        dlg.getDialogPane().setContent(g); dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(b-> b==ButtonType.OK? new MessageAddResult(fromBox.getValue(), toBox.getValue(), textF.getText()) : null);
        MessageAddResult res = dlg.showAndWait().orElse(null); if (res==null) return;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement("INSERT INTO MESSAGE(MESSAGE_ID, MESSAGE_TEXT, MESSAGE_READ, USER_FROM_ID, MESSAGE_SENT_AT, USER_TO_ID) VALUES(MESSAGE_SEQ.NEXTVAL, ?, 'N', ?, SYSDATE, ?)")){
            ps.setString(1,res.text); ps.setObject(2, res.from==null? null : res.from.userId); ps.setInt(3, res.to.userId); ps.executeUpdate();
        } catch (SQLException e){ showError("Error adding message: "+e.getMessage()); }
        reloadMessages();
    }

    // DTOs for dialog results
    private static class ClientEditResult { final UserOption teller; final String country,state,city,street,house,zip; ClientEditResult(UserOption t,String c1,String c2,String c3,String st,String h,String z){ teller=t; country=c1; state=c2; city=c3; street=st; house=h; zip=z; } }
    private static class TellerEditResult { final BranchOption branch; final String country,state,city,street,house,zip; TellerEditResult(BranchOption b,String c1,String c2,String c3,String st,String h,String z){ branch=b; country=c1; state=c2; city=c3; street=st; house=h; zip=z; } }
    private static class UserAddResult { final String name,surname,password; final boolean active; final RoleOption role; final String country,state,city,street,house,zip; UserAddResult(String n,String s,String p,boolean a,RoleOption r,String c1,String c2,String c3,String st,String h,String z){name=n;surname=s;password=p;active=a;role=r;country=c1;state=c2;city=c3;street=st;house=h;zip=z;} }
    private static class ClientAddResult { final String name,surname,password,birth,phone,email; final UserOption teller; final String country,state,city,street,house,zip; ClientAddResult(String n,String s,String p,String b,String ph,String e,UserOption t,String c1,String c2,String c3,String st,String h,String z){name=n;surname=s;password=p;birth=b;phone=ph;email=e;teller=t;country=c1;state=c2;city=c3;street=st;house=h;zip=z;} }
    private static class TellerAddResult { final String name,surname,password,phone,email; final BranchOption branch; final String country,state,city,street,house,zip; TellerAddResult(String n,String s,String p,String ph,String e,BranchOption b,String c1,String c2,String c3,String st,String h,String z){name=n;surname=s;password=p;phone=ph;email=e;branch=b;country=c1;state=c2;city=c3;street=st;house=h;zip=z;} }
    private static class AccountEditResult { final UserOption client; final String number,balance; AccountEditResult(UserOption c,String n,String b){client=c;number=n;balance=b;} }
    private static class TransactionEditResult { final String typeName; final String amount; final AccountOption fromAcc,toAcc; TransactionEditResult(String t,String a,AccountOption f,AccountOption to){typeName=t;amount=a;fromAcc=f;toAcc=to;} }
    private static class MessageAddResult { final UserOption from,to; final String text; MessageAddResult(UserOption f,UserOption t,String tx){from=f;to=t;text=tx;} }
}
