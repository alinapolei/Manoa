package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.lucene.util.AttributeFactory;

import java.io.IOException;
import java.util.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("My Manoa");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    public static Set<String> stopWords = new HashSet<>();
    public static Indexer indexer;
    public static Map<String,City> cityIndexer=new HashMap<>();
    public static HashMap<String, Doc> allDocs;
    public static HashMap<String,City>CityStorage=new HashMap<>();
    public static Set<String>Country =new HashSet<>();
    public static Set<String>Capital =new HashSet<>();
    public static Set<String>nonCapital=new HashSet<>();

}
