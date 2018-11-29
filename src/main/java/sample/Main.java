package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Main extends Application {


    public static Set<String> stopWords = new HashSet<>();
    public static Indexer indexer;
    public static Map <String,City> cityIndexer=new HashMap<>();
    public static HashSet<Doc> allDocs;
    static int counter = 0;

    @Override
    public void start(Stage primaryStage) throws Exception{
        allDocs = new HashSet<>();
        List<File> allFiles = new ArrayList<File>();
        getAllFiles("C:\\Users\\Dror\\Desktop\\corpus", allFiles);
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
                indexer.transferDocsData(allDocs);
                counter = 0;
            }
            long start1 = System.nanoTime();
            HashSet<Doc> docs = new HashSet<>();
            readFile.separateDocuments(file, docs);
            parse.doParse(docs);
            System.out.println("[+] doneParse" + file.getName());
            allDocs.addAll(docs);
            docs.clear();
            System.out.println("sum: "+(System.nanoTime()-start1)*Math.pow(10, -9));

        }
        parse.transferDisk();
        indexer.transferDocsData(allDocs);

        System.out.println("sum: "+(System.nanoTime()-start)*Math.pow(10, -9));
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("My Manoa");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    public void getAllFiles(String path, List<File> allFiles) {
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

    public static void main(String[] args) throws IOException {


        launch(args);
    }
}
