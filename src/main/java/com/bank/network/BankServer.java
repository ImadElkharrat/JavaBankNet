package com.bank.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BankServer {

    private static final int PORT = 12345;
    // Pool de threads : permet de gérer jusqu'à 10 clients en parallèle
    private static final ExecutorService pool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        System.out.println("Démarrage du Serveur Bancaire sur le port " + PORT + "...");

        // Initialisation de la BDD (Test de connexion au démarrage)
        try {
            Class.forName("com.bank.data.DatabaseSource");
        } catch (ClassNotFoundException e) {
            System.err.println("Attention : Driver BDD non chargé.");
        }

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serveur prêt et en attente de connexions...");

            while (true) {
                // 1. Bloque ici jusqu'à ce qu'un client se connecte
                Socket clientSocket = serverSocket.accept();

                // 2. Créer la tâche pour ce client
                ClientHandler handler = new ClientHandler(clientSocket);

                // 3. Déléguer l'exécution au Pool de Threads (Exécution parallèle)
                pool.execute(handler);
            }

        } catch (IOException e) {
            System.err.println("Erreur serveur : " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }
}