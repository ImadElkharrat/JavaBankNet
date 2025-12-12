package com.bank.data;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseSource {

    // L'instance unique (Singleton)
    private static DatabaseSource instance;
    private Connection connection;

    // Variables de config
    private String url;
    private String username;
    private String password;

    // Constructeur PRIVÉ : Personne ne peut faire "new DatabaseSource()"
    private DatabaseSource() {
        try {
            // 1. Charger les propriétés
            Properties props = new Properties();
            InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties");

            if (input == null) {
                System.out.println("Fichier db.properties introuvable !");
                return;
            }
            props.load(input);

            this.url = props.getProperty("db.url");
            this.username = props.getProperty("db.user");
            this.password = props.getProperty("db.password");

            // 2. Charger le Driver MySQL (Optionnel sur les versions récentes de Java, mais recommandé)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 3. Créer la connexion
            this.connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connexion à la Base de Données réussie !");

        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur critique : Impossible de se connecter à la BDD.");
        }
    }

    // Point d'accès global
    public static DatabaseSource getDataSource() {
        if (instance == null) {
            instance = new DatabaseSource();
        } else {
            try {
                if (instance.connection.isClosed()) {
                    instance = new DatabaseSource();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}