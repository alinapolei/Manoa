package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.*;




public class Main extends Application {


    public Set<String> stopWords = new HashSet<>();


    @Override
    public void start(Stage primaryStage) throws Exception{
        List<File> allFiles = new ArrayList<File>();
        getAllFiles("C:\\Users\\Dror\\Desktop\\corpuss", allFiles);
        ReadFile readFile = new ReadFile();
        readFile.setStopWords(stopWords);
        Parse parse = new Parse();
        HashSet<Doc> docs = new HashSet<>();
        int numFile=1;
        for (File file : allFiles) {
            System.out.println("Number of File :  "+numFile);
            readFile.separateDocuments(file, docs);
            parse.doParse(docs);
            numFile++;
        }
        parse.removeStopWords(stopWords);

        /*HashSet<Doc> docs = new HashSet<>();
        ReadFile rf = new ReadFile("C:\\Users\\alina\\Documents\\semester 5\\IR\\corpus\\corpus",docs, stopWords);
        //ReadFile rf = new ReadFile("C:\\Users\\alina\\Desktop\\FB396150", docs, stopWords);
        Parse parse = new Parse(docs, stopWords);*/

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
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
