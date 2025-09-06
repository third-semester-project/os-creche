package br.com.creche.ui;

import javafx.scene.Parent;
import javafx.scene.Scene;

import java.util.prefs.Preferences;

public class SceneFactory {
    private static final String PREFS_NODE = "br.com.creche";
    private static final String PREF_TEMA = "tema"; // SISTEMA | CLARO | ESCURO

    public static Scene createScene(Parent root) {
        Scene scene = new Scene(root);

        Preferences prefs = Preferences.userRoot().node(PREFS_NODE);
        String tema = prefs.get(PREF_TEMA, "SISTEMA");

        ThemeManager.Theme temaEnum = ThemeManager.Theme.valueOf(tema);
        ThemeManager.apply(scene, temaEnum);

        return scene;
    }
}
