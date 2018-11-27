package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

public class Main extends Application {


    public Set<String> stopWords = new HashSet<>();
    public static Indexer indexer;

    @Override
    public void start(Stage primaryStage) throws Exception{

        List<File> allFiles = new ArrayList<File>();
        getAllFiles("C:\\Users\\Dror\\Desktop\\corpus", allFiles);
        ReadFile readFile = new ReadFile();
        readFile.setStopWords(stopWords);
        indexer=new Indexer();
        //Parse parse = new Parse(index);
        //HashSet<Doc> docs = new HashSet<>();
        long start = System.nanoTime();
        Parse parse = new Parse();
        for (File file : allFiles) {
              // docs.clear();
                System.out.println(file.getName());
                System.out.println("[+] start");
            //readFile.separateDocuments(file, docs);
            new Thread(){
                   public void run(){
                       HashSet<Doc> docs = new HashSet<>();
                       Parse parse = new Parse();
                       readFile.separateDocuments(file, docs);
                       parse.doParse(docs);
                       System.out.println("[+] doneParse" );
                       docs.clear();
                   }
               }.start();


            // parse.doParse(docs);
            //System.out.println("parse file: " +(System.nanoTime()-start1)*Math.pow(10, -9));

        }
        //parse.setAllTerms();
       // parse.removeStopWords(stopWords);
       // parse.transferDisk();
        System.out.println("sum: "+(System.nanoTime()-start)*Math.pow(10, -9));

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("My Manoa");
        primaryStage.setScene(new Scene(root, 300, 275));
//        System.out.println("here");
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
