package upce.javafx;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

        // Return to login screen
        showOnly(loginPane);
    }

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

        String userLoginQuery = "SELECT * FROM \"User\" JOIN ROLE ON \"User\".ROLE_ID = ROLE.ROLE_ID WHERE name = ? AND surname = ? AND password = ?";

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
                if (res.equals("Teller")) {
                    isTeller = true;
                } else if (res.equals("Client")) {
                    isTeller = false;
                } else if (res.equals("Admin")) {
                    // Route to Admin view; keep isTeller false for legacy flag
                    isTeller = false;
                    HelloApplication.userId = rs.getInt("USER_ID");
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

            String sql = """
                    SELECT u.NAME, u.SURNAME, t.WORK_PHONE_NUMBER, t.WORK_EMAIL_ADDRESS, u.USER_ID 
                    FROM "User" u 
                    JOIN TELLER t ON u.USER_ID = t.USER_ID 
                    WHERE u.USER_ID = ?
                """;

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
            String sql = "SELECT * FROM \"User\" JOIN CLIENT ON \"User\".USER_ID = CLIENT.USER_ID WHERE CLIENT.USER_ID = ?";
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

            sql = "SELECT * FROM CLIENT JOIN TELLER ON CLIENT.TELLER_ID = TELLER.USER_ID JOIN \"User\" u ON TELLER_ID = u.USER_ID JOIN BRANCH ON BRANCH.BRANCH_ID = TELLER.BRANCH_ID WHERE CLIENT.USER_ID = ?";
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

            sql = "SELECT ACCOUNT_ID, ACCOUNT_NUMBER FROM ACCOUNT WHERE CLIENT_ID = ?";
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

    // ===== Emulation API for Admin =====
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

    private void setVisibleManaged(Node node, boolean value) {
        if (node == null) return;
        node.setVisible(value);
        node.setManaged(value);
    }

    private boolean isEmpty(String s) { return s == null || s.trim().isEmpty(); }

    // Delegation area — expose key functionality from sub-controllers so
    // the app can interact with client/teller features through this root controller.

    // --- Client view helpers ---
    public void updateClientProfile(String name, String surname, String birthNumber, String phone, String email) {
        if (clientViewController != null) {
            clientViewController.updateProfileInfo(name, surname, birthNumber, phone, email);
        }
    }

    public void updateClientTellerSection(String name, String surname, String phone, String email, String branch) {
        if (clientViewController != null) {
            clientViewController.updateTellerSection(name, surname, phone, email, branch);
        }
    }

    // --- Teller view helpers --- (demonstrative)
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
