package com.bank.network;

import com.bank.data.AccountDAO;
import com.bank.model.Account;
import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;
    private AccountDAO accountDAO;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.accountDAO = new AccountDAO();
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            System.out.println("Nouveau client connecté : " + socket.getInetAddress());
            out.println("BIENVENUE à la JavaBank ! (Commandes: BALANCE <id>, TRANSFER <src> <dest> <montant>)");

            String request;
            while ((request = in.readLine()) != null) {
                System.out.println("Reçu du client : " + request);

                String[] parts = request.split(" ");
                String command = parts[0].toUpperCase();

                switch (command) {
                    case "BALANCE":
                        if (parts.length < 2) {
                            out.println("ERREUR: Syntaxe invalide. Utilisez: BALANCE <account_id>");
                        } else {
                            Account acc = accountDAO.findAccount(parts[1]);
                            if (acc != null) {
                                out.println("SUCCES: Solde du compte " + acc.getAccountNumber() + " = " + acc.getBalance());
                            } else {
                                out.println("ERREUR: Compte introuvable.");
                            }
                        }
                        break;

                    case "TRANSFER":
                        // TODO: Implémenter la logique de virement ici avec la méthode synchronized
                        out.println("INFO: Fonctionnalité en cours de développement...");
                        break;

                    case "EXIT":
                        out.println("AU REVOIR");
                        return;

                    default:
                        out.println("ERREUR: Commande inconnue.");
                }
            }

        } catch (IOException e) {
            System.err.println("Erreur de communication avec le client : " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException e) { /* Ignorer */ }
        }
    }
}