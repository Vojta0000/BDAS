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

    @FXML
    private void initialize() {
        // Show branches by default when admin view loads
        showOnly(branchesSection);
        setupBranchesTable();
        setupUsersTable();
    }

    public void onLoginLoaded() {
        // Called from AppViewController after successful admin login
        reloadBranches();
    }

    // Navigation
    @FXML private void showBranches() { showOnly(branchesSection); reloadBranches(); }
    @FXML private void showUsers() { showOnly(usersSection); reloadUsers(); }
    @FXML private void showClients() { showOnly(clientsSection); /* TODO */ }
    @FXML private void showTellers() { showOnly(tellersSection); /* TODO */ }
    @FXML private void showAccounts() { showOnly(accountsSection); /* TODO */ }
    @FXML private void showTransactions() { showOnly(transactionsSection); /* TODO */ }
    @FXML private void showLogins() { showOnly(loginsSection); /* TODO */ }
    @FXML private void showMessages() { showOnly(messagesSection); /* TODO */ }
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
                    ToolBar bar = new ToolBar(editBtn, deleteBtn);
                    setGraphic(bar);
                }
            }
        });
        actionsCol.setPrefWidth(160);

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
                    setGraphic(new ToolBar(editBtn, deleteBtn, emulateBtn));
                }
            }
        });
        actionsCol.setPrefWidth(160);

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
    @FXML private void reloadClients() { /* TODO: implement */ }
    @FXML private void reloadTellers() { /* TODO: implement */ }
    @FXML private void reloadAccounts() { /* TODO: implement */ }
    @FXML private void reloadTransactions() { /* TODO: implement */ }

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

    private static class RoleOption {
        final int id; final String name;
        RoleOption(int id, String name) { this.id = id; this.name = name; }
        @Override public String toString() { return name; }
    }

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
}
