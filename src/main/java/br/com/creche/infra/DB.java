package br.com.creche.infra;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DB {
    private static String url;
    private static String user;
    private static String password;
    private static String schema;

    static {
        try (InputStream in = DB.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties props = new Properties();
            props.load(in);
            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");
            schema = props.getProperty("db.schema", "public");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar application.properties", e);
        }
    }

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            // Define o schema padrão se necessário
            if (schema != null && !schema.isBlank()) {
                try (var st = conn.createStatement()) {
                    st.execute("set search_path to " + schema);
                }
            }
            return conn;
        } catch (Exception e) {
            throw new RuntimeException("Falha ao conectar no banco", e);
        }
    }
}