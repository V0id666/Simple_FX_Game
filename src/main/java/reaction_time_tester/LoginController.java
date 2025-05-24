package reaction_time_tester;

import javafx.scene.Scene;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    public void initialize() {
        DatabaseManager.initDatabase();
    }

    @FXML
    public void handleLogin() {
        // Load username + password
        // Hash it
        // Check DB for a match
        // If success → open Dashboard.fxml
        String username = usernameField.getText();
        String password = passwordField.getText();
        String hash = HashUtil.hashPassword(password);

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM players WHERE username=? AND password_hash=?")) {
            stmt.setString(1, username);
            stmt.setString(2, hash);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Player player = new Player(rs.getInt("id"), rs.getString("username"));
                openDashboard(player);
            } else {
                System.out.println("Invalid login");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRegister() {
        // Load username + password
        // Hash it
        // Try to INSERT into DB
        // Catch duplicate usernames
        String username = usernameField.getText();
        String password = passwordField.getText();
        String hash = HashUtil.hashPassword(password);

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO players(username, password_hash) VALUES (?, ?);")) {
            stmt.setString(1, username);
            stmt.setString(2, hash);
            stmt.executeUpdate();
            System.out.println("Registration successful");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openDashboard(Player player) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/reaction_time_tester/Dashboard.fxml"));
        Parent root = loader.load();  // <- wrap this in try-catch to debug if needed
        DashboardController controller = loader.getController();
        controller.setPlayer(player);

        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(new Scene(root, 300, 200));
        stage.show();
    }

}

