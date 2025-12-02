package br.com.creche.ui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.prefs.Preferences;

public class SceneFactory {
    private static final String PREFS_NODE = "br.com.creche";
    private static final String PREF_TEMA = "tema"; // SISTEMA | CLARO | ESCURO
    private static final String ICON_PATH = "/images/logo.png";

    public static Scene createScene(Parent root) {
        Scene scene = new Scene(root);

        Preferences prefs = Preferences.userRoot().node(PREFS_NODE);
        String tema = prefs.get(PREF_TEMA, "SISTEMA");

        ThemeManager.Theme temaEnum = ThemeManager.Theme.valueOf(tema);
        ThemeManager.apply(scene, temaEnum);

        return scene;
    }

    public static void applyAppIcon(Stage stage) {
        if (stage == null) return;
        try {
            var iconStream = SceneFactory.class.getResourceAsStream(ICON_PATH);
            if (iconStream != null) {
                Image icon = new Image(iconStream);
                stage.getIcons().clear();
                stage.getIcons().add(icon);
            }
        } catch (Exception ignored) {
            // Mantém o ícone padrão se falhar ao carregar
        }
    }
}
