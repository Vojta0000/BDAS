package upce.javafx;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.net.InetAddress;

/**
 * Root controller that combines a simple login screen with both Client and Teller views.
 *
 * The actual role-selection logic is intentionally simplified: a boolean flag is chosen when
 * pressing the Login button. For demo, we use the checkbox value to decide the role.
 *
 * Both existing views are included via fx:include in app-view.fxml and their controllers are
 * injected here so this controller can delegate functionality to them as needed.
 */
public class AppViewController {

    // Login layer controls
    @FXML private VBox loginPane;
    @FXML private TextField nameField;
    @FXML private TextField surnameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label loginErrorLabel;

    // Included views' root nodes
    // Note: names must match fx:id in app-view.fxml
    @FXML private Node clientView;
    @FXML private Node tellerView;
    @FXML private Node adminView;

    // Included views' controllers are injected into fields named fxid + "Controller"
    @FXML private ClientViewController clientViewController; // from fx:id "clientView"
    @FXML private TellerViewController tellerViewController; // from fx:id "tellerView"
    @FXML private AdminViewController adminViewController;   // from fx:id "adminView"

    // Simple flag to decide which view to show after login
    private boolean isTeller = false;
    // Emulation state
    private boolean isEmulating = false;
    private int adminSavedUserId = -1;

    /**
     * Initializes the controller, sets up initial visibility of UI components.
     */
    @FXML
    private void initialize() {
        // Start with only login visible; includes are hidden in FXML via visible/managed flags.
        showOnly(loginPane);

        // Inject this main controller into the sub-controllers so they can call logout()
        if (clientViewController != null) {
            clientViewController.setAppViewController(this);
        }
        if (tellerViewController != null) {
            tellerViewController.setAppViewController(this);
        }
        if (adminViewController != null) {
            adminViewController.setAppViewController(this);
        }
    }

    /**
     * Logs out the current user and returns to the login screen.
     */
    public void logout() {
        // Clear login fields for security
        nameField.clear();
        surnameField.clear();
        passwordField.clear();
        loginErrorLabel.setVisible(false);
        loginErrorLabel.setManaged(false);

        // Reset global user ID
        HelloApplication.userId = 0;
        isEmulating = false;
        adminSavedUserId = -1;

        // Ensure stop buttons are hidden for next session
        if (clientViewController != null) clientViewController.setEmulationMode(false);
        if (tellerViewController != null) tellerViewController.setEmulationMode(false);

        // Return to login screen
        showOnly(loginPane);
    }

