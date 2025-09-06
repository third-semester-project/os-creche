package br.com.creche.infra;

import java.sql.ResultSet;

public class DBHealth {

    public static boolean testConnection() {
        try (var conn = DB.getConnection();
             var st = conn.createStatement();
             ResultSet rs = st.executeQuery("select 1")) {
            if (rs.next() && rs.getInt(1) == 1) {
                System.out.println("[DB] Conexão OK (select 1).");
                conn.close();
                return true;
            }
        } catch (Exception e) {
            System.err.println("[DB] Falha de conexão: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        System.err.println("[DB] Conexão respondeu, mas resultado inesperado.");
        return false;
    }

    public static String pingInfo() {
        try (var conn = DB.getConnection();
             var st = conn.createStatement();
             ResultSet rs = st.executeQuery("select version()")) {
            if (rs.next()) {
                String version = rs.getString(1);
                conn.close();
                return "Conectado ao PostgreSQL: " + version;
            }
        } catch (Exception e) {
            e.printStackTrace(); // para ver o motivo real (auth failed, timeout, SSL, etc.)
            throw new RuntimeException("Falha ao conectar no banco", e);
        }
        return "Sem resposta do banco.";
    }
}