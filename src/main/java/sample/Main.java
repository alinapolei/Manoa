package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.jsoup.nodes.Element;


import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.util.HashSet;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //HashSet<Element> docs = new HashSet<>();
        //ReadFile rf = new ReadFile("C:\\Users\\Dror\\Desktop\\corpus",docs);

        HashSet<String> docs = new HashSet<>();
        File file = new File("C:\\Users\\alina\\Desktop\\FB396150\\New");
        docs.add(String.join("\n", Files.readAllLines(file.toPath())));
        Parse parse = new Parse(docs);


        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) throws IOException {


        launch(args);
    }
}
