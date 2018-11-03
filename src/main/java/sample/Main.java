package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;


import java.io.IOException;
import java.util.*;


import java.io.File;


public class Main extends Application {


    public Set<String> stopWords = new HashSet<>();


    @Override
    public void start(Stage primaryStage) throws Exception{

        HashSet<Doc> docs = new HashSet<>();
        ReadFile rf = new ReadFile("C:\\Users\\Dror\\Desktop\\corpus",docs,stopWords);
        Parse parse=new Parse(docs,stopWords);
        //HashSet<String> docs = new HashSet<>();
        //File file = new File("C:\\Users\\Dror\\Desktop\\New.txt");
        //Document doc = Jsoup.parse(file, "utf-8");
        //Elements x=doc.getElementsByTag("date1");
        //Doc t=new Doc();
        //t.setPublishDate(doc.getElementsByTag("date1").text());
        //t.setDocNumber(doc.getElementsByTag("DOCNO").text());
        //int k=doc.getElementsByTag("text").text().indexOf("[Text]")+6;
        //t.setBodyText(doc.getElementsByTag("text").text().substring(k));


        //docs.add(String.join("\n", Files.readAllLines(file.toPath())));
        //Parse parse = new Parse(docs);

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }



    public static void main(String[] args) throws IOException {


        launch(args);
    }
}
