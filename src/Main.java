import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ressource/game.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1200, 800);
            String cssPath = getClass().getResource("/ressource/style.css").toExternalForm();
            System.out.println("Loading CSS from: " + cssPath);
            scene.getStylesheets().add(cssPath);
            primaryStage.setTitle("Farm Game");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de l'interface : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}