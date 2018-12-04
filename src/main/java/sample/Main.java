package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main extends Application {


    public static Set<String> stopWords = new HashSet<>();
    public static Indexer indexer;
    public static Map<String, City> cityIndexer = new HashMap<>();
    public static HashMap<String, Doc> allDocs;
    public static HashMap<String, City> CityStorage = new HashMap<>();
    //public static Set<String>Country =new HashSet<>();
    public static Set<String> Capital = new HashSet<>();
    public static Set<String> nonCapital = new HashSet<>();
    public static Conditions con = new Conditions();
    public static int numofAlldocs = 0;


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("My Manoa");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    public static void main(String[] args) throws IOException {
        launch(args);

        //Queue<File> allFiles = new ArrayDeque<>();
       /* File directory = new File("C:\\Users\\Dror\\Desktop\\Posting\\withoutStem\\");
        File[] fileList = directory.listFiles();
        for (File file :fileList)
        {
            List<String> list = Files.readAllLines(Paths.get((file.getPath())));
            Collections.sort(list);
            file.delete();
            FileWriter fos = new FileWriter(file, true);
            PrintWriter out = new PrintWriter(fos, true);
            for (String s : list)
                out.println(s);
            out.close();
        }
*/
        System.out.println("hewre");
    }
}
