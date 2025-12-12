package com.bank.network;

import java.io.*;
import java.net.Socket;

public class NetworkClient {

    private static NetworkClient instance;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    // Singleton : Une seule connexion pour toute l'app
    private NetworkClient() {
        try {
            this.socket = new Socket("localhost", 12345);
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Consommer le message de bienvenue du serveur
            System.out.println("Serveur: " + in.readLine());

        } catch (IOException e) {
            System.err.println("Impossible de se connecter au serveur : " + e.getMessage());
        }
    }

    public static NetworkClient getInstance() {
        if (instance == null) {
            instance = new NetworkClient();
        }
        return instance;
    }

    /**
     * Envoie une requête au serveur et attend la réponse immédiate.
     */
    public String sendRequest(String request) {
        if (out == null) return "ERREUR: Pas de connexion";
        try {
            out.println(request); // Envoi
            return in.readLine(); // Attente réponse
        } catch (IOException e) {
            return "ERREUR: " + e.getMessage();
        }
    }
}