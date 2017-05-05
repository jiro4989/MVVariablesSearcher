package app;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.*;

public class Main extends Application {
  public static final String TITLE = "MV Variables Searcher";
  public static final String VERSION = "ver 1.0.0";
  public static final String CSS = "/app/res/css/basic.css";
  public static final String APP_ICON = "/app/res/img/app_icon.png";
  public static final String TITLE_VERSION = TITLE + " " + VERSION;

  @Override
  public void start(Stage primaryStage) {//{{{
    FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));

    try {
      BorderPane root   = (BorderPane) loader.load();
      Scene scene = new Scene(root, 512, 600);

      primaryStage.setScene(scene);
      primaryStage.getIcons().add(new Image(APP_ICON));
      primaryStage.setTitle(TITLE_VERSION);

      primaryStage.show();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }//}}}

  public static void main(String... args) { launch(args); }

}
