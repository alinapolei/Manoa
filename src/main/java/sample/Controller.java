package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;

public class Controller {
    @FXML
    public TextField corpusPath;
    public TextField postingPath;

    public HashSet<String> dictionary;//the type need to be changed

    public void corpusPathChooser(ActionEvent actionEvent) {
        File file = openFileLocation();
        corpusPath.setText(file.getPath());
    }

    public void postingPathChooser(ActionEvent actionEvent) {
        File file = openFileLocation();
        postingPath.setText(file.getPath());
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
        if(postingPath.getText()!=""){
            dictionary.clear();
            try {
                Files.delete(Paths.get(postingPath.getText()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void showDictionaryButton(ActionEvent actionEvent) {
    }

    public void loadDictionaryButton(ActionEvent actionEvent) {
    }

    public void start(ActionEvent actionEvent) {
        if(corpusPath.getText()=="" || postingPath.getText()==""){
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("You must enter corpus path and posting path");
            alert.showAndWait();
            alert.close();
        }
        else{
            //stat
        }
    }
}
