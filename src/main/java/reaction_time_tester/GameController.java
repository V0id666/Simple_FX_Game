package reaction_time_tester;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameController {
    @FXML private StackPane rootPane;
    @FXML private Label infoLabel;
    @FXML private Button clickButton;

    private Player player;
    private String mode;
    private long startTime;
    private boolean canClick = false;
    private int round = 0;
    private final List<Long> reactionTimes = new ArrayList<>();
    private final Random rand = new Random();

    private int totalRounds;

    public void setupGame(Player player, String mode) {
        this.player = player;
        this.mode = mode;
        totalRounds = mode.equals("Easy") ? 3 : mode.equals("Medium") ? 5 : 7;
        nextRound();
    }

    private void nextRound() {
        if (round >= totalRounds) {
            finishGame();
            return;
        }

        round++;
        canClick = false;
        infoLabel.setText("Wait for green...");
        clickButton.setDisable(true);
        rootPane.setStyle("-fx-background-color: red;");

        int delay = 1000 + rand.nextInt(3000);
        PauseTransition wait = new PauseTransition(Duration.millis(delay));
        wait.setOnFinished(e -> showGo());
        wait.play();
    }

    private void showGo() {
        canClick = true;
        rootPane.setStyle("-fx-background-color: green;");
        clickButton.setDisable(false);
        infoLabel.setText("NOW! Click!");
        startTime = Instant.now().toEpochMilli();

        // Auto penalty timeout (optional)
        if (mode.equals("Hard")) {
            PauseTransition autoFail = new PauseTransition(Duration.seconds(2));
            autoFail.setOnFinished(e -> {
                if (canClick) {
                    infoLabel.setText("Too Slow!");
                    reactionTimes.add(2000L);
                    canClick = false;
                    nextAfterDelay();
                }
            });
            autoFail.play();
        }
    }

    @FXML
    public void handleClick() {
        if (!canClick) {
            if (mode.equals("Hard")) {
                infoLabel.setText("Early click! +1000ms");
                reactionTimes.add(1000L);
                nextAfterDelay();
            } else {
                infoLabel.setText("Too soon! Wait for green.");
            }
            return;
        }

        long reaction = Instant.now().toEpochMilli() - startTime;
        reactionTimes.add(reaction);
        infoLabel.setText("Reaction: " + reaction + " ms");
        canClick = false;
        nextAfterDelay();
    }

    private void nextAfterDelay() {
        clickButton.setDisable(true);
        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(e -> nextRound());
        pause.play();
    }

    private void finishGame() {
        long sum = reactionTimes.stream().mapToLong(Long::longValue).sum();
        long fastest = Collections.min(reactionTimes);
        double average = sum / (double) reactionTimes.size();

        infoLabel.setText("Done! Avg: " + average + " ms, Fastest: " + fastest + " ms");
        clickButton.setDisable(true);
        rootPane.setStyle("-fx-background-color: lightblue;");

        saveResult(average, fastest, reactionTimes.size());
    }

    private void saveResult(double average, long fastest, int attempts) {
        new Thread(() -> {
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO results(player_id, mode, average_time, fastest_time, attempts) VALUES (?, ?, ?, ?, ?);")) {
                stmt.setInt(1, player.getId());
                stmt.setString(2, mode);
                stmt.setDouble(3, average);
                stmt.setLong(4, fastest);
                stmt.setInt(5, attempts);
                stmt.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
