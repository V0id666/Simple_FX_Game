package reaction_time_tester;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
//import model.Player;
import java.sql.*;
import java.io.IOException;

public class DashboardController {
    private Player player;

    @FXML private Label welcomeLabel;
    @FXML private Label statsLabel;

    public void setPlayer(Player player) {
        this.player = player;
        welcomeLabel.setText("Welcome, " + player.getUsername());
        loadStats();
    }

    private void loadStats() {
        StringBuilder stats = new StringBuilder();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT mode, average_time, fastest_time, attempts, timestamp FROM results WHERE player_id = ? ORDER BY timestamp DESC LIMIT 1")) {
            stmt.setInt(1, player.getId());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                stats.append("Mode: ").append(rs.getString("mode")).append("\n");
                stats.append("Average Time: ").append(rs.getDouble("average_time")).append(" ms\n");
                stats.append("Fastest Time: ").append(rs.getDouble("fastest_time")).append(" ms\n");
                stats.append("Attempts: ").append(rs.getInt("attempts")).append("\n");
                stats.append("Date: ").append(rs.getString("timestamp"));
            } else {
                stats.append("No previous results.");
            }
            statsLabel.setText(stats.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void startEasy() { startGame("Easy"); }
    public void startMedium() { startGame("Medium"); }
    public void startHard() { startGame("Hard"); }

    private void startGame(String mode) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reaction_time_tester/Game.fxml"));
//            Scene scene = new Scene(loader.load());
            Scene scene = new Scene(loader.load(), 600, 400); // width = 600, height = 400
            GameController controller = loader.getController();
            controller.setupGame(player, mode); //setupGame
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
