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

    @Override
    public void start(Stage primaryStage) throws Exception{
        //HashSet<Doc> docs = new HashSet<>();
        //ReadFile rf = new ReadFile("C:\\Users\\Dror\\Desktop\\corpus",docs);

        String x="An an An AN intelligent man with a blindingly fast wit, Gosa was a tireless worker. For \n" +
                "many years he not only handled his own three- to four-hour, six-day-a-week air \n" +
                "shift at KKGO (formerly KBCA-FM), he also was in charge of the station's \n" +
                "commercial production schedule and produced many of the commercials himself. As \n" +
                "if that weren't enough, Gosa began studying law at night in 1982, eventually \n" +
                "passing the California State Bar last June.";


        String[] y=x.split(" ");
        Set<String> pairs=new HashSet<>();

        for (int i=0;i<y.length;i++)
        {
            if (y[i].startsWith("\n"))
                y[i] = y[i].replace("\n", "");
            else if(y[i].startsWith("("))
                y[i] = y[i].replace("(", "");
            else if(y[i].endsWith("),"))
                y[i] = y[i].replace("),", "");

            


        }
        System.out.println("hi");
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
