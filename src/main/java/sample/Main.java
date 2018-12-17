package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.lucene.codecs.lucene50.Lucene50TermVectorsFormat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main extends Application {


    /**
     * set of the stop words
     */
    public static Set<String> stopWords = new HashSet<>();
    /**
     * instance of the indexer class
     */
    public static Indexer indexer;
    /**
     * the cities
     * key - the doc name
     * value - a city object
     */
    public static Map<String, City> cityIndexer = new HashMap<>();
    /**
     * hashmap that consists of all the docs in a specific chunk
     */
    public static HashMap<String, Doc> allDocs;
    /**
     * all the cities that was read from the api
     */
    public static HashMap<String, City> CityStorage = new HashMap<>();
    /**
     * a set of capital cities that was found in the corpus
     */
    public static Set<String> Capital = new HashSet<>();
    /**
     * a ser of non capital cities that was found in the corpus
     */
    public static Set<String> nonCapital = new HashSet<>();
    /**
     * instance of conditions class
     */
    public static Conditions con = new Conditions();
    /**
     * a counter that represents that number of files that was read till now
     */
    public static int numofAlldocs = 0;

    public static HashMap<String ,City>citycorp=new HashMap<>();
    public static HashMap <String,ArrayList<String>> lang=new HashMap<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("My Manoa");
        primaryStage.setScene(new Scene(root, 600, 700));
        primaryStage.show();
    }

    public static void main(String[] args) throws IOException {
        launch(args);
    }
}
