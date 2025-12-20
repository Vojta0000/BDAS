package upce.javafx;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
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

    /**
     * Sets the application view controller reference.
     * @param appViewController The parent application view controller.
     */
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
    @FXML private VBox rolesSection;
    @FXML private VBox addressesSection;
    @FXML private VBox transactionTypesSection;
    @FXML private VBox documentsSection;

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

    // Roles UI
    @FXML private TableView<RoleRow> rolesTable;

    // Addresses UI
    @FXML private TableView<AddressRow> addressesTable;

    // Transaction types UI
    @FXML private TableView<TransactionTypeRow> transactionTypesTable;

    // Logins & Audits UI
    @FXML private TableView<LoginRow> loginsTable;
    @FXML private TableView<AuditRow> auditsTable;

    // Documents UI
    @FXML private TableView<DocumentRow> documentsTable;

    // --- Newly added filter inputs (Logins, Audits, Documents) ---
    // Logins filters
    @FXML private TextField  loginUserSearchField;
    @FXML private TextField  loginIpSearchField;
    @FXML private DatePicker loginDateFromPicker;
    @FXML private DatePicker loginDateToPicker;

    // Audits filters
    @FXML private TextField  auditUserSearchField;
    @FXML private TextField  auditChangeSearchField;
    @FXML private DatePicker auditDateFromPicker;
    @FXML private DatePicker auditDateToPicker;

    // Documents filters
    @FXML private TextField documentUserSearchField;
    @FXML private TextField documentExtensionSearchField;

    /**
     * Initializes the controller, sets up initial visibility of UI components and tables.
     */
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
        setupRolesTable();
        setupAddressesTable();
        setupTransactionTypesTable();
        setupLoginsTable();
        setupAuditsTable();
        setupDocumentsTable();

        if (transactionDirectionFilter != null) {
            transactionDirectionFilter.setItems(FXCollections.observableArrayList("All", "Incoming", "Outgoing"));
            transactionDirectionFilter.getSelectionModel().selectFirst();
        }
        if (transactionTypeFilter != null) {
            transactionTypeFilter.setItems(loadTransactionTypeNames());
        }
    }

    /**
     * Refreshes the view when the admin login is completed.
     */
    public void onLoginLoaded() {
        // Called from AppViewController after successful admin login
        reloadBranches();
    }

    // Navigation
    /**
     * Navigates to the branches section and reloads the data.
     */
    @FXML private void showBranches() { showOnly(branchesSection); reloadBranches(); }
    /**
     * Navigates to the users section and reloads the data.
     */
    @FXML private void showUsers() { showOnly(usersSection); reloadUsers(); }
    /**
     * Navigates to the clients section and reloads the data.
     */
    @FXML private void showClients() { showOnly(clientsSection); reloadClients(); }
    /**
     * Navigates to the tellers section and reloads the data.
     */
    @FXML private void showTellers() { showOnly(tellersSection); reloadTellers(); }
    /**
     * Navigates to the accounts section and reloads the data.
     */
    @FXML private void showAccounts() { showOnly(accountsSection); reloadAccounts(); }
    /**
     * Navigates to the transactions section and reloads the data.
     */
    @FXML private void showTransactions() { showOnly(transactionsSection); reloadTransactions(); }
    /**
     * Navigates to the logins section and reloads the data.
     */
    @FXML private void showLogins() { showOnly(loginsSection); reloadLogins(); }
    /**
     * Navigates to the messages section and reloads the data.
     */
    @FXML private void showMessages() { showOnly(messagesSection); reloadMessages(); }
    /**
     * Navigates to the audits section and reloads the data.
     */
    @FXML private void showAudits() { showOnly(auditsSection); reloadAudits(); }
    /**
     * Navigates to the roles section and reloads the data.
     */
    @FXML private void showRoles() { showOnly(rolesSection); reloadRoles(); }
    /**
     * Navigates to the addresses section and reloads the data.
     */
    @FXML private void showAddresses() { showOnly(addressesSection); reloadAddresses(); }
    /**
     * Navigates to the transaction types section and reloads the data.
     */
    @FXML private void showTransactionTypes() { showOnly(transactionTypesSection); reloadTransactionTypes(); }
    /**
     * Navigates to the documents section and reloads the data.
     */
    @FXML private void showDocuments() { showOnly(documentsSection); reloadDocuments(); }

    /**
     * Handles the logout process for the admin.
     */
    @FXML private void onLogout() {
        if (appViewController != null) {
            appViewController.logout();
        }
    }

    /**
     * Shows only the specified section node and hides all others.
     * @param toShow The section node to display.
     */
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
        setVisibleManaged(rolesSection, toShow == rolesSection);
        setVisibleManaged(addressesSection, toShow == addressesSection);
        setVisibleManaged(transactionTypesSection, toShow == transactionTypesSection);
        setVisibleManaged(documentsSection, toShow == documentsSection);
    }

    /**
     * Sets the visibility and managed state of a node.
     * @param node The node to modify.
     * @param value True to make it visible and managed.
     */
    private void setVisibleManaged(Node node, boolean value) {
        if (node != null) {
            node.setVisible(value);
            node.setManaged(value);
        }
    }

    // ----- Roles -----
    private boolean rolesColumnsInitialized = false;
    /**
     * Sets up the columns for the roles table.
     */
    private void setupRolesTable() {
        if (rolesTable == null || rolesColumnsInitialized) return;
        TableColumn<RoleRow, String> nameCol = new TableColumn<>("Role");
        nameCol.setCellValueFactory(d -> d.getValue().name);
        nameCol.setPrefWidth(200);

        TableColumn<RoleRow, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(d -> d.getValue().description);
        descCol.setPrefWidth(420);

        TableColumn<RoleRow, String> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            {
                editBtn.setOnAction(e -> editRole(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(e -> deleteRole(getTableView().getItems().get(getIndex()).roleId));
                editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(6, editBtn, deleteBtn));
            }
        });
        actionsCol.setPrefWidth(200);

        rolesTable.getColumns().addAll(nameCol, descCol, actionsCol);
        rolesColumnsInitialized = true;
    }

    /**
     * Reloads role data from the database.
     */
    @FXML private void reloadRoles() {
        setupRolesTable();
        ObservableList<RoleRow> rows = FXCollections.observableArrayList();
        String sql = "SELECT ROLE_ID, ROLE_NAME, ROLE_DESCRIPTION FROM ROLE ORDER BY ROLE_NAME";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while (rs.next()) {
                rows.add(new RoleRow(rs.getInt(1), rs.getString(2), rs.getString(3)));
            }
        } catch (SQLException e){ showError("Error loading roles: "+e.getMessage()); }
        rolesTable.setItems(rows);
    }

    /**
     * Adds a new role to the system.
     */
    @FXML private void onAddRole(){
        Dialog<RoleEditResult> dlg = new Dialog<>(); dlg.setTitle("Add Role");
        TextField nameF = new TextField(); TextField descF = new TextField();
        GridPane grid = new GridPane(); grid.setHgap(8); grid.setVgap(8);
        grid.addRow(0, new Label("Name:"), nameF);
        grid.addRow(1, new Label("Description:"), descF);
        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(bt -> bt==ButtonType.OK ? new RoleEditResult(nameF.getText(), descF.getText()) : null);
        RoleEditResult res = dlg.showAndWait().orElse(null); if (res==null) return;
        String sql = "INSERT INTO ROLE(ROLE_ID, ROLE_NAME, ROLE_DESCRIPTION) VALUES (ROLE_SEQ.NEXTVAL, ?, ?)";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, res.name); ps.setString(2, res.description); ps.executeUpdate();
        } catch (SQLException e){ showError("Error adding role: "+e.getMessage()); }
        reloadRoles();
    }

    /**
     * Edits an existing role.
     * @param row The role row to edit.
     */
    private void editRole(RoleRow row){
        Dialog<RoleEditResult> dlg = new Dialog<>(); dlg.setTitle("Edit Role");
        TextField nameF = new TextField(row.name.get()); TextField descF = new TextField(row.description.get());
        GridPane grid = new GridPane(); grid.setHgap(8); grid.setVgap(8);
        grid.addRow(0, new Label("Name:"), nameF);
        grid.addRow(1, new Label("Description:"), descF);
        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(bt -> bt==ButtonType.OK ? new RoleEditResult(nameF.getText(), descF.getText()) : null);
        RoleEditResult res = dlg.showAndWait().orElse(null); if (res==null) return;
        String sql = "UPDATE ROLE SET ROLE_NAME=?, ROLE_DESCRIPTION=? WHERE ROLE_ID=?";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, res.name); ps.setString(2, res.description); ps.setInt(3, row.roleId.get()); ps.executeUpdate();
        } catch (SQLException e){ showError("Error updating role: "+e.getMessage()); }
        reloadRoles();
    }

    private void deleteRole(SimpleIntegerProperty roleId){
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM ROLE WHERE ROLE_ID=?")){
            ps.setInt(1, roleId.get()); ps.executeUpdate();
        } catch (SQLException e){ showError("Error deleting role: "+e.getMessage()); }
        reloadRoles();
    }

    // ----- Addresses -----
    private boolean addressesColumnsInitialized = false;
    private void setupAddressesTable(){
        if (addressesTable == null || addressesColumnsInitialized) return;
        TableColumn<AddressRow, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(d -> d.getValue().formatted);
        addressCol.setPrefWidth(620);
        TableColumn<AddressRow, String> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> new TableCell<>(){
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            { editBtn.setOnAction(e -> editAddress(getTableView().getItems().get(getIndex())));
              deleteBtn.setOnAction(e -> deleteAddress(getTableView().getItems().get(getIndex()).addressId));
              editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
              deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;"); }
            @Override protected void updateItem(String item, boolean empty){ super.updateItem(item, empty); setGraphic(empty? null : new HBox(6, editBtn, deleteBtn)); }
        });
        actionsCol.setPrefWidth(200);
        addressesTable.getColumns().addAll(addressCol, actionsCol);
        addressesColumnsInitialized = true;
    }

    @FXML private void reloadAddresses(){
        setupAddressesTable();
        ObservableList<AddressRow> rows = FXCollections.observableArrayList();
        String sql = "SELECT ADDRESS_ID, COUNTRY, STATE, CITY, STREET, HOUSE_NUMBER, ZIP_CODE FROM ADDRESS ORDER BY ADDRESS_ID";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while (rs.next()){
                rows.add(new AddressRow(
                        rs.getInt("ADDRESS_ID"),
                        rs.getString("COUNTRY"), rs.getString("STATE"), rs.getString("CITY"),
                        rs.getString("STREET"), rs.getInt("HOUSE_NUMBER"), rs.getInt("ZIP_CODE")
                ));
            }
        } catch (SQLException e){ showError("Error loading addresses: "+e.getMessage()); }
        addressesTable.setItems(rows);
    }

    @FXML private void onAddAddress(){
        AddressEditResult res = promptAddress(null);
        if (res == null) return;
        String sqlA = "INSERT INTO ADDRESS(ADDRESS_ID, COUNTRY, STATE, CITY, STREET, HOUSE_NUMBER, ZIP_CODE) VALUES (ADDRESS_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sqlA)){
            ps.setString(1, res.country); ps.setString(2, res.state); ps.setString(3, res.city); ps.setString(4, res.street); ps.setInt(5, parseIntSafe(res.house)); ps.setInt(6, parseIntSafe(res.zip));
            ps.executeUpdate();
        } catch (SQLException e){ showError("Error adding address: "+e.getMessage()); }
        reloadAddresses();
    }

    private void editAddress(AddressRow row){
        AddressEditResult res = promptAddress(row);
        if (res == null) return;
        String sql = "UPDATE ADDRESS SET COUNTRY=?, STATE=?, CITY=?, STREET=?, HOUSE_NUMBER=?, ZIP_CODE=? WHERE ADDRESS_ID=?";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, res.country); ps.setString(2, res.state); ps.setString(3, res.city); ps.setString(4, res.street); ps.setInt(5, parseIntSafe(res.house)); ps.setInt(6, parseIntSafe(res.zip)); ps.setInt(7, row.addressId.get());
            ps.executeUpdate();
        } catch (SQLException e){ showError("Error updating address: "+e.getMessage()); }
        reloadAddresses();
    }

    private void deleteAddress(SimpleIntegerProperty addressId){
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM ADDRESS WHERE ADDRESS_ID=?")){
            ps.setInt(1, addressId.get()); ps.executeUpdate();
        } catch (SQLException e){ showError("Error deleting address: "+e.getMessage()); }
        reloadAddresses();
    }

    private AddressEditResult promptAddress(AddressRow row){
        Dialog<AddressEditResult> dlg = new Dialog<>(); dlg.setTitle(row==null? "Add Address" : "Edit Address");
        TextField c = new TextField(row==null? "" : row.country.get());
        TextField s = new TextField(row==null? "" : row.state.get());
        TextField city = new TextField(row==null? "" : row.city.get());
        TextField street = new TextField(row==null? "" : row.street.get());
        TextField house = new TextField(row==null? "" : String.valueOf(row.house.get()));
        TextField zip = new TextField(row==null? "" : String.valueOf(row.zip.get()));
        GridPane g = new GridPane(); g.setHgap(8); g.setVgap(8);
        g.addRow(0, new Label("Country:"), c);
        g.addRow(1, new Label("State:"), s);
        g.addRow(2, new Label("City:"), city);
        g.addRow(3, new Label("Street:"), street);
        g.addRow(4, new Label("House #:"), house);
        g.addRow(5, new Label("ZIP:"), zip);
        dlg.getDialogPane().setContent(g);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(bt -> bt==ButtonType.OK ? new AddressEditResult(c.getText(), s.getText(), city.getText(), street.getText(), house.getText(), zip.getText()) : null);
        return dlg.showAndWait().orElse(null);
    }

    // ----- Transaction Types -----
    private boolean trTypesColumnsInitialized = false;
    private void setupTransactionTypesTable(){
        if (transactionTypesTable == null || trTypesColumnsInitialized) return;
        TableColumn<TransactionTypeRow, String> nameCol = new TableColumn<>("Type"); nameCol.setCellValueFactory(d -> d.getValue().name); nameCol.setPrefWidth(220);
        TableColumn<TransactionTypeRow, String> descCol = new TableColumn<>("Description"); descCol.setCellValueFactory(d -> d.getValue().description); descCol.setPrefWidth(420);
        TableColumn<TransactionTypeRow, String> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> new TableCell<>(){
            private final Button editBtn = new Button("Edit"); private final Button deleteBtn = new Button("Delete");
            { editBtn.setOnAction(e -> editTransactionType(getTableView().getItems().get(getIndex())));
              deleteBtn.setOnAction(e -> deleteTransactionType(getTableView().getItems().get(getIndex()).id));
              editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
              deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;"); }
            @Override protected void updateItem(String item, boolean empty){ super.updateItem(item, empty); setGraphic(empty? null : new HBox(6, editBtn, deleteBtn)); }
        });
        actionsCol.setPrefWidth(200);
        transactionTypesTable.getColumns().addAll(nameCol, descCol, actionsCol);
        trTypesColumnsInitialized = true;
    }

    @FXML private void reloadTransactionTypes(){
        setupTransactionTypesTable();
        ObservableList<TransactionTypeRow> rows = FXCollections.observableArrayList();
        String sql = "SELECT TRANSACTION_TYPE_ID, TRANSACTION_TYPE_NAME, TRANSACTION_TYPE_DESCRIPTION FROM TRANSACTION_TYPE ORDER BY TRANSACTION_TYPE_NAME";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while (rs.next()) rows.add(new TransactionTypeRow(rs.getInt(1), rs.getString(2), rs.getString(3)));
        } catch (SQLException e){ showError("Error loading transaction types: "+e.getMessage()); }
        transactionTypesTable.setItems(rows);
    }

    @FXML private void onAddTransactionType(){
        Dialog<RoleEditResult> dlg = new Dialog<>(); dlg.setTitle("Add Transaction Type");
        TextField nameF = new TextField(); TextField descF = new TextField();
        GridPane g = new GridPane(); g.setHgap(8); g.setVgap(8);
        g.addRow(0, new Label("Name:"), nameF); g.addRow(1, new Label("Description:"), descF);
        dlg.getDialogPane().setContent(g); dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(bt -> bt==ButtonType.OK ? new RoleEditResult(nameF.getText(), descF.getText()) : null);
        RoleEditResult res = dlg.showAndWait().orElse(null); if (res==null) return;
        String sql = "INSERT INTO TRANSACTION_TYPE(TRANSACTION_TYPE_ID, TRANSACTION_TYPE_NAME, TRANSACTION_TYPE_DESCRIPTION) VALUES (TRANSACTION_TYPE_SEQ.NEXTVAL, ?, ?)";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, res.name); ps.setString(2, res.description); ps.executeUpdate();
        } catch (SQLException e){ showError("Error adding transaction type: "+e.getMessage()); }
        reloadTransactionTypes();
    }

    private void editTransactionType(TransactionTypeRow row){
        Dialog<RoleEditResult> dlg = new Dialog<>(); dlg.setTitle("Edit Transaction Type");
        TextField nameF = new TextField(row.name.get()); TextField descF = new TextField(row.description.get());
        GridPane g = new GridPane(); g.setHgap(8); g.setVgap(8);
        g.addRow(0, new Label("Name:"), nameF); g.addRow(1, new Label("Description:"), descF);
        dlg.getDialogPane().setContent(g); dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(bt -> bt==ButtonType.OK ? new RoleEditResult(nameF.getText(), descF.getText()) : null);
        RoleEditResult res = dlg.showAndWait().orElse(null); if (res==null) return;
        String sql = "UPDATE TRANSACTION_TYPE SET TRANSACTION_TYPE_NAME=?, TRANSACTION_TYPE_DESCRIPTION=? WHERE TRANSACTION_TYPE_ID=?";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, res.name); ps.setString(2, res.description); ps.setInt(3, row.id.get()); ps.executeUpdate();
        } catch (SQLException e){ showError("Error updating transaction type: "+e.getMessage()); }
        reloadTransactionTypes();
    }

    private void deleteTransactionType(SimpleIntegerProperty id){
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM TRANSACTION_TYPE WHERE TRANSACTION_TYPE_ID=?")){
            ps.setInt(1, id.get()); ps.executeUpdate();
        } catch (SQLException e){ showError("Error deleting transaction type: "+e.getMessage()); }
        reloadTransactionTypes();
    }

    // ----- Logins (Login_record) -----
    private boolean loginsColumnsInitialized = false;
    private void setupLoginsTable(){
        if (loginsTable == null || loginsColumnsInitialized) return;
        TableColumn<LoginRow, String> userCol = new TableColumn<>("User"); userCol.setCellValueFactory(d -> d.getValue().userName); userCol.setPrefWidth(200);
        TableColumn<LoginRow, String> ipCol = new TableColumn<>("IP"); ipCol.setCellValueFactory(d -> d.getValue().ip); ipCol.setPrefWidth(140);
        TableColumn<LoginRow, String> timeCol = new TableColumn<>("Time"); timeCol.setCellValueFactory(d -> d.getValue().time); timeCol.setPrefWidth(180);
        TableColumn<LoginRow, String> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> new TableCell<>(){
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            {
                editBtn.setStyle("-fx-background-color:#3498db;-fx-text-fill:white;");
                deleteBtn.setStyle("-fx-background-color:#e74c3c;-fx-text-fill:white;");
                editBtn.setOnAction(e -> editLogin(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(e -> {
                    LoginRow row = getTableView().getItems().get(getIndex());
                    deleteLogin(row.id);
                });
            }
            @Override protected void updateItem(String item, boolean empty){
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(new HBox(6, editBtn, deleteBtn));
            }
        });
        actionsCol.setPrefWidth(160);
        loginsTable.getColumns().addAll(userCol, ipCol, timeCol, actionsCol);
        loginsColumnsInitialized = true;
    }

    @FXML private void onAddLogin(){
        Dialog<LoginAddResult> dlg = new Dialog<>(); dlg.setTitle("Add Login Record");
        ComboBox<UserOption> userBox = new ComboBox<>(loadAllUsers());
        TextField ipF = new TextField("127.0.0.1");
        TextField timeF = new TextField(new java.sql.Timestamp(System.currentTimeMillis()).toString().substring(0,19));
        GridPane g = new GridPane(); g.setHgap(8); g.setVgap(8);
        g.addRow(0, new Label("User:"), userBox); g.addRow(1, new Label("IP:"), ipF); g.addRow(2, new Label("Time (yyyy-mm-dd hh:mm:ss):"), timeF);
        dlg.getDialogPane().setContent(g); dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(bt -> bt == ButtonType.OK ? new LoginAddResult(userBox.getValue(), ipF.getText(), timeF.getText()) : null);

        LoginAddResult res = dlg.showAndWait().orElse(null);
        if (res == null || res.user == null) return;

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO LOGIN_RECORD (LOGIN_ID, LOGIN_IP_ADDRESS, USER_ID, LOGIN_TIME) VALUES (LOGIN_SEQ.NEXTVAL, ?, ?, ?)")){
            ps.setString(1, res.ip);
            ps.setInt(2, res.user.userId);
            ps.setTimestamp(3, java.sql.Timestamp.valueOf(res.time.trim()));
            ps.executeUpdate();
        } catch (Exception e){ showError("Error adding login: " + e.getMessage()); }
        reloadLogins();
    }

    private void deleteLogin(SimpleIntegerProperty id){
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM LOGIN_RECORD WHERE LOGIN_ID=?")){
            ps.setInt(1, id.get());
            ps.executeUpdate();
        } catch (SQLException e){ showError("Error deleting login: " + e.getMessage()); }
        reloadLogins();
    }

    @FXML
    private void reloadLogins(){
        setupLoginsTable();
        ObservableList<LoginRow> rows = FXCollections.observableArrayList();

        String userFilter = (loginUserSearchField != null) ? loginUserSearchField.getText() : null;
        String ipFilter   = (loginIpSearchField != null) ? loginIpSearchField.getText() : null;
        java.sql.Date dFrom = (loginDateFromPicker != null && loginDateFromPicker.getValue() != null)
                ? java.sql.Date.valueOf(loginDateFromPicker.getValue()) : null;
        java.sql.Date dTo   = (loginDateToPicker != null && loginDateToPicker.getValue() != null)
                ? java.sql.Date.valueOf(loginDateToPicker.getValue()) : null;

        String sql =
                "SELECT LOGIN_ID, LOGIN_IP_ADDRESS, LOGIN_TIME, USER_NAME, USER_SURNAME\n" +
                "FROM v_login_overview\n" +
                "WHERE ( ? IS NULL OR ? = '' OR UPPER(USER_NAME) LIKE ? OR UPPER(USER_SURNAME) LIKE ? )\n" +
                "  AND ( ? IS NULL OR ? = '' OR UPPER(LOGIN_IP_ADDRESS) LIKE ? )\n" +
                "  AND ( ? IS NULL OR LOGIN_TIME >= ? )\n" +
                "  AND ( ? IS NULL OR LOGIN_TIME <  ? + 1 )\n" +
                "ORDER BY LOGIN_TIME DESC";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String userLike = (userFilter == null || userFilter.isEmpty()) ? null : (userFilter.trim().toUpperCase() + "%");
            String ipLike   = (ipFilter == null   || ipFilter.isEmpty())   ? null : (ipFilter.trim().toUpperCase() + "%");

            ps.setString(1, userFilter);
            ps.setString(2, userFilter);
            ps.setString(3, userLike);
            ps.setString(4, userLike);
            ps.setString(5, ipFilter);
            ps.setString(6, ipFilter);
            ps.setString(7, ipLike);
            ps.setDate(8, dFrom);
            ps.setDate(9, dFrom);
            ps.setDate(10, dTo);
            ps.setDate(11, dTo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new LoginRow(
                            rs.getInt("LOGIN_ID"),
                            rs.getString("USER_NAME") + " " + rs.getString("USER_SURNAME"),
                            rs.getString("LOGIN_IP_ADDRESS"),
                            String.valueOf(rs.getTimestamp("LOGIN_TIME"))
                    ));
                }
            }
        } catch (SQLException e) { showError("Error loading logins: " + e.getMessage()); }

        loginsTable.setItems(rows);
    }

    // ----- Audits -----
    private boolean auditsColumnsInitialized = false;
    private void setupAuditsTable(){
        if (auditsTable == null || auditsColumnsInitialized) return;
        TableColumn<AuditRow, String> userCol = new TableColumn<>("User"); userCol.setCellValueFactory(d -> d.getValue().userName); userCol.setPrefWidth(200);
        TableColumn<AuditRow, String> typeCol = new TableColumn<>("Change"); typeCol.setCellValueFactory(d -> d.getValue().changeType); typeCol.setPrefWidth(180);
        TableColumn<AuditRow, String> timeCol = new TableColumn<>("Time"); timeCol.setCellValueFactory(d -> d.getValue().time); timeCol.setPrefWidth(180);
        TableColumn<AuditRow, String> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> new TableCell<>(){
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            {
                editBtn.setStyle("-fx-background-color:#3498db;-fx-text-fill:white;");
                deleteBtn.setStyle("-fx-background-color:#e74c3c;-fx-text-fill:white;");
                editBtn.setOnAction(e -> editAudit(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(e -> {
                    AuditRow row = getTableView().getItems().get(getIndex());
                    deleteAudit(row.id);
                });
            }
            @Override protected void updateItem(String item, boolean empty){
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(new HBox(6, editBtn, deleteBtn));
            }
        });
        actionsCol.setPrefWidth(160);
        auditsTable.getColumns().addAll(userCol, typeCol, timeCol, actionsCol);
        auditsColumnsInitialized = true;
    }

    @FXML private void onAddAudit(){
        Dialog<AuditAddResult> dlg = new Dialog<>(); dlg.setTitle("Add Audit Log");
        ComboBox<UserOption> userBox = new ComboBox<>(loadAllUsers());
        TextField typeF = new TextField();
        TextField timeF = new TextField(new java.sql.Timestamp(System.currentTimeMillis()).toString().substring(0,19));
        GridPane g = new GridPane(); g.setHgap(8); g.setVgap(8);
        g.addRow(0, new Label("User:"), userBox); g.addRow(1, new Label("Change:"), typeF); g.addRow(2, new Label("Time:"), timeF);
        dlg.getDialogPane().setContent(g); dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(bt -> bt == ButtonType.OK ? new AuditAddResult(userBox.getValue(), typeF.getText(), timeF.getText()) : null);

        AuditAddResult res = dlg.showAndWait().orElse(null);
        if (res == null || res.user == null) return;

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO AUDIT_LOG (Audit_id, Change_type, Change_time, User_id) VALUES (AUDIT_SEQ.NEXTVAL, ?, ?, ?)")){
            ps.setString(1, res.type);
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(res.time.trim()));
            ps.setInt(3, res.user.userId);
            ps.executeUpdate();
        } catch (Exception e){ showError("Error adding audit: " + e.getMessage()); }
        reloadAudits();
    }

    private void deleteAudit(SimpleIntegerProperty id){
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM AUDIT_LOG WHERE AUDIT_ID=?")){
            ps.setInt(1, id.get());
            ps.executeUpdate();
        } catch (SQLException e){ showError("Error deleting audit: " + e.getMessage()); }
        reloadAudits();
    }

    @FXML
    private void reloadAudits(){
        setupAuditsTable();
        ObservableList<AuditRow> rows = FXCollections.observableArrayList();

        String userFilter   = (auditUserSearchField != null) ? auditUserSearchField.getText() : null;
        String changeFilter = (auditChangeSearchField != null) ? auditChangeSearchField.getText() : null;
        java.sql.Date dFrom = (auditDateFromPicker != null && auditDateFromPicker.getValue() != null)
                ? java.sql.Date.valueOf(auditDateFromPicker.getValue()) : null;
        java.sql.Date dTo   = (auditDateToPicker != null && auditDateToPicker.getValue() != null)
                ? java.sql.Date.valueOf(auditDateToPicker.getValue()) : null;

        String sql =
                "SELECT AUDIT_ID, CHANGE_TYPE, CHANGE_TIME, USER_NAME, USER_SURNAME\n" +
                "FROM v_audit_overview\n" +
                "WHERE ( ? IS NULL OR ? = '' OR UPPER(USER_NAME) LIKE ? OR UPPER(USER_SURNAME) LIKE ? )\n" +
                "  AND ( ? IS NULL OR ? = '' OR UPPER(CHANGE_TYPE) LIKE ? )\n" +
                "  AND ( ? IS NULL OR CHANGE_TIME >= ? )\n" +
                "  AND ( ? IS NULL OR CHANGE_TIME <  ? + 1 )\n" +
                "ORDER BY CHANGE_TIME DESC";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String userLike   = (userFilter == null   || userFilter.isEmpty())   ? null : (userFilter.trim().toUpperCase() + "%");
            String changeLike = (changeFilter == null || changeFilter.isEmpty()) ? null : ("%" + changeFilter.trim().toUpperCase() + "%");

            ps.setString(1, userFilter);
            ps.setString(2, userFilter);
            ps.setString(3, userLike);
            ps.setString(4, userLike);
            ps.setString(5, changeFilter);
            ps.setString(6, changeFilter);
            ps.setString(7, changeLike);
            ps.setDate(8, dFrom);
            ps.setDate(9, dFrom);
            ps.setDate(10, dTo);
            ps.setDate(11, dTo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new AuditRow(
                            rs.getInt("AUDIT_ID"),
                            rs.getString("USER_NAME") + " " + rs.getString("USER_SURNAME"),
                            rs.getString("CHANGE_TYPE"),
                            String.valueOf(rs.getTimestamp("CHANGE_TIME"))
                    ));
                }
            }
        } catch (SQLException e){ showError("Error loading audits: " + e.getMessage()); }
        auditsTable.setItems(rows);
    }

    // ----- Documents -----
    private boolean documentsColumnsInitialized = false;
    private void setupDocumentsTable(){
        if (documentsTable == null || documentsColumnsInitialized) return;
        TableColumn<DocumentRow, String> nameCol = new TableColumn<>("File"); nameCol.setCellValueFactory(d -> d.getValue().fileName); nameCol.setPrefWidth(280);
        TableColumn<DocumentRow, String> extCol = new TableColumn<>("Ext"); extCol.setCellValueFactory(d -> d.getValue().extension); extCol.setPrefWidth(80);
        TableColumn<DocumentRow, String> userCol = new TableColumn<>("User"); userCol.setCellValueFactory(d -> d.getValue().userName); userCol.setPrefWidth(240);
        TableColumn<DocumentRow, String> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> new TableCell<>(){
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            { editBtn.setOnAction(e -> editDocument(getTableView().getItems().get(getIndex())));
              deleteBtn.setOnAction(e -> deleteDocument(getTableView().getItems().get(getIndex()).documentId));
              editBtn.setStyle("-fx-background-color:#3498db;-fx-text-fill:white;");
              deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;"); }
            @Override protected void updateItem(String item, boolean empty){ super.updateItem(item, empty); setGraphic(empty? null : new HBox(6, editBtn, deleteBtn)); }
        });
        actionsCol.setPrefWidth(120);
        documentsTable.getColumns().addAll(nameCol, extCol, userCol, actionsCol);
        documentsColumnsInitialized = true;
    }

    @FXML
    private void reloadDocuments(){
        setupDocumentsTable();
        ObservableList<DocumentRow> rows = FXCollections.observableArrayList();

        String userFilter = (documentUserSearchField != null) ? documentUserSearchField.getText() : null;
        String extFilter  = (documentExtensionSearchField != null) ? documentExtensionSearchField.getText() : null;

        String normExt = null;
        if (extFilter != null) {
            normExt = extFilter.trim();
            if (normExt.startsWith(".")) normExt = normExt.substring(1);
            if (normExt.isEmpty()) normExt = null;
        }

        String sql =
                "SELECT d.DOCUMENT_ID, d.FILE_NAME, d.FILE_EXTENSION, u.NAME, u.SURNAME\n" +
                "FROM DOCUMENT d JOIN \"User\" u ON u.USER_ID = d.USER_ID\n" +
                "WHERE ( ? IS NULL OR ? = '' OR UPPER(u.NAME) LIKE ? OR UPPER(u.SURNAME) LIKE ? )\n" +
                "  AND ( ? IS NULL OR ? = '' OR UPPER(d.FILE_EXTENSION) LIKE ? )\n" +
                "ORDER BY d.DOCUMENT_ID DESC";

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String userLike = (userFilter == null || userFilter.isEmpty()) ? null : (userFilter.trim().toUpperCase() + "%");
            String extLike  = (normExt == null) ? null : (normExt.toUpperCase() + "%");

            ps.setString(1, userFilter);
            ps.setString(2, userFilter);
            ps.setString(3, userLike);
            ps.setString(4, userLike);
            ps.setString(5, normExt);
            ps.setString(6, normExt);
            ps.setString(7, extLike);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new DocumentRow(
                            rs.getInt(1), rs.getString(2), rs.getString(3),
                            rs.getString(4) + " " + rs.getString(5)
                    ));
                }
            }
        } catch (SQLException e){ showError("Error loading documents: " + e.getMessage()); }
        documentsTable.setItems(rows);
    }

    // --- Add actions placeholders for newly added buttons ---
    @FXML private void onAddDocument(){
        Dialog<DocumentAddResult> dlg = new Dialog<>();
        dlg.setTitle("Add Document");
        ComboBox<UserOption> userBox = new ComboBox<>(loadAllUsers());
        StackPane dropArea = new StackPane(new Label("Drag file here to upload"));
        dropArea.setPrefSize(300, 100);
        dropArea.setStyle("-fx-border-color: gray; -fx-border-style: dashed;");

        final java.io.File[] fileArr = {null};
        dropArea.setOnDragOver(ev -> { if(ev.getDragboard().hasFiles()) ev.acceptTransferModes(javafx.scene.input.TransferMode.COPY); ev.consume(); });
        dropArea.setOnDragDropped(ev -> { if(ev.getDragboard().hasFiles()){ fileArr[0] = ev.getDragboard().getFiles().get(0); ((Label)dropArea.getChildren().get(0)).setText(fileArr[0].getName()); } ev.consume(); });

        VBox v = new VBox(10, new Label("Owner:"), userBox, dropArea);
        dlg.getDialogPane().setContent(v);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(bt -> bt == ButtonType.OK ? new DocumentAddResult(userBox.getValue(), fileArr[0]) : null);

        DocumentAddResult res = dlg.showAndWait().orElse(null);
        if (res != null && res.user != null && res.file != null) {
            saveAdminDocument(res.file, res.user.userId);
        }
        reloadDocuments();
    }

    private void saveAdminDocument(java.io.File file, int userId) {
        String name = file.getName();
        String ext = name.contains(".") ? name.substring(name.lastIndexOf(".") + 1) : "";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             java.io.FileInputStream fis = new java.io.FileInputStream(file)) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO DOCUMENT (DOCUMENT_ID, FILE_NAME, FILE_EXTENSION, FILE_DATA, USER_ID) VALUES (DOCUMENT_SEQ.NEXTVAL, ?, ?, ?, ?)");
            ps.setString(1, name); ps.setString(2, ext); ps.setBinaryStream(3, fis, (int)file.length()); ps.setInt(4, userId);
            ps.executeUpdate();
        } catch (Exception e) { showError("Upload failed: " + e.getMessage()); }
    }

    // Helpers
    private static class LoginAddResult { UserOption user; String ip, time; LoginAddResult(UserOption u, String i, String t){user=u; ip=i; time=t;} }
    private static class AuditAddResult { UserOption user; String type, time; AuditAddResult(UserOption u, String ty, String t){user=u; type=ty; time=t;} }
    private static class DocumentAddResult { UserOption user; java.io.File file; DocumentAddResult(UserOption u, java.io.File f){user=u; file=f;} }

    private void deleteDocument(SimpleIntegerProperty id){
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM DOCUMENT WHERE DOCUMENT_ID=?")){
            ps.setInt(1, id.get()); ps.executeUpdate();
        } catch (SQLException e){ showError("Error deleting document: "+e.getMessage()); }
        reloadDocuments();
    }

    // ----- Branches -----
    private boolean branchesColumnsInitialized = false;

    private void setupBranchesTable() {
        if (branchesTable == null || branchesColumnsInitialized) return;

        TableColumn<BranchRow, String> nameCol = new TableColumn<>("Branch");
        nameCol.setCellValueFactory(data -> data.getValue().branchNameProperty);
        nameCol.setPrefWidth(200);

        TableColumn<BranchRow, String> parentCol = new TableColumn<>("From parent branch");
        parentCol.setCellValueFactory(data -> data.getValue().parentNameProperty);
        parentCol.setPrefWidth(200);

        TableColumn<BranchRow, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(data -> data.getValue().addressTextProperty);
        addressCol.setPrefWidth(420);

        TableColumn<BranchRow, String> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final Button tellersBtn = new Button("Tellers");
            {
                editBtn.setOnAction(e -> {
                    BranchRow row = getTableView().getItems().get(getIndex());
                    editBranch(row);
                });
                deleteBtn.setOnAction(e -> {
                    BranchRow row = getTableView().getItems().get(getIndex());
                    deleteBranch(row.branchId);
                });
                tellersBtn.setOnAction(e -> {
                    BranchRow row = getTableView().getItems().get(getIndex());
                    showBranchTellers(row);
                });
                editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                tellersBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(6, editBtn, tellersBtn, deleteBtn);
                    setGraphic(box);
                }
            }
        });
        actionsCol.setPrefWidth(280);

        branchesTable.getColumns().addAll(nameCol, parentCol, addressCol, actionsCol);
        branchesColumnsInitialized = true;
    }

    @FXML
    private void reloadBranches() {
        setupBranchesTable();
        ObservableList<BranchRow> rows = FXCollections.observableArrayList();
        String filter = branchSearchField != null ? branchSearchField.getText() : null;
        String sql = """
                SELECT BRANCH_ID, BRANCH_NAME,
                       COUNTRY, STATE, CITY, STREET, HOUSE_NUMBER, ZIP_CODE,
                       PARENT_NAME
                FROM v_branch_overview
                WHERE (? IS NULL OR ? = '' OR UPPER(BRANCH_NAME) LIKE ?)
                ORDER BY BRANCH_NAME
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
                    String parentName = rs.getString("PARENT_NAME");
                    String address = formatAddress(
                            rs.getString("COUNTRY"), rs.getString("STATE"), rs.getString("CITY"),
                            rs.getString("STREET"), rs.getInt("HOUSE_NUMBER"), rs.getInt("ZIP_CODE")
                    );
                    rows.add(new BranchRow(id, name, parentName, address));
                }
            }
        } catch (SQLException e) {
            showError("Error loading branches: " + e.getMessage());
        }
        branchesTable.setItems(rows);
    }

    // Zobrazí seznam tellerů pro vybranou pobočku včetně všech podřízených (hierarchický dotaz)
    private void showBranchTellers(BranchRow row) {
        ObservableList<String> items = FXCollections.observableArrayList();
        String sql = """
                SELECT tv.NAME, tv.SURNAME, tv.WORK_PHONE_NUMBER, tv.WORK_EMAIL_ADDRESS, tv.BRANCH_NAME
                FROM v_teller_overview tv
                JOIN v_branch_tree bt ON bt.BRANCH_ID = tv.BRANCH_ID
                WHERE bt.ROOT_BRANCH_ID = ?
                ORDER BY tv.BRANCH_NAME, tv.SURNAME, tv.NAME
                """;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, row.branchId.get());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String branch = rs.getString(5);
                    String name = rs.getString(1) + " " + rs.getString(2);
                    String phone = rs.getString(3);
                    String email = rs.getString(4);
                    String line = String.format("%s — %s (phone: %s, email: %s)", branch, name,
                            phone == null ? "-" : phone, email == null ? "-" : email);
                    items.add(line);
                }
            }
        } catch (SQLException e) {
            showError("Error loading tellers: " + e.getMessage());
            return;
        }

        Dialog<Void> dlg = new Dialog<>();
        dlg.setTitle("Tellers for branch including children");
        ListView<String> list = new ListView<>(items);
        list.setPlaceholder(new Label("No tellers found"));
        dlg.getDialogPane().setContent(new VBox(8, new Label("Branch: " + row.branchNameProperty.get()), list));
        dlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dlg.showAndWait();
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

        // Parent branch dropdown (with empty option)
        Label parentL = new Label("Parent branch:");
        ObservableList<BranchOption> allBranches = loadBranches();
        ComboBox<BranchOption> parentBox = new ComboBox<>(FXCollections.observableArrayList());
        parentBox.getItems().add(null); // empty option
        // exclude self from choices
        for (BranchOption bo : allBranches) if (bo.branchId != row.branchId.get()) parentBox.getItems().add(bo);

        // Prefill address from DB
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT a.COUNTRY, a.STATE, a.CITY, a.STREET, a.HOUSE_NUMBER, a.ZIP_CODE, b.PARENT_BRANCH_ID FROM BRANCH b JOIN ADDRESS a ON b.ADDRESS_ID = a.ADDRESS_ID WHERE b.BRANCH_ID = ?")) {
            ps.setInt(1, row.branchId.get());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cF.setText(rs.getString(1)); sF.setText(rs.getString(2)); cityF.setText(rs.getString(3));
                    streetF.setText(rs.getString(4)); houseF.setText(String.valueOf(rs.getInt(5))); zipF.setText(String.valueOf(rs.getInt(6)));
                    int parentId = rs.getInt(7); if (!rs.wasNull()) {
                        for (BranchOption bo : parentBox.getItems()) { if (bo != null && bo.branchId == parentId) { parentBox.getSelectionModel().select(bo); break; } }
                    } else {
                        parentBox.getSelectionModel().select(null);
                    }
                }
            }
        } catch (SQLException ignore) {}

        GridPane grid = new GridPane();
        grid.setHgap(8); grid.setVgap(8);
        grid.addRow(0, nameL, nameF);
        grid.addRow(1, cL, cF);
        grid.addRow(2, sL, sF);
        grid.addRow(3, cityL, cityF);
        grid.addRow(4, streetL, streetF);
        grid.addRow(5, houseL, houseF);
        grid.addRow(6, zipL, zipF);
        grid.addRow(7, parentL, parentBox);

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
        Connection conn = null; boolean prevAuto = true;
        try {
            conn = ConnectionSingleton.getInstance().getConnection();
            prevAuto = conn.getAutoCommit();
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

            try (PreparedStatement ps = conn.prepareStatement("UPDATE BRANCH SET BRANCH_NAME = ?, ADDRESS_ID = ?, PARENT_BRANCH_ID = ? WHERE BRANCH_ID = ?")) {
                ps.setString(1, res.branchName);
                ps.setInt(2, newAddressId);
                BranchOption sel = parentBox.getValue();
                if (sel == null) ps.setNull(3, java.sql.Types.INTEGER); else ps.setInt(3, sel.branchId);
                ps.setInt(4, row.branchId.get());
                ps.executeUpdate();
            }
            conn.commit();
        } catch (SQLException ex) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ignore) {} }
            showError("Error updating branch: " + ex.getMessage());
        } finally {
            if (conn != null) { try { conn.setAutoCommit(prevAuto); } catch (SQLException ignore) {} try { conn.close(); } catch (SQLException ignore) {} }
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

        TableColumn<UserRow, String> approvedCol = new TableColumn<>("Approved");
        approvedCol.setCellValueFactory(d -> d.getValue().approvedProperty);
        approvedCol.setPrefWidth(90);

        TableColumn<UserRow, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(d -> d.getValue().addressTextProperty);
        addressCol.setPrefWidth(320);

        TableColumn<UserRow, String> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final Button emulateBtn = new Button("Emulate");
            private final Button approveBtn = new Button("Approve");
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
                approveBtn.setOnAction(e -> approveUser(getTableView().getItems().get(getIndex()).userId));
                editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                emulateBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
                approveBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
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
                    // Only show Approve when not approved
                    approveBtn.setDisable(row == null || "Y".equalsIgnoreCase(row.approvedProperty.get()));
                    HBox box = new HBox(6, editBtn, deleteBtn, emulateBtn, approveBtn);
                    setGraphic(box);
                }
            }
        });
        actionsCol.setPrefWidth(360);

        usersTable.getColumns().addAll(nameCol, surnameCol, roleCol, approvedCol, addressCol, actionsCol);
        usersColumnsInitialized = true;
    }

    @FXML
    private void reloadUsers() {
        setupUsersTable();
        String nameFilter = userNameSearchField != null ? userNameSearchField.getText() : null;
        String surnameFilter = userSurnameSearchField != null ? userSurnameSearchField.getText() : null;
        ObservableList<UserRow> rows = FXCollections.observableArrayList();
        // Use view: v_user_overview
        String sql = """
                SELECT USER_ID, NAME, SURNAME, APPROVED, ROLE_NAME,
                       COUNTRY, STATE, CITY, STREET, HOUSE_NUMBER, ZIP_CODE
                FROM v_user_overview
                WHERE (? IS NULL OR ? = '' OR UPPER(NAME) LIKE ?)
                  AND (? IS NULL OR ? = '' OR UPPER(SURNAME) LIKE ?)
                ORDER BY SURNAME, NAME
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
                    String approved = null; try { approved = rs.getString("APPROVED"); } catch (SQLException ignore) {}
                    String role = rs.getString("ROLE_NAME");
                    String address = formatAddress(
                            rs.getString("COUNTRY"), rs.getString("STATE"), rs.getString("CITY"),
                            rs.getString("STREET"), rs.getInt("HOUSE_NUMBER"), rs.getInt("ZIP_CODE")
                    );
                    rows.add(new UserRow(id, name, surname, role, approved == null? "?" : approved, address));
                }
            }
        } catch (SQLException e) {
            showError("Error loading users: " + e.getMessage());
        }
        usersTable.setItems(rows);
    }

    private void approveUser(SimpleIntegerProperty userId){
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE \"User\" SET APPROVED='Y' WHERE USER_ID = ?")){
            ps.setInt(1, userId.get());
            ps.executeUpdate();
        } catch (SQLException e){ showError("Error approving user: "+e.getMessage()); }
        reloadUsers();
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
        // Use view: v_client_overview
        String sql = """
                SELECT USER_ID, NAME, SURNAME, PHONE_NUMBER, EMAIL_ADDRESS,
                       TELLER_NAME, TELLER_SURNAME,
                       COUNTRY, STATE, CITY, STREET, HOUSE_NUMBER, ZIP_CODE
                FROM v_client_overview
                WHERE (? IS NULL OR ? = '' OR UPPER(NAME) LIKE ?)
                  AND (? IS NULL OR ? = '' OR UPPER(SURNAME) LIKE ?)
                ORDER BY SURNAME, NAME
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
                    String teller = ((rs.getString("TELLER_NAME") == null ? "-" : rs.getString("TELLER_NAME"))
                            + " " + (rs.getString("TELLER_SURNAME") == null ? "" : rs.getString("TELLER_SURNAME"))).trim();
                    String addr = formatAddress(rs.getString("COUNTRY"), rs.getString("STATE"), rs.getString("CITY"), rs.getString("STREET"), rs.getInt("HOUSE_NUMBER"), rs.getInt("ZIP_CODE"));
                    rows.add(new ClientRow(id, n, s, contact, teller, addr));
                }
            }
        } catch (SQLException e) { showError("Error loading clients: " + e.getMessage()); }
        clientsTable.setItems(rows);
    }

    private void editClient(ClientRow row) {
        Dialog<ClientAddResult> dlg = new Dialog<>(); // Use AddResult DTO to hold all fields
        dlg.setTitle("Edit Client");
        
        TextField nameF = new TextField(); TextField surF = new TextField();
        TextField birthF = new TextField(); TextField phoneF = new TextField(); TextField emailF = new TextField();
        ComboBox<UserOption> tellerBox = new ComboBox<>(loadTellers());
        TextField countryF = new TextField(); TextField stateF = new TextField(); TextField cityF = new TextField();
        TextField streetF = new TextField(); TextField houseF = new TextField(); TextField zipF = new TextField();

        // Load current data from DB
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT u.NAME, u.SURNAME, c.BIRTH_NUMBER, c.PHONE_NUMBER, c.EMAIL_ADDRESS, c.TELLER_ID, a.COUNTRY, a.STATE, a.CITY, a.STREET, a.HOUSE_NUMBER, a.ZIP_CODE " +
                     "FROM CLIENT c JOIN \"User\" u ON c.USER_ID = u.USER_ID JOIN ADDRESS a ON u.ADDRESS_ID = a.ADDRESS_ID WHERE c.USER_ID = ?")) {
            ps.setInt(1, row.userId.get());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    nameF.setText(rs.getString(1)); surF.setText(rs.getString(2)); birthF.setText(rs.getString(3));
                    phoneF.setText(rs.getString(4)); emailF.setText(rs.getString(5));
                    int tId = rs.getInt(6);
                    for (UserOption t : tellerBox.getItems()) if (t.userId == tId) tellerBox.getSelectionModel().select(t);
                    countryF.setText(rs.getString(7)); stateF.setText(rs.getString(8)); cityF.setText(rs.getString(9));
                    streetF.setText(rs.getString(10)); houseF.setText(String.valueOf(rs.getInt(11))); zipF.setText(String.valueOf(rs.getInt(12)));
                }
            }
        } catch (SQLException ignore) {}

        GridPane g = new GridPane(); g.setHgap(8); g.setVgap(8);
        g.addRow(0,new Label("Name:"), nameF); g.addRow(1,new Label("Surname:"), surF);
        g.addRow(2,new Label("Birth number:"), birthF); g.addRow(3,new Label("Phone:"), phoneF); g.addRow(4,new Label("Email:"), emailF);
        g.addRow(5,new Label("Teller:"), tellerBox);
        g.addRow(6,new Label("Country:"), countryF); g.addRow(7,new Label("State:"), stateF); g.addRow(8,new Label("City:"), cityF);
        g.addRow(9,new Label("Street:"), streetF); g.addRow(10,new Label("House No:"), houseF); g.addRow(11,new Label("ZIP:"), zipF);
        
        dlg.getDialogPane().setContent(g);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(b -> b == ButtonType.OK ? new ClientAddResult(nameF.getText(), surF.getText(), "", birthF.getText(), phoneF.getText(), emailF.getText(), tellerBox.getValue(), countryF.getText(), stateF.getText(), cityF.getText(), streetF.getText(), houseF.getText(), zipF.getText()) : null);
        
        ClientAddResult res = dlg.showAndWait().orElse(null);
        if (res == null) return;

        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            int addrId;
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO ADDRESS(ADDRESS_ID, COUNTRY, STATE, CITY, STREET, HOUSE_NUMBER, ZIP_CODE) VALUES(ADDRESS_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?)")) {
                ps.setString(1, res.country); ps.setString(2, res.state); ps.setString(3, res.city); ps.setString(4, res.street); ps.setInt(5, parseIntSafe(res.house)); ps.setInt(6, parseIntSafe(res.zip));
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("SELECT ADDRESS_SEQ.CURRVAL FROM dual"); ResultSet rs = ps.executeQuery()) { rs.next(); addrId = rs.getInt(1); }
            
            try (PreparedStatement ps = conn.prepareStatement("UPDATE \"User\" SET NAME=?, SURNAME=?, ADDRESS_ID=? WHERE USER_ID=?")) {
                ps.setString(1, res.name); ps.setString(2, res.surname); ps.setInt(3, addrId); ps.setInt(4, row.userId.get()); ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("UPDATE CLIENT SET BIRTH_NUMBER=?, PHONE_NUMBER=?, EMAIL_ADDRESS=?, TELLER_ID=? WHERE USER_ID=?")) {
                ps.setString(1, res.birth); ps.setString(2, res.phone); ps.setString(3, res.email); ps.setInt(4, res.teller.userId); ps.setInt(5, row.userId.get()); ps.executeUpdate();
            }
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
        ComboBox<BranchOption> branchBox = new ComboBox<>(loadBranches());
        TextField phoneF = new TextField(); TextField emailF = new TextField();
        TextField cF = new TextField(); TextField sF = new TextField(); TextField cityF = new TextField();
        TextField streetF = new TextField(); TextField houseF = new TextField(); TextField zipF = new TextField(); TextField countryF = new TextField();

        // Prefill current teller data
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT t.WORK_PHONE_NUMBER, t.WORK_EMAIL_ADDRESS, t.BRANCH_ID, a.COUNTRY, a.STATE, a.CITY, a.STREET, a.HOUSE_NUMBER, a.ZIP_CODE " +
                     "FROM TELLER t JOIN \"User\" u ON t.USER_ID = u.USER_ID JOIN ADDRESS a ON u.ADDRESS_ID = a.ADDRESS_ID WHERE t.USER_ID = ?")) {
            ps.setInt(1, row.userId.get());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    phoneF.setText(rs.getString(1)); emailF.setText(rs.getString(2));
                    int bId = rs.getInt(3);
                    for (BranchOption b : branchBox.getItems()) if (b.branchId == bId) branchBox.getSelectionModel().select(b);
                    countryF.setText(rs.getString(4)); sF.setText(rs.getString(5)); cityF.setText(rs.getString(6));
                    streetF.setText(rs.getString(7)); houseF.setText(String.valueOf(rs.getInt(8))); zipF.setText(String.valueOf(rs.getInt(9)));
                }
            }
        } catch (SQLException ignore) {}

        GridPane grid = new GridPane(); grid.setHgap(8); grid.setVgap(8);
        grid.addRow(0, new Label("Work Phone:"), phoneF);
        grid.addRow(1, new Label("Work Email:"), emailF);
        grid.addRow(2, new Label("Branch:"), branchBox);
        grid.addRow(3, new Label("Country:"), countryF);
        grid.addRow(4, new Label("State:"), sF);
        grid.addRow(5, new Label("City:"), cityF);
        grid.addRow(6, new Label("Street:"), streetF);
        grid.addRow(7, new Label("House No:"), houseF);
        grid.addRow(8, new Label("ZIP:"), zipF);

        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(b -> b == ButtonType.OK ? new TellerEditResult(branchBox.getValue(), phoneF.getText(), emailF.getText(), countryF.getText(), sF.getText(), cityF.getText(), streetF.getText(), houseF.getText(), zipF.getText()) : null);

        TellerEditResult res = dlg.showAndWait().orElse(null); if (res == null) return;
        Connection conn = null; boolean prevAuto = true;
        try {
            conn = ConnectionSingleton.getInstance().getConnection();
            prevAuto = conn.getAutoCommit();
            conn.setAutoCommit(false);
            int newAddrId;
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO ADDRESS(ADDRESS_ID, COUNTRY, STATE, CITY, STREET, HOUSE_NUMBER, ZIP_CODE) VALUES(ADDRESS_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?)")){
                ps.setString(1,res.country); ps.setString(2,res.state); ps.setString(3,res.city); ps.setString(4,res.street); ps.setInt(5, parseIntSafe(res.house)); ps.setInt(6, parseIntSafe(res.zip));
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("SELECT ADDRESS_SEQ.CURRVAL FROM dual"); ResultSet rs = ps.executeQuery()) { rs.next(); newAddrId = rs.getInt(1); }
            try (PreparedStatement ps = conn.prepareStatement("UPDATE \"User\" SET ADDRESS_ID = ? WHERE USER_ID = ?")) { ps.setInt(1,newAddrId); ps.setInt(2,row.userId.get()); ps.executeUpdate(); }
            try (PreparedStatement ps = conn.prepareStatement("UPDATE TELLER SET BRANCH_ID = ?, WORK_PHONE_NUMBER = ?, WORK_EMAIL_ADDRESS = ? WHERE USER_ID = ?")) {
                ps.setInt(1,res.branch.branchId); ps.setString(2, res.phone); ps.setString(3, res.email); ps.setInt(4,row.userId.get()); ps.executeUpdate();
            }
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
        // Use view: v_account_overview
        String sql = """
                SELECT ACCOUNT_ID, ACCOUNT_NUMBER, ACCOUNT_BALANCE,
                       OWNER_NAME, OWNER_SURNAME
                FROM v_account_overview
                WHERE (? IS NULL OR ? = '' OR UPPER(OWNER_NAME) LIKE ?)
                  AND (? IS NULL OR ? = '' OR UPPER(OWNER_SURNAME) LIKE ?)
                  AND (? IS NULL OR ? = '' OR UPPER(ACCOUNT_NUMBER) LIKE ?)
                ORDER BY ACCOUNT_NUMBER
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
                            rs.getString("OWNER_NAME")+" "+rs.getString("OWNER_SURNAME"),
                            String.valueOf(rs.getBigDecimal("ACCOUNT_BALANCE"))
                    ));
                }
            }
        } catch (SQLException e){ showError("Error loading accounts: "+e.getMessage()); }
        accountsTable.setItems(rows);
    }

    private void editAccount(AccountRow row){
        Dialog<AccountEditResult> dlg = new Dialog<>(); dlg.setTitle("Edit Account");
        ComboBox<UserOption> clientBox = new ComboBox<>(loadClients());

        // Prefill client selection
        for (UserOption u : clientBox.getItems()) {
            if (u.display.equals(row.owner.get())) {
                clientBox.getSelectionModel().select(u);
                break;
            }
        }

        TextField numberF = new TextField(row.number.get());
        TextField balanceF = new TextField(row.balance.get());

        GridPane grid = new GridPane(); grid.setHgap(8); grid.setVgap(8);
        grid.addRow(0, new Label("Owner:"), clientBox);
        grid.addRow(1, new Label("Account Number:"), numberF);
        grid.addRow(2, new Label("Balance:"), balanceF);

        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(bt -> bt == ButtonType.OK ? new AccountEditResult(clientBox.getValue(), numberF.getText(), balanceF.getText()) : null);

        AccountEditResult res = dlg.showAndWait().orElse(null);
        if (res == null || res.client == null) return;

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE ACCOUNT SET CLIENT_ID = ?, ACCOUNT_NUMBER = ?, ACCOUNT_BALANCE = ? WHERE ACCOUNT_ID = ?")){
            ps.setInt(1, res.client.userId);
            ps.setString(2, res.number);
            ps.setBigDecimal(3, new java.math.BigDecimal(res.balance));
            ps.setInt(4, row.accountId.get());
            ps.executeUpdate();
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
        sb.append("SELECT TRANSACTION_ID, TRANSFER_AMOUNT, TRANSACTION_TIME, TRANSACTION_TYPE_NAME, ");
        sb.append("ACCOUNT_FROM_NUMBER AS ACC_FROM, ACCOUNT_TO_NUMBER AS ACC_TO ");
        sb.append("FROM v_transaction_overview ");
        sb.append("WHERE 1=1 ");
        if (typeFilter != null && !typeFilter.isEmpty()) sb.append("AND TRANSACTION_TYPE_NAME = ? ");
        if (min != null) sb.append("AND ABS(TRANSFER_AMOUNT) >= ? ");
        if (max != null) sb.append("AND ABS(TRANSFER_AMOUNT) <= ? ");
        if (from != null) sb.append("AND TRANSACTION_TIME >= ? ");
        if (to != null) sb.append("AND TRANSACTION_TIME <= ? ");
        if (accFilter != null && !accFilter.isEmpty()) sb.append("AND (UPPER(ACCOUNT_FROM_NUMBER) LIKE ? OR UPPER(ACCOUNT_TO_NUMBER) LIKE ?) ");
        // Direction only applies if an account filter is provided (context for direction)
        if (accFilter != null && !accFilter.isEmpty() && dir != null && !dir.equals("All")) {
            if (dir.equals("Incoming")) sb.append("AND UPPER(ACCOUNT_TO_NUMBER) LIKE ? ");
            else if (dir.equals("Outgoing")) sb.append("AND UPPER(ACCOUNT_FROM_NUMBER) LIKE ? ");
        }
        sb.append("ORDER BY TRANSACTION_TIME DESC");

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
        ComboBox<String> typeBox = new ComboBox<>(loadTransactionTypeNames());
        if (row.typeName!=null) typeBox.getSelectionModel().select(row.typeName.get()); else typeBox.getSelectionModel().selectFirst();
        TextField amountF = new TextField(row.amount.get());
        ComboBox<AccountOption> fromBox = new ComboBox<>(loadAccounts());
        ComboBox<AccountOption> toBox = new ComboBox<>(loadAccounts());
        // Preselect accounts based on account numbers in the row
        if (row.fromAcc != null){ for (AccountOption a : fromBox.getItems()) if (a != null && a.toString().equals(row.fromAcc.get())) { fromBox.getSelectionModel().select(a); break; } }
        if (row.toAcc != null){ for (AccountOption a : toBox.getItems()) if (a != null && a.toString().equals(row.toAcc.get())) { toBox.getSelectionModel().select(a); break; } }
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
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            { editBtn.setOnAction(e-> editMessage(getTableView().getItems().get(getIndex())));
              deleteBtn.setOnAction(e-> deleteMessage(getTableView().getItems().get(getIndex()).messageId));
              editBtn.setStyle("-fx-background-color:#3498db;-fx-text-fill:white;");
              deleteBtn.setStyle("-fx-background-color:#e74c3c;-fx-text-fill:white;"); }
            @Override protected void updateItem(String item, boolean empty){ super.updateItem(item, empty); setGraphic(empty? null : new HBox(6, editBtn, deleteBtn)); }
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
        sb.append("SELECT MESSAGE_ID, FROM_NAME, FROM_SURNAME, TO_NAME, TO_SURNAME, MESSAGE_TEXT, MESSAGE_SENT_AT ");
        sb.append("FROM v_message_overview ");
        sb.append("WHERE 1=1 ");
        if (from != null) sb.append("AND MESSAGE_SENT_AT >= ? ");
        if (to != null) sb.append("AND MESSAGE_SENT_AT <= ? ");
        if (userTerm != null && !userTerm.isEmpty()) {
            sb.append("AND (UPPER(NVL(FROM_NAME,'')) LIKE ? OR UPPER(NVL(FROM_SURNAME,'')) LIKE ? ");
            sb.append("OR UPPER(TO_NAME) LIKE ? OR UPPER(TO_SURNAME) LIKE ?) ");
        }
        sb.append("ORDER BY MESSAGE_SENT_AT DESC");

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

    private void editMessage(MessageRow baseRow){
        // Load full message details to prefill
        int mid = baseRow.messageId.get();
        String txt = baseRow.text.get();
        String read = "N"; Integer fromId = null; Integer toId = null; java.sql.Timestamp sentAt = null;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT MESSAGE_TEXT, MESSAGE_READ, USER_FROM_ID, USER_TO_ID, MESSAGE_SENT_AT FROM MESSAGE WHERE MESSAGE_ID = ?")){
            ps.setInt(1, mid);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    txt = rs.getString(1);
                    read = rs.getString(2);
                    fromId = rs.getObject(3) == null? null : rs.getInt(3);
                    toId = rs.getObject(4) == null? null : rs.getInt(4);
                    sentAt = rs.getTimestamp(5);
                }
            }
        } catch (SQLException ignore) {}

        Dialog<Boolean> dlg = new Dialog<>(); dlg.setTitle("Edit Message");
        ComboBox<UserOption> fromBox = new ComboBox<>(loadAllUsers());
        ComboBox<UserOption> toBox = new ComboBox<>(loadAllUsers());
        // Add a synthetic NULL sender option
        fromBox.getItems().add(0, new UserOption(-1, "<System / NULL>"));
        if (fromId != null){
            for (UserOption u : fromBox.getItems()) if (u.userId == fromId){ fromBox.getSelectionModel().select(u); break; }
        } else {
            fromBox.getSelectionModel().select(0);
        }
        if (toId != null){ for (UserOption u : toBox.getItems()) if (u.userId == toId){ toBox.getSelectionModel().select(u); break; } }
        TextArea textF = new TextArea(txt);
        ComboBox<String> readBox = new ComboBox<>(FXCollections.observableArrayList("Y","N"));
        readBox.getSelectionModel().select(read != null && read.equalsIgnoreCase("Y")? "Y":"N");
        TextField timeF = new TextField(sentAt==null? "" : sentAt.toString().substring(0,19)); // yyyy-MM-dd HH:mm:ss

        GridPane g = new GridPane(); g.setHgap(8); g.setVgap(8);
        g.addRow(0, new Label("From:"), fromBox);
        g.addRow(1, new Label("To:"), toBox);
        g.addRow(2, new Label("Text:"), textF);
        g.addRow(3, new Label("Read (Y/N):"), readBox);
        g.addRow(4, new Label("Sent at (yyyy-MM-dd HH:mm:ss):"), timeF);
        dlg.getDialogPane().setContent(g);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Boolean ok = dlg.showAndWait().orElse(false); if (!ok) return;

        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE MESSAGE SET USER_FROM_ID = ?, USER_TO_ID = ?, MESSAGE_TEXT = ?, MESSAGE_READ = ?, MESSAGE_SENT_AT = ? WHERE MESSAGE_ID = ?")){
            UserOption f = fromBox.getValue();
            UserOption t = toBox.getValue();
            if (t == null){ showError("Recipient is required."); return; }
            ps.setObject(1, (f==null || f.userId==-1)? null : f.userId);
            ps.setInt(2, t.userId);
            ps.setString(3, textF.getText());
            ps.setString(4, readBox.getValue());
            java.sql.Timestamp ts;
            try { ts = java.sql.Timestamp.valueOf(timeF.getText().trim()); } catch (Exception ex){ ts = new java.sql.Timestamp(System.currentTimeMillis()); }
            ps.setTimestamp(5, ts);
            ps.setInt(6, mid);
            ps.executeUpdate();
        } catch (SQLException e){ showError("Error updating message: "+e.getMessage()); }
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

        Label approvedL = new Label("Approved (Y/N/R):");
        ComboBox<String> approvedBox = new ComboBox<>(FXCollections.observableArrayList("Y","N","R"));
        approvedBox.getSelectionModel().select(row.approvedProperty.get());

        Label cL = new Label("Country:"); TextField cF = new TextField();
        Label sL = new Label("State:");   TextField sF = new TextField();
        Label cityL = new Label("City:");  TextField cityF = new TextField();
        Label streetL = new Label("Street:"); TextField streetF = new TextField();
        Label houseL = new Label("House No:"); TextField houseF = new TextField();
        Label zipL = new Label("ZIP:"); TextField zipF = new TextField();

        // Pre-fill address by querying current address of the user
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT a.COUNTRY,a.STATE,a.CITY,a.STREET,a.HOUSE_NUMBER,a.ZIP_CODE FROM \"User\" u JOIN ADDRESS a ON u.ADDRESS_ID=a.ADDRESS_ID WHERE u.USER_ID = ?")){
            ps.setInt(1, row.userId.get());
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    cF.setText(rs.getString(1)); sF.setText(rs.getString(2)); cityF.setText(rs.getString(3));
                    streetF.setText(rs.getString(4)); houseF.setText(String.valueOf(rs.getInt(5))); zipF.setText(String.valueOf(rs.getInt(6)));
                }
            }
        } catch (SQLException ignore) {}

        GridPane grid = new GridPane(); grid.setHgap(8); grid.setVgap(8);
        grid.addRow(0, nameL, nameF);
        grid.addRow(1, surnameL, surnameF);
        grid.addRow(2, roleL, roleBox);
        grid.addRow(3, approvedL, approvedBox);
        grid.addRow(4, cL, cF);
        grid.addRow(5, sL, sF);
        grid.addRow(6, cityL, cityF);
        grid.addRow(7, streetL, streetF);
        grid.addRow(8, houseL, houseF);
        grid.addRow(9, zipL, zipF);

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
                    "UPDATE \"User\" SET NAME = ?, SURNAME = ?, ROLE_ID = ?, ADDRESS_ID = ?, APPROVED = ? WHERE USER_ID = ?")) {
                ps.setString(1, res.name);
                ps.setString(2, res.surname);
                ps.setInt(3, res.role.id);
                ps.setInt(4, newAddrId);
                ps.setString(5, approvedBox.getValue());
                ps.setInt(6, row.userId.get());
                ps.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            showError("Error updating user: " + e.getMessage());
        }
        reloadUsers();
    }

    // Helpers to load all users for combos
    private ObservableList<UserOption> loadAllUsers(){
        ObservableList<UserOption> list = FXCollections.observableArrayList();
        String sql = "SELECT USER_ID, NAME, SURNAME FROM \"User\" ORDER BY SURNAME, NAME";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while (rs.next()) list.add(new UserOption(rs.getInt(1), rs.getString(2)+" "+rs.getString(3)));
        } catch (SQLException ignore) {}
        return list;
    }

    private void editLogin(LoginRow row){
        // Fetch values
        int id = row.id.get(); Integer userId = null; String ip = row.ip.get(); java.sql.Timestamp ts = null;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT USER_ID, LOGIN_IP_ADDRESS, LOGIN_TIME FROM LOGIN_RECORD WHERE LOGIN_ID = ?")){
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()){ userId = rs.getInt(1); ip = rs.getString(2); ts = rs.getTimestamp(3); }
            }
        } catch (SQLException ignore) {}
        ComboBox<UserOption> userBox = new ComboBox<>(loadAllUsers()); if (userId!=null){ for (UserOption u : userBox.getItems()) if (u.userId==userId){ userBox.getSelectionModel().select(u); break; } }
        TextField ipF = new TextField(ip==null? "" : ip);
        TextField timeF = new TextField(ts==null? "" : ts.toString().substring(0,19));
        Dialog<Boolean> dlg = new Dialog<>(); dlg.setTitle("Edit Login Record");
        GridPane g = new GridPane(); g.setHgap(8); g.setVgap(8);
        g.addRow(0, new Label("User:"), userBox);
        g.addRow(1, new Label("IP:"), ipF);
        g.addRow(2, new Label("Time (yyyy-MM-dd HH:mm:ss):"), timeF);
        dlg.getDialogPane().setContent(g);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        if (!dlg.showAndWait().orElse(false)) return;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement("UPDATE LOGIN_RECORD SET USER_ID=?, LOGIN_IP_ADDRESS=?, LOGIN_TIME=? WHERE LOGIN_ID=?")){
            UserOption u = userBox.getValue(); if (u==null){ showError("User required"); return; }
            ps.setInt(1, u.userId); ps.setString(2, ipF.getText());
            java.sql.Timestamp nts; try { nts = java.sql.Timestamp.valueOf(timeF.getText().trim()); } catch (Exception ex){ nts = new java.sql.Timestamp(System.currentTimeMillis()); }
            ps.setTimestamp(3, nts); ps.setInt(4, id); ps.executeUpdate();
        } catch (SQLException e){ showError("Error updating login: "+e.getMessage()); }
        reloadLogins();
    }

    private void editAudit(AuditRow row){
        int id = row.id.get(); Integer userId = null; String change = row.changeType.get(); java.sql.Timestamp ts = null;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT USER_ID, CHANGE_TYPE, CHANGE_TIME FROM AUDIT_LOG WHERE AUDIT_ID=?")){
            ps.setInt(1, id); try (ResultSet rs = ps.executeQuery()){ if (rs.next()){ userId = rs.getInt(1); change = rs.getString(2); ts = rs.getTimestamp(3);} }
        } catch (SQLException ignore) {}
        ComboBox<UserOption> userBox = new ComboBox<>(loadAllUsers()); if (userId!=null){ for (UserOption u : userBox.getItems()) if (u.userId==userId){ userBox.getSelectionModel().select(u); break; } }
        TextField changeF = new TextField(change==null? "" : change);
        TextField timeF = new TextField(ts==null? "" : ts.toString().substring(0,19));
        Dialog<Boolean> dlg = new Dialog<>(); dlg.setTitle("Edit Audit Log"); GridPane g = new GridPane(); g.setHgap(8); g.setVgap(8);
        g.addRow(0, new Label("User:"), userBox);
        g.addRow(1, new Label("Change type:"), changeF);
        g.addRow(2, new Label("Time (yyyy-MM-dd HH:mm:ss):"), timeF);
        dlg.getDialogPane().setContent(g); dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        if (!dlg.showAndWait().orElse(false)) return;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement("UPDATE AUDIT_LOG SET USER_ID=?, CHANGE_TYPE=?, CHANGE_TIME=? WHERE AUDIT_ID=?")){
            UserOption u = userBox.getValue(); if (u==null){ showError("User required"); return; }
            ps.setInt(1, u.userId); ps.setString(2, changeF.getText());
            java.sql.Timestamp nts; try { nts = java.sql.Timestamp.valueOf(timeF.getText().trim()); } catch (Exception ex){ nts = new java.sql.Timestamp(System.currentTimeMillis()); }
            ps.setTimestamp(3, nts); ps.setInt(4, id); ps.executeUpdate();
        } catch (SQLException e){ showError("Error updating audit: "+e.getMessage()); }
        reloadAudits();
    }

    private void editDocument(DocumentRow row){
        int id = row.documentId.get(); String fn = row.fileName.get(); String ext = row.extension.get(); Integer userId = null;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT FILE_NAME, FILE_EXTENSION, USER_ID FROM DOCUMENT WHERE DOCUMENT_ID=?")){
            ps.setInt(1, id); try (ResultSet rs = ps.executeQuery()){ if (rs.next()){ fn = rs.getString(1); ext = rs.getString(2); userId = rs.getInt(3);} }
        } catch (SQLException ignore) {}
        TextField fnF = new TextField(fn==null? "" : fn);
        TextField extF = new TextField(ext==null? "" : ext);
        ComboBox<UserOption> userBox = new ComboBox<>(loadAllUsers()); if (userId!=null){ for (UserOption u : userBox.getItems()) if (u.userId==userId){ userBox.getSelectionModel().select(u); break; } }
        Dialog<ButtonType> dlg = new Dialog<>(); dlg.setTitle("Edit Document"); GridPane g = new GridPane(); g.setHgap(8); g.setVgap(8);
        g.addRow(0, new Label("File name:"), fnF);
        g.addRow(1, new Label("Extension:"), extF);
        g.addRow(2, new Label("Owner:"), userBox);
        dlg.getDialogPane().setContent(g); dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        if (dlg.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try (Connection conn = ConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement("UPDATE DOCUMENT SET FILE_NAME=?, FILE_EXTENSION=?, USER_ID=? WHERE DOCUMENT_ID=?")){
            UserOption u = userBox.getValue(); if (u==null){ showError("Owner required"); return; }
            ps.setString(1, fnF.getText()); ps.setString(2, extF.getText()); ps.setInt(3, u.userId); ps.setInt(4, id); ps.executeUpdate();
        } catch (SQLException e){ showError("Error updating document: "+e.getMessage()); }
        reloadDocuments();
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
        final SimpleStringProperty parentNameProperty = new SimpleStringProperty();
        final SimpleStringProperty addressTextProperty = new SimpleStringProperty();
        public BranchRow(int id, String name, String parentName, String addressText) {
            branchId.set(id);
            branchNameProperty.set(name);
            parentNameProperty.set(parentName == null ? "" : parentName);
            addressTextProperty.set(addressText);
        }
    }

    public static class UserRow {
        final SimpleIntegerProperty userId = new SimpleIntegerProperty();
        final SimpleStringProperty nameProperty = new SimpleStringProperty();
        final SimpleStringProperty surnameProperty = new SimpleStringProperty();
        final SimpleStringProperty roleNameProperty = new SimpleStringProperty();
        final SimpleStringProperty approvedProperty = new SimpleStringProperty();
        final SimpleStringProperty addressTextProperty = new SimpleStringProperty();
        public UserRow(int id, String name, String surname, String role, String approved, String address) {
            userId.set(id);
            nameProperty.set(name);
            surnameProperty.set(surname);
            roleNameProperty.set(role);
            approvedProperty.set(approved);
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

    // New simple row models for added admin sections
    public static class RoleRow {
        final SimpleIntegerProperty roleId = new SimpleIntegerProperty();
        final SimpleStringProperty name = new SimpleStringProperty();
        final SimpleStringProperty description = new SimpleStringProperty();
        public RoleRow(int id, String n, String d){ this.roleId.set(id); this.name.set(n); this.description.set(d);} }

    public static class AddressRow {
        final SimpleIntegerProperty addressId = new SimpleIntegerProperty();
        final SimpleStringProperty country = new SimpleStringProperty();
        final SimpleStringProperty state = new SimpleStringProperty();
        final SimpleStringProperty city = new SimpleStringProperty();
        final SimpleStringProperty street = new SimpleStringProperty();
        final SimpleIntegerProperty house = new SimpleIntegerProperty();
        final SimpleIntegerProperty zip = new SimpleIntegerProperty();
        final SimpleStringProperty formatted = new SimpleStringProperty();
        public AddressRow(int id, String c, String s, String city, String st, int house, int zip){
            this.addressId.set(id); this.country.set(c); this.state.set(s); this.city.set(city); this.street.set(st); this.house.set(house); this.zip.set(zip);
            this.formatted.set((st!=null? st:"")+" "+house+", "+(city!=null? city:"")+", "+(s!=null? s:"")+", "+(c!=null? c:"")+" "+zip);
        }
    }

    public static class TransactionTypeRow {
        final SimpleIntegerProperty id = new SimpleIntegerProperty();
        final SimpleStringProperty name = new SimpleStringProperty();
        final SimpleStringProperty description = new SimpleStringProperty();
        public TransactionTypeRow(int id, String n, String d){ this.id.set(id); this.name.set(n); this.description.set(d);} }

    public static class LoginRow {
        final SimpleIntegerProperty id = new SimpleIntegerProperty();
        final SimpleStringProperty userName = new SimpleStringProperty();
        final SimpleStringProperty ip = new SimpleStringProperty();
        final SimpleStringProperty time = new SimpleStringProperty();
        public LoginRow(int id, String user, String ip, String time){ this.id.set(id); this.userName.set(user); this.ip.set(ip); this.time.set(time);} }

    public static class AuditRow {
        final SimpleIntegerProperty id = new SimpleIntegerProperty();
        final SimpleStringProperty userName = new SimpleStringProperty();
        final SimpleStringProperty changeType = new SimpleStringProperty();
        final SimpleStringProperty time = new SimpleStringProperty();
        public AuditRow(int id, String user, String type, String time){ this.id.set(id); this.userName.set(user); this.changeType.set(type); this.time.set(time);} }

    public static class DocumentRow {
        final SimpleIntegerProperty documentId = new SimpleIntegerProperty();
        final SimpleStringProperty fileName = new SimpleStringProperty();
        final SimpleStringProperty extension = new SimpleStringProperty();
        final SimpleStringProperty userName = new SimpleStringProperty();
        public DocumentRow(int id, String fn, String ext, String user){ this.documentId.set(id); this.fileName.set(fn); this.extension.set(ext); this.userName.set(user);} }

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

    private static class RoleEditResult { final String name; final String description; RoleEditResult(String n, String d){ this.name=n; this.description=d; } }
    private static class AddressEditResult { final String country, state, city, street, house, zip; AddressEditResult(String c, String s, String ci, String st, String h, String z){ this.country=c; this.state=s; this.city=ci; this.street=st; this.house=h; this.zip=z; } }

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
        Label parentL = new Label("Parent branch:");
        ObservableList<BranchOption> allBranches = loadBranches();
        ComboBox<BranchOption> parentBox = new ComboBox<>(FXCollections.observableArrayList());
        parentBox.getItems().add(null); // empty option
        parentBox.getItems().addAll(allBranches);
        parentBox.getSelectionModel().select(null);
        GridPane grid=new GridPane(); grid.setHgap(8); grid.setVgap(8);
        grid.addRow(0,new Label("Branch name:"), nameF);
        grid.addRow(1,new Label("Country:"), cF);
        grid.addRow(2,new Label("State:"), sF);
        grid.addRow(3,new Label("City:"), cityF);
        grid.addRow(4,new Label("Street:"), streetF);
        grid.addRow(5,new Label("House No:"), houseF);
        grid.addRow(6,new Label("ZIP:"), zipF);
        grid.addRow(7,parentL, parentBox);
        dlg.getDialogPane().setContent(grid); dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(b-> b==ButtonType.OK? new BranchEditResult(nameF.getText(), cF.getText(), sF.getText(), cityF.getText(), streetF.getText(), houseF.getText(), zipF.getText()):null);
        BranchEditResult res = dlg.showAndWait().orElse(null); if (res==null) return;
        Connection conn2 = null; boolean prevAuto2 = true;
        try {
            conn2 = ConnectionSingleton.getInstance().getConnection();
            prevAuto2 = conn2.getAutoCommit();
            conn2.setAutoCommit(false);
            int addrId;
            try (PreparedStatement ps = conn2.prepareStatement("INSERT INTO ADDRESS(ADDRESS_ID, COUNTRY, STATE, CITY, STREET, HOUSE_NUMBER, ZIP_CODE) VALUES(ADDRESS_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?)")){
                ps.setString(1,res.country); ps.setString(2,res.state); ps.setString(3,res.city); ps.setString(4,res.street); ps.setInt(5, parseIntSafe(res.houseNumber)); ps.setInt(6, parseIntSafe(res.zip)); ps.executeUpdate();
            }
            try (PreparedStatement ps = conn2.prepareStatement("SELECT ADDRESS_SEQ.CURRVAL AS ID FROM dual"); ResultSet rs = ps.executeQuery()){ rs.next(); addrId = rs.getInt("ID"); }
            try (PreparedStatement ps = conn2.prepareStatement("INSERT INTO BRANCH(BRANCH_ID, BRANCH_NAME, ADDRESS_ID, PARENT_BRANCH_ID) VALUES(BRANCH_SEQ.NEXTVAL, ?, ?, ?)")){
                ps.setString(1, res.branchName); ps.setInt(2, addrId);
                BranchOption sel = parentBox.getValue();
                if (sel == null) ps.setNull(3, java.sql.Types.INTEGER); else ps.setInt(3, sel.branchId);
                ps.executeUpdate();
            }
            conn2.commit();
        } catch (SQLException e){
            if (conn2 != null) { try { conn2.rollback(); } catch (SQLException ignore) {} }
            showError("Error adding branch: "+e.getMessage());
        } finally {
            if (conn2 != null) { try { conn2.setAutoCommit(prevAuto2); } catch (SQLException ignore) {} try { conn2.close(); } catch (SQLException ignore) {} }
        }
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
        Connection conn = null; boolean prevAuto = true;
        try {
            conn = ConnectionSingleton.getInstance().getConnection();
            prevAuto = conn.getAutoCommit();
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
        } catch (SQLException e){
            if (conn != null) { try { conn.rollback(); } catch (SQLException ignore) {} }
            showError("Error adding user: "+e.getMessage());
        } finally {
            if (conn != null) { try { conn.setAutoCommit(prevAuto); } catch (SQLException ignore) {} try { conn.close(); } catch (SQLException ignore) {} }
        }
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
        Connection conn = null; boolean prevAuto = true;
        try {
            conn = ConnectionSingleton.getInstance().getConnection();
            prevAuto = conn.getAutoCommit();
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
        } catch (SQLException e){
            if (conn != null) { try { conn.rollback(); } catch (SQLException ignore) {} }
            showError("Error adding client: "+e.getMessage());
        } finally {
            if (conn != null) { try { conn.setAutoCommit(prevAuto); } catch (SQLException ignore) {} try { conn.close(); } catch (SQLException ignore) {} }
        }
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
        Connection conn = null; boolean prevAuto = true;
        try {
            conn = ConnectionSingleton.getInstance().getConnection();
            prevAuto = conn.getAutoCommit();
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
        } catch (SQLException e){
            if (conn != null) { try { conn.rollback(); } catch (SQLException ignore) {} }
            showError("Error adding teller: "+e.getMessage());
        } finally {
            if (conn != null) { try { conn.setAutoCommit(prevAuto); } catch (SQLException ignore) {} try { conn.close(); } catch (SQLException ignore) {} }
        }
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
        Dialog<TransactionEditResult> dlg = new Dialog<>();
        dlg.setTitle("Add Transaction");

        // Typ transakce je vždy dostupný pro admina lokálně (nezávisle na číselníku)
        ComboBox<String> typeBox = new ComboBox<>(FXCollections.observableArrayList("TRANSFER", "WITHDRAW", "DEPOSIT"));
        typeBox.getSelectionModel().selectFirst();

        TextField amountF = new TextField();
        ComboBox<AccountOption> fromBox = new ComboBox<>(loadAccounts());
        ComboBox<AccountOption> toBox = new ComboBox<>(loadAccounts());

        // Additional fields requested: messages and time
        TextField msgSenderF = new TextField();
        msgSenderF.setPromptText("Message for Sender (optional)");
        TextField msgRecipientF = new TextField();
        msgRecipientF.setPromptText("Message for Recipient (optional)");

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Date (optional)");
        TextField timeField = new TextField();
        timeField.setPromptText("Time HH:mm (optional)");

        // Dynamické povolení/zakázání polí podle typu
        typeBox.valueProperty().addListener((obs, oldV, newV) -> {
            boolean isWithdraw = "WITHDRAW".equalsIgnoreCase(newV);
            boolean isDeposit  = "DEPOSIT".equalsIgnoreCase(newV);
            // WITHDRAW: zakázat To (prázdné) ; DEPOSIT: zakázat From (prázdné)
            toBox.setDisable(isWithdraw);
            if (isWithdraw) { toBox.getSelectionModel().clearSelection(); }
            fromBox.setDisable(isDeposit);
            if (isDeposit) { fromBox.getSelectionModel().clearSelection(); }
        });
        // Inicializace stavu podle defaultního typu
        typeBox.getSelectionModel().select("TRANSFER");

        GridPane grid = new GridPane();
        grid.setHgap(8); grid.setVgap(8);
        grid.addRow(0, new Label("Type:"), typeBox);
        grid.addRow(1, new Label("Amount:"), amountF);
        grid.addRow(2, new Label("From Account:"), fromBox);
        grid.addRow(3, new Label("To Account:"), toBox);
        grid.addRow(4, new Label("Message (Sender):"), msgSenderF);
        grid.addRow(5, new Label("Message (Recipient):"), msgRecipientF);
        grid.addRow(6, new Label("Transaction time:"), new HBox(6, datePicker, timeField));

        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dlg.setResultConverter(b -> {
            if (b != ButtonType.OK) return null;
            String t = typeBox.getValue();
            String amt = amountF.getText();
            AccountOption from = fromBox.isDisabled() ? null : fromBox.getValue();
            AccountOption to   = toBox.isDisabled()   ? null : toBox.getValue();
            // We will pass messages/time via outer scope variables (captured below)
            return new TransactionEditResult(t, amt, from, to);
        });

        TransactionEditResult res = dlg.showAndWait().orElse(null);
        if (res == null) return;

        // Validace dle typu
        if (res.amount == null || res.amount.trim().isEmpty()) { showError("Amount is required."); return; }
        java.math.BigDecimal amtBd;
        try {
            amtBd = new java.math.BigDecimal(res.amount.trim());
            if (amtBd.compareTo(java.math.BigDecimal.ZERO) <= 0) { showError("Amount must be positive."); return; }
        } catch (NumberFormatException ex) { showError("Invalid amount format."); return; }

        boolean isTransfer = "TRANSFER".equalsIgnoreCase(res.typeName);
        boolean isWithdraw = "WITHDRAW".equalsIgnoreCase(res.typeName);
        boolean isDeposit  = "DEPOSIT".equalsIgnoreCase(res.typeName);

        if (isTransfer) {
            if (res.fromAcc == null || res.toAcc == null) { showError("Both From and To accounts are required for TRANSFER."); return; }
            if (res.fromAcc.accountId == res.toAcc.accountId) { showError("From and To accounts cannot be the same."); return; }
        } else if (isWithdraw) {
            if (res.fromAcc == null) { showError("From account is required for WITHDRAW."); return; }
        } else if (isDeposit) {
            if (res.toAcc == null) { showError("To account is required for DEPOSIT."); return; }
        }

        try (Connection conn = ConnectionSingleton.getInstance().getConnection()){
            // Získat ID typu z číselníku, pokud existuje; jinak fallback na TRANSFER
            Integer typeId = null;
            try {
                typeId = findTransactionTypeId(res.typeName);
            } catch (SQLException ignore) {
                try { typeId = findTransactionTypeId("TRANSFER"); } catch (SQLException ignore2) { /* ponecháme null */ }
            }

            if (typeId == null) {
                showError("Transaction type not configured (expected at least TRANSFER).");
                return;
            }

            // Build transaction time from user input if provided, else NOW
            java.sql.Timestamp whenTs;
            java.time.LocalDate d = datePicker.getValue();
            String timeStr = timeField.getText();
            if (d != null) {
                int hh = 0, mm = 0;
                if (timeStr != null && !timeStr.trim().isEmpty()) {
                    try {
                        String[] p = timeStr.trim().split(":");
                        if (p.length >= 1) hh = Integer.parseInt(p[0]);
                        if (p.length >= 2) mm = Integer.parseInt(p[1]);
                        if (hh < 0 || hh > 23 || mm < 0 || mm > 59) throw new NumberFormatException();
                    } catch (NumberFormatException ex) {
                        showError("Invalid time format. Use HH:mm.");
                        return;
                    }
                }
                whenTs = java.sql.Timestamp.valueOf(java.time.LocalDateTime.of(d, java.time.LocalTime.of(hh, mm)));
            } else {
                whenTs = new java.sql.Timestamp(System.currentTimeMillis());
            }

            String msgSender = (msgSenderF.getText() == null) ? null : msgSenderF.getText().trim();
            String msgRecipient = (msgRecipientF.getText() == null) ? null : msgRecipientF.getText().trim();

            String sql = "INSERT INTO TRANSACTION(TRANSACTION_ID, TRANSFER_AMOUNT, MESSAGE_FOR_SENDER, MESSAGE_FOR_RECIPIENT, TRANSACTION_TYPE_ID, TRANSACTION_TIME, ACCOUNT_FROM_ID, ACCOUNT_TO_ID) " +
                         "VALUES(TRANSACTION_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setBigDecimal(1, amtBd);
                // messages (nullable)
                if (msgSender == null || msgSender.isEmpty()) ps.setNull(2, java.sql.Types.VARCHAR); else ps.setString(2, msgSender);
                if (msgRecipient == null || msgRecipient.isEmpty()) ps.setNull(3, java.sql.Types.VARCHAR); else ps.setString(3, msgRecipient);
                // type id (NOT NULL)
                ps.setInt(4, typeId);
                // time
                ps.setTimestamp(5, whenTs);
                // from
                if (isDeposit || res.fromAcc == null) ps.setNull(6, java.sql.Types.INTEGER); else ps.setInt(6, res.fromAcc.accountId);
                // to
                if (isWithdraw || res.toAcc == null) ps.setNull(7, java.sql.Types.INTEGER); else ps.setInt(7, res.toAcc.accountId);

                ps.executeUpdate();
            }
        } catch (SQLException e){
            showError("Error adding transaction: " + e.getMessage());
            return;
        }
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
    private static class TellerEditResult {
        final BranchOption branch; final String phone, email, country,state,city,street,house,zip;
        TellerEditResult(BranchOption b, String ph, String em, String c1,String c2,String c3,String st,String h,String z){
            branch=b; phone=ph; email=em; country=c1; state=c2; city=c3; street=st; house=h; zip=z;
        }
    }
    private static class UserAddResult { final String name,surname,password; final boolean active; final RoleOption role; final String country,state,city,street,house,zip; UserAddResult(String n,String s,String p,boolean a,RoleOption r,String c1,String c2,String c3,String st,String h,String z){name=n;surname=s;password=p;active=a;role=r;country=c1;state=c2;city=c3;street=st;house=h;zip=z;} }
    private static class ClientAddResult { final String name,surname,password,birth,phone,email; final UserOption teller; final String country,state,city,street,house,zip; ClientAddResult(String n,String s,String p,String b,String ph,String e,UserOption t,String c1,String c2,String c3,String st,String h,String z){name=n;surname=s;password=p;birth=b;phone=ph;email=e;teller=t;country=c1;state=c2;city=c3;street=st;house=h;zip=z;} }
    private static class TellerAddResult { final String name,surname,password,phone,email; final BranchOption branch; final String country,state,city,street,house,zip; TellerAddResult(String n,String s,String p,String ph,String e,BranchOption b,String c1,String c2,String c3,String st,String h,String z){name=n;surname=s;password=p;phone=ph;email=e;branch=b;country=c1;state=c2;city=c3;street=st;house=h;zip=z;} }
    private static class AccountEditResult { final UserOption client; final String number,balance; AccountEditResult(UserOption c,String n,String b){client=c;number=n;balance=b;} }
    private static class TransactionEditResult { final String typeName; final String amount; final AccountOption fromAcc,toAcc; TransactionEditResult(String t,String a,AccountOption f,AccountOption to){typeName=t;amount=a;fromAcc=f;toAcc=to;} }
    private static class MessageAddResult { final UserOption from,to; final String text; MessageAddResult(UserOption f,UserOption t,String tx){from=f;to=t;text=tx;} }
}
