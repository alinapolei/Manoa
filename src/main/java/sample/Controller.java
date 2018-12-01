package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import javax.tools.JavaFileManager;
import java.io.*;
import java.util.*;

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

                Main.allDocs = new HashMap<>();
                List<File> allFiles = new ArrayList<File>();
                getAllFiles(corpusPathString+ "\\corpus", allFiles);
                ReadFile readFile = new ReadFile();
                readFile.setStopWords(Main.stopWords, corpusPathString + "\\stop_words.txt");
                Main.indexer = new Indexer();
                Parse parse = new Parse();
                //HashSet<Doc> docs = new HashSet<>();
                long start = System.nanoTime();
                int counter = 0;
                for (File file : allFiles) {
                    counter++;
                    if(counter == 200) {
                        System.out.println("[+]Transfer To Disk");
                        parse.transferDisk(postingpath);
                        writeToDisk(Main.cityIndexer, postingpath);
                        counter = 0;
                    }

                    long start1 = System.nanoTime();
                    HashSet<Doc> docs = new HashSet<>();
                    readFile.separateDocuments(file, docs);
                    parse.doParse(docs, isStemCheckbox.isSelected());
                    System.out.println("[+] doneParseDoc " + file.getName());

                    for(Doc doc: docs)
                        Main.allDocs.put(doc.getDocNumber(),doc);
                    docs.clear();
                    System.out.println("sum: " + (System.nanoTime() - start1) * Math.pow(10, -9));
                }
                writeToDisk(Main.cityIndexer, postingpath);
                parse.transferDisk(postingpath);
                Main.indexer.transferDocsData(new HashSet<Doc>(Main.allDocs.values()), postingpath);
                double timeSum = (System.nanoTime() - start) * Math.pow(10, -9);
                System.out.println("sum: " + (System.nanoTime() - start) * Math.pow(10, -9));

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Finished successfully to retrieve files");
                alert.setContentText("num of documents: " + Main.allDocs.size() + "\n"
                                        + "num of terms: " + Main.indexer.getDic().size() + "\n"
                                        + "runtime: " + timeSum);
                alert.showAndWait();
                alert.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void getAllFiles(String path, List<File> allFiles) {
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
        PrintWriter out = null;
        for (String term : cityIndexer.keySet()) {
            City city = cityIndexer.get(term);
            File file = new File(path+"\\cities.txt");
            FileWriter fos = new FileWriter(file, true);
            out = new PrintWriter(fos, true);
            out.println(city.toString());
        }
        if(out!=null)
            out.close();
    }
}