    /**
     * Handles the login logic, verifies credentials, and switches to the appropriate view based on user role.
     */
    @FXML
    private void onLogin() {
        // In real app you would authenticate here.
        // For now, just use the checkbox to decide role; keep a simple boolean as requested.
//        isTeller = loginAsTellerCheck.isSelected();

        // Optionally, you could validate non-empty username/password
        if (isEmpty(nameField.getText()) || isEmpty(surnameField.getText()) || isEmpty(passwordField.getText())) {
            loginErrorLabel.setText("Please enter name, surname and password.");
            loginErrorLabel.setVisible(true);
            loginErrorLabel.setManaged(true);
            return;
        }

        Connection conn;
        try {
            conn = ConnectionSingleton.getInstance().getConnection();
        } catch (Exception e) {
            System.out.println("Connection error");
            return;
        }

        // Use view for login: v_user_with_role
        String userLoginQuery = "SELECT * FROM v_user_with_role WHERE NAME = ? AND SURNAME = ? AND PASSWORD = ?";

        try (PreparedStatement stmt = conn.prepareStatement(userLoginQuery)) {
            stmt.setString(1, nameField.getText());
            stmt.setString(2, surnameField.getText());
            stmt.setString(3, passwordField.getText());

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    loginErrorLabel.setText("Invalid username or password.");
                    loginErrorLabel.setVisible(true);
                    loginErrorLabel.setManaged(true);
                    return;
                }
                String res = rs.getString("ROLE_NAME");
                String approved = null;
                try { approved = rs.getString("APPROVED"); } catch (SQLException ignore) {}

                // If not admin, require approved = 'Y'
                if (!"Admin".equals(res)) {
                    if (approved == null || !"Y".equalsIgnoreCase(approved)) {
                        loginErrorLabel.setText("Your account is pending approval by a teller.");
                        loginErrorLabel.setVisible(true);
                        loginErrorLabel.setManaged(true);
                        return;
                    }
                }
                if (res.equals("Teller")) {
                    isTeller = true;
                } else if (res.equals("Client")) {
                    isTeller = false;
                } else if (res.equals("Admin")) {
                    // Route to Admin view; keep isTeller false for legacy flag
                    isTeller = false;
                    HelloApplication.userId = rs.getInt("USER_ID");
                    // Log successful login (Admin)
                    logLogin(HelloApplication.userId);
                    // Show admin view and initialize
                    loginErrorLabel.setVisible(false);
                    loginErrorLabel.setManaged(false);
                    showOnly(adminView);
                    if (adminViewController != null) {
                        adminViewController.onLoginLoaded();
                    }
                    return;
                } else {
                    throw new RuntimeException("Unexpected role: " + res);
                }
                HelloApplication.userId = rs.getInt("USER_ID");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        loginErrorLabel.setVisible(false);
        loginErrorLabel.setManaged(false);

        // Ensure emulation mode is off for fresh login
        if (clientViewController != null) clientViewController.setEmulationMode(false);
        if (tellerViewController != null) tellerViewController.setEmulationMode(false);

        if (isTeller) {
            // Show teller view
            showOnly(tellerView);

            tellerViewController.showSection("profile");

            tellerViewController.loadMyClients();
            // You could trigger teller default section here if needed.
            // Update Teller Profile
            try {
                conn = ConnectionSingleton.getInstance().getConnection();
            } catch (Exception e) {
                System.out.println("Connection error");
                return;
            }

            String sql = "SELECT NAME, SURNAME, WORK_PHONE_NUMBER, WORK_EMAIL_ADDRESS, USER_ID FROM v_teller_overview WHERE USER_ID = ?";

            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, HelloApplication.userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        if (tellerViewController != null) {
                            tellerViewController.updateProfileInfo(
                                    rs.getString("NAME"),
                                    rs.getString("SURNAME"),
                                    rs.getString("WORK_PHONE_NUMBER"),
                                    rs.getString("WORK_EMAIL_ADDRESS")
                            );
                        }
                    }
                }
            } catch(SQLException e){
                System.out.println("SQL error loading teller profile: " + e.getMessage());
            }
        } else {
            // Show client view
            showOnly(clientView);
            //fill in the fields with the data from the database
            clientViewController.showSection("profile");

            try {
                conn = ConnectionSingleton.getInstance().getConnection();
            } catch (Exception e) {
                System.out.println("Connection error");
                return;
            }
            String sql = "SELECT NAME, SURNAME, BIRTH_NUMBER, PHONE_NUMBER, EMAIL_ADDRESS FROM v_client_overview WHERE USER_ID = ?";
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, HelloApplication.userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        return;
                    }
                    updateClientProfile(rs.getString("NAME"), rs.getString("SURNAME"), rs.getString("BIRTH_NUMBER"), rs.getString("PHONE_NUMBER"), rs.getString("EMAIL_ADDRESS"));
                }

            }catch(SQLException e){
                System.out.println("SQL error");
                return;
            }

            sql = "SELECT TELLER_NAME AS NAME, TELLER_SURNAME AS SURNAME, TELLER_WORK_PHONE AS WORK_PHONE_NUMBER, TELLER_WORK_EMAIL AS WORK_EMAIL_ADDRESS, TELLER_BRANCH_NAME AS BRANCH_NAME FROM v_client_overview WHERE USER_ID = ?";
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, HelloApplication.userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        return;
                    }
                    updateClientTellerSection(rs.getString("NAME"), rs.getString("SURNAME"), rs.getString("WORK_PHONE_NUMBER"), rs.getString("WORK_EMAIL_ADDRESS"), rs.getString("BRANCH_NAME"));
                }

            }catch(SQLException e){
                System.out.println("SQL error");
                return;
            }

            sql = "SELECT ACCOUNT_ID, ACCOUNT_NUMBER FROM v_account_list WHERE CLIENT_ID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, HelloApplication.userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (clientViewController != null) {
                        clientViewController.clearAccountButtons();
                        while (rs.next()) {
                            int accId = rs.getInt("ACCOUNT_ID");
                            String accNum = rs.getString("ACCOUNT_NUMBER");
                            clientViewController.addAccountButton("Account " + accNum, accId);
                        }
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error loading accounts: " + e.getMessage());
            }
            // You could trigger client default section here if needed.
        }
    }

    /**
     * Handles the request for a new account from the login screen.
     */
    @FXML
    private void onRequestAccount() {
        // Build a simple registration dialog mirroring teller registration fields
        Dialog<Boolean> dlg = new Dialog<>();
        dlg.setTitle("Request an account");

        TextField nameF = new TextField();
        TextField surnameF = new TextField();
        PasswordField passF = new PasswordField();
        TextField birthF = new TextField();
        TextField phoneF = new TextField();
        TextField emailF = new TextField();
        TextField streetF = new TextField();
        TextField houseF = new TextField();
        TextField cityF = new TextField();
        TextField zipF = new TextField();
        TextField countryF = new TextField("Czechia");

        GridPane grid = new GridPane();
        grid.setHgap(8); grid.setVgap(8);
        grid.addRow(0, new Label("Name:"), nameF);
        grid.addRow(1, new Label("Surname:"), surnameF);
        grid.addRow(2, new Label("Password:"), passF);
        grid.addRow(3, new Label("Birth number:"), birthF);
        grid.addRow(4, new Label("Phone:"), phoneF);
        grid.addRow(5, new Label("Email:"), emailF);
        grid.addRow(6, new Label("Street:"), streetF);
        grid.addRow(7, new Label("House #:"), houseF);
        grid.addRow(8, new Label("City:"), cityF);
        grid.addRow(9, new Label("ZIP:"), zipF);
        grid.addRow(10, new Label("Country:"), countryF);

        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(bt -> bt == ButtonType.OK);

        Boolean ok = dlg.showAndWait().orElse(false);
        if (!ok) return;

        // Basic validation
        if (isEmpty(nameF.getText()) || isEmpty(surnameF.getText()) || isEmpty(passF.getText()) ||
                isEmpty(birthF.getText()) || isEmpty(phoneF.getText()) || isEmpty(emailF.getText()) ||
                isEmpty(streetF.getText()) || isEmpty(houseF.getText()) || isEmpty(cityF.getText()) || isEmpty(zipF.getText())) {
            showInfo("Validation", "All fields are required.");
            return;
        }

        int houseNum; int zipNum;
        try {
            houseNum = Integer.parseInt(houseF.getText().trim());
            zipNum = Integer.parseInt(zipF.getText().trim());
        } catch (NumberFormatException nfe) {
            showInfo("Validation", "House number and ZIP must be numbers.");
            return;
        }

        // Insert pending user
        Connection conn = null;
        boolean prevAuto = true;
        try {
            conn = ConnectionSingleton.getInstance().getConnection();
            prevAuto = conn.getAutoCommit();
            conn.setAutoCommit(false);

            // find client role id
            int clientRoleId = -1;
            try (PreparedStatement ps = conn.prepareStatement("SELECT ROLE_ID FROM ROLE WHERE ROLE_NAME = 'Client'")) {
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) clientRoleId = rs.getInt(1); }
            }
            if (clientRoleId == -1) { conn.rollback(); showInfo("Error", "Client role not found."); return; }

            // find a teller to assign
            int tellerId = -1;
            try (PreparedStatement ps = conn.prepareStatement("SELECT USER_ID FROM TELLER FETCH FIRST 1 ROWS ONLY")) {
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) tellerId = rs.getInt(1); }
            }
            if (tellerId == -1) { conn.rollback(); showInfo("Error", "No teller available to assign. Try later."); return; }

            // generate IDs from sequences
            int addressId = -1; int userId = -1;
            try (PreparedStatement ps = conn.prepareStatement("SELECT ADDRESS_SEQ.NEXTVAL FROM DUAL")) {
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) addressId = rs.getInt(1); }
            }
            try (PreparedStatement ps = conn.prepareStatement("SELECT USER_SEQ.NEXTVAL FROM DUAL")) {
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) userId = rs.getInt(1); }
            }

            if (addressId == -1 || userId == -1) { conn.rollback(); showInfo("Error", "Failed to generate IDs."); return; }

            // insert address
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO ADDRESS(ADDRESS_ID, COUNTRY, STATE, CITY, STREET, HOUSE_NUMBER, ZIP_CODE) VALUES (?, ?, NULL, ?, ?, ?, ?)")) {
                ps.setInt(1, addressId);
                ps.setString(2, countryF.getText().trim());
                ps.setString(3, cityF.getText().trim());
                ps.setString(4, streetF.getText().trim());
                ps.setInt(5, houseNum);
                ps.setInt(6, zipNum);
                ps.executeUpdate();
            }

            // insert user (approved = 'N')
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO \"User\"(USER_ID, NAME, SURNAME, PASSWORD, ACTIVE, APPROVED, ROLE_ID, ADDRESS_ID) VALUES (?, ?, ?, ?, 'Y', 'N', ?, ?)")) {
                ps.setInt(1, userId);
                ps.setString(2, nameF.getText().trim());
                ps.setString(3, surnameF.getText().trim());
                ps.setString(4, passF.getText());
                ps.setInt(5, clientRoleId);
                ps.setInt(6, addressId);
                ps.executeUpdate();
            }

            // insert client
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO CLIENT(USER_ID, BIRTH_NUMBER, PHONE_NUMBER, EMAIL_ADDRESS, TELLER_ID) VALUES (?, ?, ?, ?, ?)")) {
                ps.setInt(1, userId);
                ps.setString(2, birthF.getText().trim());
                ps.setString(3, phoneF.getText().trim());
                ps.setString(4, emailF.getText().trim());
                ps.setInt(5, tellerId);
                ps.executeUpdate();
            }

            conn.commit();
            showInfo("Request submitted", "Your request was submitted. A teller will review and approve your account.");
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignore) {}
            }
            showInfo("Error", "Failed to submit request: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(prevAuto); } catch (SQLException ignore) {}
                try { conn.close(); } catch (SQLException ignore) {}
            }
        }
    }

    /**
     * Displays an information alert to the user.
     * @param title Title of the alert.
     * @param msg Message content of the alert.
     */
    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(title);
        a.setTitle(title);
        a.showAndWait();
    }

    // ===== Emulation API for Admin =====
    /**
     * Starts emulation mode as a client.
     * @param clientUserId The user ID of the client to emulate.
     */
    public void startEmulateClient(int clientUserId) {
        // Save admin context
        if (!isEmulating) {
            adminSavedUserId = HelloApplication.userId;
        }
        isEmulating = true;
        HelloApplication.userId = clientUserId;

        // Show client view and load data (reuse login flow logic for client)
        showOnly(clientView);
        if (clientViewController != null) {
            clientViewController.showSection("profile");
            clientViewController.clearAccountButtons(); // FIX: Clear previous user's accounts
        }
        Connection conn;
        try {
            conn = ConnectionSingleton.getInstance().getConnection();
        } catch (Exception e) {
            System.out.println("Connection error");
            return;
        }
        String sql = "SELECT * FROM \"User\" JOIN CLIENT ON \"User\".USER_ID = CLIENT.USER_ID WHERE CLIENT.USER_ID = ?";
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, HelloApplication.userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    updateClientProfile(rs.getString("NAME"), rs.getString("SURNAME"), rs.getString("BIRTH_NUMBER"), rs.getString("PHONE_NUMBER"), rs.getString("EMAIL_ADDRESS"));
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL error");
        }

        sql = "SELECT * FROM CLIENT JOIN TELLER ON CLIENT.TELLER_ID = TELLER.USER_ID JOIN \"User\" u ON TELLER_ID = u.USER_ID JOIN BRANCH ON BRANCH.BRANCH_ID = TELLER.BRANCH_ID WHERE CLIENT.USER_ID = ?";
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, HelloApplication.userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    updateClientTellerSection(rs.getString("NAME"), rs.getString("SURNAME"), rs.getString("WORK_PHONE_NUMBER"), rs.getString("WORK_EMAIL_ADDRESS"), rs.getString("BRANCH_NAME"));
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL error");
        }

        sql = "SELECT ACCOUNT_ID, ACCOUNT_NUMBER FROM ACCOUNT WHERE CLIENT_ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, HelloApplication.userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int accountId = rs.getInt("ACCOUNT_ID");
                    String accountNumber = rs.getString("ACCOUNT_NUMBER");
                    clientViewController.addAccountButton(accountNumber, accountId);
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL error");
        }

        // Show stop emulate button in subview
        if (clientViewController != null) {
            clientViewController.setEmulationMode(true);
        }
    }

    /**
     * Starts emulation mode as a teller.
     * @param tellerUserId The user ID of the teller to emulate.
     */
    public void startEmulateTeller(int tellerUserId) {
        if (!isEmulating) {
            adminSavedUserId = HelloApplication.userId;
        }
        isEmulating = true;
        HelloApplication.userId = tellerUserId;

        showOnly(tellerView);
        if (tellerViewController != null) {
            tellerViewController.showSection("profile");
        }

        Connection conn;
        try {
            conn = ConnectionSingleton.getInstance().getConnection();
        } catch (Exception e) {
            System.out.println("Connection error");
            return;
        }
        String sql = """
                SELECT u.NAME, u.SURNAME, t.WORK_PHONE_NUMBER, t.WORK_EMAIL_ADDRESS, u.USER_ID 
                FROM "User" u 
                JOIN TELLER t ON u.USER_ID = t.USER_ID 
                WHERE u.USER_ID = ?
            """;

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, HelloApplication.userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && tellerViewController != null) {
                    tellerViewController.updateProfileInfo(
                            rs.getString("NAME"),
                            rs.getString("SURNAME"),
                            rs.getString("WORK_PHONE_NUMBER"),
                            rs.getString("WORK_EMAIL_ADDRESS")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL error loading teller profile: " + e.getMessage());
        }
        if (tellerViewController != null) {
            tellerViewController.loadMyClients();
            tellerViewController.setEmulationMode(true);
        }
    }

    /**
     * Stops emulation mode and returns to the admin view.
     */
    public void stopEmulation() {
        if (!isEmulating) return;
        // restore admin
        HelloApplication.userId = adminSavedUserId;
        isEmulating = false;
        adminSavedUserId = -1;
        // Hide stop buttons
        if (clientViewController != null) clientViewController.setEmulationMode(false);
        if (tellerViewController != null) tellerViewController.setEmulationMode(false);
        // Return to admin view
        showOnly(adminView);
        if (adminViewController != null) {
            adminViewController.onLoginLoaded();
        }
    }

    /**
     * Shows only the specified node and hides all others in the main stack.
     * @param toShow The node to display.
     */
    private void showOnly(Node toShow) {
        // login
        setVisibleManaged(loginPane, toShow == loginPane);
        // client
        setVisibleManaged(clientView, toShow == clientView);
        // teller
        setVisibleManaged(tellerView, toShow == tellerView);
        // admin
        setVisibleManaged(adminView, toShow == adminView);
    }

    // --- Login logging helpers ---
    /**
     * Logs the login event into the database.
     * @param userId The ID of the user logging in.
     */
    private void logLogin(int userId) {
        if (userId <= 0) return;
        String ip = getLocalIpAddress();
        String sql = "INSERT INTO LOGIN_RECORD (LOGIN_ID, LOGIN_IP_ADDRESS, USER_ID, LOGIN_TIME) " +
                     "VALUES (LOGIN_SEQ.NEXTVAL, ?, ?, SYSDATE)";
        try (Connection conn = ConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ip);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            // Nezastavovat přihlášení kvůli logování – jen zaznamenat do konzole
            System.out.println("[LoginLog] Failed to insert login record: " + e.getMessage());
        }
    }

    /**
     * Gets the local IP address of the machine.
     * @return The IP address as a string.
     */
    private String getLocalIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }

    /**
     * Sets both visibility and managed state of a node.
     * @param node The node to modify.
     * @param value The value to set for visible and managed properties.
     */
    private void setVisibleManaged(Node node, boolean value) {
        if (node == null) return;
        node.setVisible(value);
        node.setManaged(value);
    }

    /**
     * Checks if a string is null or empty.
     * @param s The string to check.
     * @return True if null or empty, false otherwise.
     */
    private boolean isEmpty(String s) { return s == null || s.trim().isEmpty(); }

    // Delegation area — expose key functionality from sub-controllers so
    // the app can interact with client/teller features through this root controller.

    // --- Client view helpers ---
    /**
     * Updates the client profile information.
     * @param name Name of the client.
     * @param surname Surname of the client.
     * @param birthNumber Birth number of the client.
     * @param phone Phone number of the client.
     * @param email Email address of the client.
     */
    public void updateClientProfile(String name, String surname, String birthNumber, String phone, String email) {
        if (clientViewController != null) {
            clientViewController.updateProfileInfo(name, surname, birthNumber, phone, email);
        }
    }

    /**
     * Updates the client profile information in the teller section.
     * @param name Name of the client.
     * @param surname Surname of the client.
     * @param phone Phone number of the client.
     * @param email Email address of the client.
     * @param branch Branch name.
     */
    public void updateClientTellerSection(String name, String surname, String phone, String email, String branch) {
        if (clientViewController != null) {
            clientViewController.updateTellerSection(name, surname, phone, email, branch);
        }
    }

    // --- Teller view helpers --- (demonstrative)
    /**
     * Opens the client demo view for a teller.
     * @param name Name of the client.
     * @param id ID of the client.
     */
    public void tellerOpenDemoClient(String name, String id) {
        // Expose a simple way to open a client in teller view
        if (tellerViewController != null) {
            // openClient is package-private; add a public wrapper if needed.
            // Here we simulate by selecting sections through existing API where possible.
            try {
                java.lang.reflect.Method m = TellerViewController.class.getDeclaredMethod("openClient", String.class, String.class);
                m.setAccessible(true);
                m.invoke(tellerViewController, name, id);
            } catch (Exception ignored) { }
        }
    }
}
