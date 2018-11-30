package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
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

    /*
    public static void startRetriving(boolean isStem) throws Exception{
        allDocs = new HashSet<>();
        List<File> allFiles = new ArrayList<File>();
        getAllFiles("C:\\Users\\alina\\Desktop\\FB396150", allFiles);
        ReadFile readFile = new ReadFile();
        readFile.setStopWords(stopWords);
        indexer=new Indexer();
        Parse parse = new Parse();
        //HashSet<Doc> docs = new HashSet<>();
        long start = System.nanoTime();
        counter = 0;
        for (File file : allFiles) {
            counter++;
            if(counter == 200) {
                parse.transferDisk();
                counter = 0;
            }
            long start1 = System.nanoTime();
            HashSet<Doc> docs = new HashSet<>();
            readFile.separateDocuments(file, docs);
            parse.doParse(docs, isStem);
            System.out.println("[+] doneParse" + file.getName());
            allDocs.addAll(docs);
            docs.clear();
            System.out.println("sum: "+(System.nanoTime()-start1)*Math.pow(10, -9));

        }
        parse.transferDisk();
        indexer.transferDocsData(allDocs);

        System.out.println("sum: "+(System.nanoTime()-start)*Math.pow(10, -9));
    }
    public static void getAllFiles(String path, List<File> allFiles) {
        File directory = new File(path);
        File[] fileList = directory.listFiles();
        if (fileList != null) {
            for (File file : fileList) {
                if (file.isFile())
                    allFiles.add(file);
                else if (file.isDirectory())
                    getAllFiles(file.getAbsolutePath(), allFiles);
            }
        }
    }
*/
}
