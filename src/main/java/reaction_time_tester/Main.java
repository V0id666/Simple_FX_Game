package reaction_time_tester;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/reaction_time_tester/Login.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.setTitle("Reaction Time Tester");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
