package minichat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatApplication extends Application {
  @Override
  public void start(@SuppressWarnings("exports") Stage stage) throws IOException {
      FXMLLoader fxmlLoader = new FXMLLoader(ChatApplication.class.getResource("chat-view.fxml"));
      Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
      stage.setTitle("Mini Chat");
      stage.setScene(scene);
      stage.show();
  }

  public static void main(String[] args) {
      launch();
  }
}