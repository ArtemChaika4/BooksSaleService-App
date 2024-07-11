package ua.edu.dnu.booksales.settings;


import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import ua.edu.dnu.booksales.util.BackupUtil;

import java.io.File;
import java.util.Arrays;

public class SettingsController {
    public void onCreateBackup() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Обрати директорію");
        File directory = directoryChooser.showDialog(null);
        if(directory != null){
            createBackup(directory);
        }
    }

    private void createBackup(File directory){
        try {
            String path = directory.getAbsolutePath() + "/" +
                    getBackupName();
            BackupUtil.saveToBackup(path);
            showAlert("Резервну копію успішно створено: " + path,
                    Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Виникла помилка при створенні резервної копії",
                    Alert.AlertType.ERROR);
        }
    }

    private String getBackupName(){
        long time = System.currentTimeMillis();
        String name = "backup";
        String extension = ".bak";
        return name + "_" + time + extension;
    }

    private void showAlert(String message, Alert.AlertType type){
        Alert alert = new Alert(type, message);
        alert.showAndWait();
    }

    public void onRestore() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Обрати резервну копію");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Backup Files", "*.bak"));
        File file = fileChooser.showOpenDialog(null);
        if(file != null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Попередження про виконання операції відновлення");
            alert.setContentText("Після виконання операції стан бази даних буде відновлено з " +
                    "резервної копії: " + file.getName());
            alert.showAndWait().ifPresent(b -> restore(file));
        }
    }

    private void restore(File file){
        try {
            String path = file.getAbsolutePath();
            BackupUtil.restoreFromBackup(path);
            showAlert("Відновлення з резервної копії успішно виконано",
                    Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Виникла помилка при відновленні з резервної копії",
                    Alert.AlertType.ERROR);
        }
    }
}
