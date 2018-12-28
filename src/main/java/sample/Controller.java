package sample;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.management.Query;
import javax.swing.text.html.ImageView;
import java.io.*;
import java.nio.charset.StandardCharsets;
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

    public TextField query;
    public TextField queryPath;
    public String queryPathString;
    public CheckBox isShowDominantCheckbox;
    public CheckBox isSemanticCheckbox;
    public List<String> selectedCities;
    public HashMap<Queryy, HashMap<String, Double>> rankedDocs;

    @FXML
    public void initialize(){
        /**
         * set the languages list
         */
        /*
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
        */
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
        /**
         * clear the dictionary and delete the posting files
         */
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

                fristRun(allFiles);
                SetLang();
                getAllFiles(corpusPathString+ "\\corpus", allFiles);

                long start = System.nanoTime();
                int counter = 0;
                    while(!allFiles.isEmpty()){
                        File file=allFiles.poll();
                        counter++;
                        if(counter == 150) {
                            System.out.println("[+]Transfer To Disk");
                            parse.transferDisk(postingpath);
                            Main.indexer.transferDocsData(new HashSet<Doc>(Main.allDocs.values()), postingpath);
                            Main.numofAlldocs=Main.numofAlldocs+Main.allDocs.size();
                            Main.allDocs.clear();
                            writeToDisk(Main.citycorp, postingpath);
                            counter = 0;
                        }

                        long start1 = System.nanoTime();
                    Queue<Doc> docs = new ArrayDeque<>();
                    readFile.separateDocuments(file, docs);
                    parse.doParse(docs, isStemCheckbox.isSelected());
                    docs.clear();
                }
                writeToDisk(Main.citycorp, postingpath);
                parse.transferDisk(postingpath);
                Main.indexer.transferDocsData(new HashSet<Doc>(Main.allDocs.values()), postingpath);
                Main.numofAlldocs=Main.numofAlldocs+Main.allDocs.size();
                Main.allDocs.clear();
                double timeSum = (System.nanoTime() - start) * Math.pow(10, -9);
                //System.out.println("sum: " + (System.nanoTime() - start) * Math.pow(10, -9));
               // City maxCity=maxCityTerm();
                sortAllfiles(postingpath);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Finished successfully to retrieve files");
                alert.setContentText("num of documents: " +Main.numofAlldocs+ "\n"
                                        + "num of terms: " + Main.indexer.getDic().size() + "\n"
                                        +  "num of County: "+ Main.Capital.size() +"\n"
                                        +   "num of capital city: "+Main.Capital.size()+"\n"
                                         +"num of non capital: "+(Main.nonCapital.size())+"\n"
                                        +"num of all city: "+(Main.nonCapital.size()+Main.Capital.size())+"\n"
                                        //+"max insance of city : "+maxCity.getDoc()+" "+maxCity.getName()+" "+maxCity.getDocplace().toString() +"\n"
                                         + "runtime: " + timeSum);
                alert.showAndWait();
                alert.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void SetLang() {
        Set <String> allLanguages = Main.lang.keySet();
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

    private void fristRun(Queue<File> allFiles) throws Exception {
        File file;
        ReadFile readFile = new ReadFile();
        while (!allFiles.isEmpty()){
            file=allFiles.poll();
            Queue<Doc> docs = new ArrayDeque<>();
             readFile.setLangANDccITY(file);
        }
        Main.CityStorage=null;
    }

    /**
     * sort the rows that was wrote to the disk
     * @throws IOException
     */
    private void sortAllfiles(String postingpath) throws IOException {
        File directory = new File(postingpath);
        File[] fileList = directory.listFiles();
        HashMap <String,String> mergefile=new HashMap<>();
        List<String>lwrite=new ArrayList<>();
        for (File file :fileList)
        {
            System.out.println(file.getPath());
            List<String> list = Files.readAllLines(Paths.get((file.getPath())));//, StandardCharsets.ISO_8859_1);
            file.delete();
            Collections.sort(list);
            for (String s : list)
            {
                String [] strings=s.split("->");
                if(!mergefile.containsKey(strings[0]))
                    if(!mergefile.containsKey(strings[0].toLowerCase()))
                        if(!mergefile.containsKey(strings[0].toUpperCase()))
                            mergefile.put(strings[0],  (s.substring(strings[0].length())));
                        else
                            mergefile.replace(strings[0].toUpperCase(),mergefile.get(strings[0].toUpperCase()),mergefile.get(strings[0].toUpperCase())+(s.substring(strings[0].length())));
                            else
                             mergefile.replace(strings[0].toLowerCase(),mergefile.get(strings[0].toLowerCase()),mergefile.get(strings[0].toLowerCase())+(s.substring(strings[0].length())));
                        else
                             mergefile.replace(strings[0],mergefile.get(strings[0]),mergefile.get(strings[0])+(s.substring(strings[0].length())));
            }
            list.clear();

            FileWriter fos = new FileWriter(file, true);
            PrintWriter out = new PrintWriter(fos, true);
            for (String s:mergefile.keySet())
                lwrite.add(s +mergefile.get(s).toString());
            Collections.sort(lwrite);
            mergefile.clear();

            for(String n:lwrite)
                out.println(n);
            out.close();

            lwrite.clear();
            mergefile.clear();

        }
        System.gc();
    }
/*
    private City maxCityTerm() {
        City city = new City(" "," "," "," "," ");
        for (City x :Main.cityIndexer.values()){
            if(city.getDocplace().size()<x.getDocplace().size())
                city = x;
        }
            return city;
    }
*/
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

    /**
     * writes the city dictionary to the dick
     * @param cityIndexer
     * @param path
     * @throws IOException
     */
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

        file.delete();
        WrireFunc(file, list);
    }





    public void runQuery(ActionEvent actionEvent) throws Exception {
        if (queryPathString == null && query.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("You must enter a query");
            alert.showAndWait();
            alert.close();
        }
        else {
            if(postingPathString==null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("You must enter a path to the existing posting");
                alert.showAndWait();
                alert.close();
            }
            else {
                String postingpath;
                if (isStemCheckbox.isSelected())
                    postingpath = postingPathString + "\\withStem";
                else
                    postingpath = postingPathString + "\\withoutStem";
                File tmpDir = new File(postingpath);
                if (!tmpDir.exists()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("The posting that you entered doesn't exist");
                    alert.showAndWait();
                    alert.close();
                }
                else {
                    Queue<Queryy> queries = new ArrayDeque<>();
                    if (queryPathString != null) {
                        ReadFile readFile = new ReadFile();
                        File file = new File(queryPathString);
                        readFile.separateQueries(file, queries);
                    } else if (!query.getText().equals("")) {
                        queries.add(new Queryy(query.getText()));
                    }

                    if(Main.indexer==null||Main.indexer.getDic().isEmpty()) {
                        List<String> lines = readFromDisc("Dictionary.txt");
                        createDicfromPosting(lines);
                    }



                    Searcher searcher = new Searcher();
                    HashMap<Queryy, HashSet<String>> finalTokens = searcher.search(queries, isStemCheckbox.isSelected(), isSemanticCheckbox.isSelected());


                    Ranker ranker = new Ranker(postingpath);
                    HashMap<Queryy, HashMap<String, Double>> rankedDocs = ranker.rank(finalTokens,selectedCities);

                    showResults(rankedDocs);
                }
            }
        }
    }

    private void createDicfromPosting(List<String> lines) {
        Main.indexer=new Indexer();
        String [] res;

        for (String s:lines)
        {

            res=s.split("\\|");
            if(res.length==3)
                Main.indexer.addtoDic(res[0],new DicEntry(res[0],Integer.valueOf(res[1]),Integer.valueOf(res[2])));

        }
    }



    private void showResults(HashMap<Queryy, HashMap<String, Double>> rankedDocs) {
        ScrollPane root = new ScrollPane();
        root.setPadding(new Insets(5));
        VBox vBox = new VBox();

        for(Queryy query : rankedDocs.keySet()) {
            Label label = new Label("->" + query.getTitle());
            TableView<String> table = new TableView<>();
            TableColumn<String, String> docCol = new TableColumn<>("מסמך");
            docCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
            docCol.setSortType(TableColumn.SortType.ASCENDING);
            // Display row data
            List list = new ArrayList(rankedDocs.get(query).keySet());
            table.setItems(FXCollections.observableList(list));
            table.getColumns().addAll(docCol);

            vBox.getChildren().add(label);
            vBox.getChildren().add(table);
        }
        root.setContent(vBox);

        Stage stage = new Stage();
        stage.setTitle("Results");
        Scene scene = new Scene(root, 300, 1500);
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(corpusPath.getScene().getWindow());
        stage.show();
    }

    public void queryPathChooser(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File file = fileChooser.showOpenDialog(new Stage());

        if(file != null) {
            queryPath.setText(file.getPath());
            queryPathString = file.getPath();
        }
    }

    public void chooseCity(ActionEvent actionEvent) {
        if(Main.citycorp != null){
            if(selectedCities != null)
                selectedCities.clear();

            TableView<String> table = new TableView<>();
            TableColumn<String, Boolean> chooserCol = new TableColumn<>("chooser");
            TableColumn<String, String> cityCol = new TableColumn<>("city");
            // Defines how to fill data for each cell.
            chooserCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
            Callback<TableColumn<String, Boolean>, TableCell<String, Boolean>> cellFactory
                    = new Callback<TableColumn<String, Boolean>, TableCell<String, Boolean>>() {
                @Override
                public TableCell call(final TableColumn<String, Boolean> param) {
                    final TableCell<String, Boolean> cell = new TableCell<String, Boolean>() {
                        CheckBox checkBox = new CheckBox();

                        @Override
                        public void updateItem(Boolean item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                                setText(null);
                            } else {
                                checkBox.setOnAction(event -> {
                                    if(checkBox.isSelected()) {
                                        if (selectedCities == null) selectedCities = new ArrayList<>();
                                        selectedCities.add(getTableView().getItems().get(getIndex()));
                                    }
                                    else{
                                        selectedCities.remove(getTableView().getItems().get(getIndex()));
                                    }
                                });
                                setGraphic(checkBox);
                                setText(null);
                            }
                        }
                    };
                    return cell;
                }
            };
            chooserCol.setCellFactory(cellFactory);
            cityCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));

            // Set Sort type for userName column
            cityCol.setSortType(TableColumn.SortType.ASCENDING);

            // Display row data
            List list = new ArrayList(Main.citycorp.keySet());
            Collections.sort(list, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            });
            table.setItems(FXCollections.observableList(list));
            table.getColumns().addAll(chooserCol, cityCol);

            Button close = new Button();
            close.setText("ready");

            VBox root = new VBox();
            root.setPadding(new Insets(5));
            root.getChildren().add(table);
            root.getChildren().add(close);
            Stage stage = new Stage();

            close.setOnAction(event -> {
                stage.close();
            });

            stage.setTitle("Cities");
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

    public void saveQueryResults(ActionEvent actionEvent) throws IOException {
       // public HashMap<Queryy, HashMap<String, Double>> rankedDocs
        File file = new File(openFileLocation() + "result.txt");
        if(file.exists())
            file.delete();
        HashMap<String,Double> ranktmp;
        List <String>list=new ArrayList<>();
        String tmp="";
        for (Queryy q:rankedDocs.keySet())
        {
            tmp=tmp+q.getNumber();
            ranktmp=rankedDocs.get(q);
            for (String x:ranktmp.keySet())
            {
                list.add(tmp+" "+"0"+" "+x+" "+ranktmp.get(x)+" "+"1"+" "+"linki");
            }
            tmp="";
        }
        PrintWriter out = null;
        WrireFunc(file, list);
        //save the results
    }

    private void WrireFunc(File file, List <String>list) throws IOException {
        PrintWriter out;
        FileWriter fos = new FileWriter(file, true);
        out = new PrintWriter(fos, true);
        for (String string:list)
            out.println(string);
        list.clear();
        out.close();
    }

    private List<String> readFromDisc(String s) throws IOException {
        String postingpath;
        if(isStemCheckbox.isSelected())
            postingpath = postingPathString + "\\withStem";
        else
            postingpath = postingPathString + "\\withoutStem";
        List<String> res= Collections.emptyList();
        File file=new File(postingpath+"\\"+s);

        if(file.exists())
        {
            res=Files.readAllLines(Paths.get(postingpath+"\\"+s), StandardCharsets.UTF_8);
        }
        else{
            System.out.println("file not exist");
        }
       // System.out.println("here");
    return res;
    }


}
