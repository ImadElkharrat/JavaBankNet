package com.bank;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class MainTest {
    public static void main(String[] args) {
        System.out.println("--- TEST INTEGRATION CONSOLE ---");

        // Assure-toi que BankServer est lancé DANS UN AUTRE TERMINAL ou via IntelliJ avant de lancer ceci !

        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // 1. Tenter une action sans être connecté
            System.out.println("1. Test Sécurité (Doit échouer) :");
            out.println("BALANCE FR76-ALICE");
            System.out.println("Serveur > " + in.readLine()); // Attente réponse

            // 2. Se connecter
            System.out.println("\n2. Test Login (Doit réussir) :");
            out.println("LOGIN alice pass123");
            System.out.println("Serveur > " + in.readLine());

            // 3. Réessayer l'action
            System.out.println("\n3. Test Accès Données (Doit réussir) :");
            out.println("BALANCE FR76-ALICE");
            System.out.println("Serveur > " + in.readLine());

            // 4. Quitter
            out.println("EXIT");

        } catch (IOException e) {
            System.err.println("ERREUR : Le serveur est-il allumé ? " + e.getMessage());
        }
    }
}