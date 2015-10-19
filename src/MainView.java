package musicplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.File;
import javafx.scene.image.Image;

public class MainView extends Application {

    private final String fxmlPath = "res/fxml/";
    private final String imgPath = "musicplayer/res/img/";

    public static void main(String[] args) {

        Application.launch(MainView.class);
    }

    @Override
    public void start(Stage stage) {

        try {

            BorderPane view = (BorderPane) FXMLLoader.load(MainView.class.getResource(fxmlPath + "main.fxml"));
            Scene scene = new Scene(view);
            stage.setScene(scene);
            stage.getIcons().add(new Image(imgPath + "songsIcon.png"));
            stage.setTitle("Music Player");
            stage.show();

        } catch (Exception ex) {

            System.out.println(ex.getMessage());
        }
    }

}