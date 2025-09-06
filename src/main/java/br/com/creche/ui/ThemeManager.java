package br.com.creche.ui;

import javafx.scene.Scene;

import java.util.Objects;

public class ThemeManager {
    public enum Theme {
        SISTEMA, CLARO, ESCURO
    }

    private static final String LIGHT = "/css/app.css";
    private static final String DARK  = "/css/dark.css";

    public static void apply(Scene scene, Theme theme) {
        if (scene == null) return;
        // Remove ambos para garantir limpeza
        scene.getStylesheets().removeIf(s -> s.endsWith("app.css") || s.endsWith("dark.css"));
        switch (Objects.requireNonNullElse(theme, Theme.SISTEMA)) {
            case CLARO -> scene.getStylesheets().add(ThemeManager.class.getResource(LIGHT).toExternalForm());
            case ESCURO -> scene.getStylesheets().add(ThemeManager.class.getResource(DARK).toExternalForm());
            case SISTEMA -> {
                // heurística: usa claro por padrão; se quiser detectar OS, injete uma flag ao iniciar
                scene.getStylesheets().add(ThemeManager.class.getResource(LIGHT).toExternalForm());
            }
        }
    }
}