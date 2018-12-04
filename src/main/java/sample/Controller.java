package sample;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Controller {
    @FXML
    public TextField corpusPath;
    public String corpusPathString;
    public TextField postingPath;
    public String postingPathString;
    public CheckBox isStemCheckbox;
    public ComboBox languageChooserComboBox;
    public ObservableList<String> Languages;

    @FXML
    public void initialize(){
        //SortedSet<String> allLanguages = new TreeSet<String>();
        List<String> allLanguages = new ArrayList<>();
        String[] languages = Locale.getISOLanguages();
        for (int i = 0; i < languages.length; i++){
            Locale loc = new Locale(languages[i]);
            allLanguages.add(loc.getDisplayLanguage());
        }
        Languages = FXCollections.observableArrayList(allLanguages);
        Collections.sort(Languages,
                new Comparator<String>()
                {
                    public int compare(String f1, String f2)
                    {
                        return f1.compareTo(f2);
                    }
                });
        languageChooserComboBox.setItems(Languages);
    }


    public void corpusPathChooser(ActionEvent actionEvent) {
        File file = openFileLocation();
        if(file != null) {
            corpusPath.setText(file.getPath());
            corpusPathString = file.getPath();
        }
    }

    public void postingPathChooser(ActionEvent actionEvent) {
        File file = openFileLocation();
        if(file != null) {
            postingPath.setText(file.getPath());
            postingPathString = file.getPath();
        }
    }

    private File openFileLocation() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose directory");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File selectedFile = directoryChooser.showDialog(new Stage());
        return selectedFile;
    }

    public void stemChecked(ActionEvent actionEvent) {
    }

    public void languageChooser(ActionEvent actionEvent) {
    }

    public void resetButton(ActionEvent actionEvent) {
        //clear the dictionary and delete the posting files
        if(!postingPathString.equals("")){
            try {
                File directory = new File(postingPathString);
                FileUtils.cleanDirectory(directory);
                Main.indexer.getDic().clear();
                Main.allDocs.clear();//?
                Main.cityIndexer.clear();//?

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void showDictionaryButton(ActionEvent actionEvent) {
        if(Main.indexer != null && Main.indexer.getDic() != null) {
            TableView<DicEntry> table = new TableView<>();
            TableColumn<DicEntry, String> termCol = new TableColumn<>("Term");
            TableColumn<DicEntry, Integer> tfCorpusCol = new TableColumn<>("TF");
            // Defines how to fill data for each cell.
            termCol.setCellValueFactory(new PropertyValueFactory<>("term"));
            tfCorpusCol.setCellValueFactory(new PropertyValueFactory<>("tfCourpus"));

            // Set Sort type for userName column
            termCol.setSortType(TableColumn.SortType.ASCENDING);
            //tfCorpusCol.setSortType(TableColumn.);

            // Display row data
            List list = new ArrayList(Main.indexer.getDic().values());
            Collections.sort(list, new Comparator<DicEntry>() {
                @Override
                public int compare(DicEntry o1, DicEntry o2) {
                    return o1.getTerm().compareTo(o2.getTerm());
                }
            });
            table.setItems(FXCollections.observableList(list));
            table.getColumns().addAll(termCol, tfCorpusCol);

            StackPane root = new StackPane();
            root.setPadding(new Insets(5));
            root.getChildren().add(table);
            Stage stage = new Stage();
            stage.setTitle("Dictionary");
            Scene scene = new Scene(root, 300, 400);
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(corpusPath.getScene().getWindow());
            stage.show();
        }
        else{
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("You must hit before the start button");
            alert.showAndWait();
        }
    }

    public void loadDictionaryButton(ActionEvent actionEvent) {
        if (Main.indexer != null && Main.indexer.getDic() != null) {
            try {
                String postingpath;
                if(isStemCheckbox.isSelected())
                    postingpath = postingPathString + "\\withStem";
                else
                    postingpath = postingPathString + "\\withoutStem";
                File directory = new File(postingpath);
                if (! directory.exists())
                    directory.mkdir();

                PrintWriter out = null;
                File file = new File(directory.getPath() + "\\Dictionary.txt");
                List<DicEntry> list = new ArrayList(Main.indexer.getDic().values());
                Collections.sort(list, new Comparator<DicEntry>() {
                    @Override
                    public int compare(DicEntry o1, DicEntry o2) {
                        return o1.getTerm().compareTo(o2.getTerm());
                    }
                });

                FileWriter fos = new FileWriter(file, true);
                out = new PrintWriter(fos, true);
                for (DicEntry dicEntry : list)
                    out.println(dicEntry.toString());
                if (out != null) {
                    out.close();

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setHeaderText("The dictionary saved successfully");
                    alert.showAndWait();
                    alert.close();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void showCityDictionaryButton(ActionEvent actionEvent) {
        if(Main.CityStorage != null && Main.CityStorage.values().size() != 0) {
            TableView<City> table = new TableView<>();
            TableColumn<City, String> cityCol = new TableColumn<>("City");
            TableColumn<City, String> currencyCol = new TableColumn<>("Currency");
            TableColumn<City, String> populationCol = new TableColumn<>("Population");
            // Defines how to fill data for each cell.
            cityCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            currencyCol.setCellValueFactory(new PropertyValueFactory<>("Currency"));
            populationCol.setCellValueFactory(new PropertyValueFactory<>("pop"));

            // Set Sort type for userName column
            cityCol.setSortType(TableColumn.SortType.ASCENDING);
            //tfCorpusCol.setSortType(TableColumn.);

            // Display row data
            Map<String, Long> result = Main.CityStorage.values().stream().collect(
                            Collectors.groupingBy(City::getName, Collectors.counting()));
            List list = new ArrayList(Main.CityStorage.values());
            Collections.sort(list, new Comparator<City>() {
                @Override
                public int compare(City o1, City o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            table.setItems(FXCollections.observableList(list));
            table.getColumns().addAll(cityCol, currencyCol, populationCol);

            StackPane root = new StackPane();
            root.setPadding(new Insets(5));
            root.getChildren().add(table);
            Stage stage = new Stage();
            stage.setTitle("City Dictionary");
            Scene scene = new Scene(root, 300, 400);
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(corpusPath.getScene().getWindow());
            stage.show();
        }
        else{
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("You must hit before the start button");
            alert.showAndWait();
        }
    }

    public void start(ActionEvent actionEvent) {
        if (corpusPathString.equals("") || postingPathString.equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("You must enter corpus path and posting path");
            alert.showAndWait();
            alert.close();
        } else {
            //path for files => corpusPath
            //path for stop words => corpusPath\\stop_words.txt
            //path for posting postingPath\\withStem / postingPath\\withoutStem (according to isStem)
            try {
                //Main.startRetriving(isStemCheckbox.isSelected());

                String postingpath;
                if(isStemCheckbox.isSelected())
                    postingpath = postingPathString + "\\withStem";
                else
                    postingpath = postingPathString + "\\withoutStem";
                File directory = new File(postingpath);
                if (! directory.exists())
                    directory.mkdir();

                HttpResponse<JsonNode> response = Unirest.get("https://restcountries.eu/rest/v2/all")
                        .header("X-Mashape-Key", "<required>")
                        .header("Accept", "application/json")
                        .asJson();
                Object map = (response.getBody().getArray());
               for (int i=0;i<((JSONArray) map).length();i++)
               {
                   String countryName=((JSONArray) map).getJSONObject(i).get("name").toString().toUpperCase();
                   String CapitalName=((JSONArray) map).getJSONObject(i).get("capital").toString().toUpperCase();
                   String Currency =((JSONArray) map).getJSONObject(i).get("currencies").toString().toUpperCase();
                   String Pop=((JSONArray) map).getJSONObject(i).get("population").toString().toUpperCase();
                   Main.CityStorage.put(CapitalName,new City(CapitalName,Currency,Pop,countryName,""));
               }
                response=null;
                map=null;

                Main.allDocs = new HashMap<>();
                Queue<File> allFiles = new ArrayDeque<>();
                getAllFiles(corpusPathString+ "\\corpus", allFiles);
                ReadFile readFile = new ReadFile();
                readFile.setStopWords(Main.stopWords, corpusPathString + "\\stop_words.txt");
                Main.indexer = new Indexer();
                Parse parse = new Parse();
                //HashSet<Doc> docs = new HashSet<>();
                long start = System.nanoTime();
                int counter = 0;
                //for (File file : allFiles) {
                    while(!allFiles.isEmpty()){
                        File file=allFiles.poll();
                        counter++;
                        if(counter == 150) {
                            System.out.println("[+]Transfer To Disk");
                            parse.transferDisk(postingpath);
                            Main.indexer.transferDocsData(new HashSet<Doc>(Main.allDocs.values()), postingpath);
                            Main.numofAlldocs=Main.numofAlldocs+Main.allDocs.size();
                            Main.allDocs.clear();
                            writeToDisk(Main.cityIndexer, postingpath);
                            counter = 0;
                        }

                        long start1 = System.nanoTime();
                    Queue<Doc> docs = new ArrayDeque<>();
                    readFile.separateDocuments(file, docs);
                    parse.doParse(docs, isStemCheckbox.isSelected());
                    System.out.println("[+] doneParseDoc " + file.getName());
                  //  for(Doc doc: docs)
                     //   Main.allDocs.put(doc.getDocNumber(),doc);

                    docs.clear();
                    System.out.println("sum: " + (System.nanoTime() - start1) * Math.pow(10, -9));


                }
                writeToDisk(Main.cityIndexer, postingpath);
                parse.transferDisk(postingpath);
                Main.indexer.transferDocsData(new HashSet<Doc>(Main.allDocs.values()), postingpath);
                Main.numofAlldocs=Main.numofAlldocs+Main.allDocs.size();
                Main.allDocs.clear();
                double timeSum = (System.nanoTime() - start) * Math.pow(10, -9);
                System.out.println("sum: " + (System.nanoTime() - start) * Math.pow(10, -9));
                City maxCity=maxCityTerm();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Finished successfully to retrieve files");
                alert.setContentText("num of documents: " +Main.numofAlldocs+ "\n"
                                        + "num of terms: " + Main.indexer.getDic().size() + "\n"
                                        +  "num of County: "+ Main.Capital.size() +"\n"
                                        +   "num of capital city: "+Main.Capital.size()+"\n"
                                         +"num of non capital: "+(Main.nonCapital.size())+"\n"
                                        +"num of all city: "+(Main.nonCapital.size()+Main.Capital.size())+"\n"
                                        +"max insance of city : "+maxCity.getDoc()+" "+maxCity.getName()+" "+maxCity.getDocplace().toString() +"\n"
                                         + "runtime: " + timeSum);
                alert.showAndWait();
                alert.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private City maxCityTerm() {
        City city = new City(" "," "," "," "," ");
        for (City x :Main.cityIndexer.values()){
            if(city.getDocplace().size()<x.getDocplace().size())
                city = x;
        }
            return city;
    }

    private void getAllFiles(String path, Queue<File> allFiles) {
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

    private void writeToDisk(Map<String, City> cityIndexer, String path) throws IOException {
        File file = new File(path+"\\cities.txt");
        List<String> list;
        if (file.exists())
             list = Files.readAllLines(Paths.get((file.getPath())));
        else
            list=new ArrayList<>();
        for (String s:cityIndexer.keySet())
            list.add(cityIndexer.get(s).toString());
        PrintWriter out = null;
        //for (String term : cityIndexer.keySet()) {
          //  City city = cityIndexer.get(term);
            file.delete();
            FileWriter fos = new FileWriter(file, true);
            out = new PrintWriter(fos, true);
            for (String string:list)
                out.println(string);
            list.clear();
            //out.println(city.toString());
        //}
        //if(out!=null)
            out.close();
    }
}
