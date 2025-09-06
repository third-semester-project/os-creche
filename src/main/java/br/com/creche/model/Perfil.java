package br.com.creche.model;

import java.util.Locale;

public enum Perfil {
    ADMIN,
    GESTOR,
    OPERADOR;

    public static Perfil from(String value) {
        if (value == null) return null;
        try {
            return Perfil.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return null; // ou lance exceção se preferir ser estrito
        }
    }
}