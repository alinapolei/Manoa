package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.util.HashSet;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        HashSet<Element> docs = new HashSet<>();
        ReadFile rf = new ReadFile("C:\\Users\\Dror\\Desktop\\corpus",docs);

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) throws IOException {


        launch(args);
    }
}
