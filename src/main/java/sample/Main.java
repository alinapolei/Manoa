package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
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
/*
        File dic = new File("C:\\Users\\Dror\\Desktop\\Posting\\withStem\\Dictionary.txt");
        List <String> list=Files.readAllLines(Paths.get(dic.getPath()));
        HashMap <String,Integer> l=new HashMap<>();
        List <Integer> in=new ArrayList<>();
        for (String string:list ) {
            in.add(Integer.parseInt(string.split("=")[string.split("=").length-1]));
        }
        FileWriter FOS=new FileWriter(new File("C:\\Users\\Dror\\Desktop\\Posting\\withStem\\val.txt"),true);
        PrintWriter out=new PrintWriter(FOS,true);
       Collections.sort(in, new Comparator<Integer>() {
           @Override
           public int compare(Integer o1, Integer o2) {
               if(o1>o2)
                   return -1;
               if(o2>01)
                   return 1;
               return 0;
           }
       });
        for (Integer i:in)
            out.println(i);
        out.close();
        System.out.println("here");

*/
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
        //System.out.println("hewre");
    }
}
