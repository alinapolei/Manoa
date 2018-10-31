package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        HashSet<String> docs = new HashSet<String>();
        //ReadFile rf = new ReadFile("C:\\Users\\alina\\Documents\\semester 5\\IR\\corpus", docs);

        docs.add(new String(Files.readAllBytes(new File("C:\\Users\\alina\\Desktop\\FB396150\\FB").toPath())));
        Parse parse = new Parse(docs);



        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
