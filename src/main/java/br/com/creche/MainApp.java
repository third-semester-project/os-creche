package br.com.creche;

import br.com.creche.infra.DBHealth;
import br.com.creche.ui.ThemeManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.prefs.Preferences;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(loader.load());

        Preferences prefs = Preferences.userRoot().node("br.com.creche");
        String tema = prefs.get("tema", "SISTEMA");
        ThemeManager.apply(scene, ThemeManager.Theme.valueOf(tema));

//        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        stage.setTitle("Sistema de O.S. - Creche");
//        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/app.png"))); // opcional
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }



    public static void main(String[] args) {
        launch();
    }
}