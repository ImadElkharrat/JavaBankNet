package com.bank.network;

import com.bank.data.AccountDAO;
import com.bank.data.UserDAO;
import com.bank.model.Account;
import com.bank.model.User;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {

    private Socket socket;
    private AccountDAO accountDAO;
    private UserDAO userDAO;
    private User currentUser;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.accountDAO = new AccountDAO(); // Connexion DB pour les comptes
        this.userDAO = new UserDAO();       // Connexion DB pour le login
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            out.println("BIENVENUE à la JavaBank !");

            String request;
            while ((request = in.readLine()) != null) {
                System.out.println("[Reçu] " + request);
                String[] parts = request.split(" ");
                String command = parts[0].toUpperCase();

                // --- GESTION LOGIN ---
                if ("LOGIN".equals(command)) {
                    if (parts.length < 3) {
                        out.println("ERREUR: Usage LOGIN <user> <pass>");
                    } else {
                        User user = userDAO.login(parts[1], parts[2]);
                        if (user != null) {
                            this.currentUser = user;
                            out.println("SUCCES: Bienvenue " + user.getUsername());
                        } else {
                            out.println("ECHEC: Identifiants incorrects.");
                        }
                    }
                }

                // --- GESTION MY_ACCOUNTS (Pour le Dashboard) ---
                else if ("MY_ACCOUNTS".equals(command)) {
                    if (currentUser == null) {
                        out.println("ERREUR: Non connecté.");
                    } else {
                        List<Account> myAccounts = accountDAO.getAccountsByUserId(currentUser.getId());

                        if (myAccounts.isEmpty()) {
                            out.println("INFO: Aucun compte trouvé.");
                        } else {
                            // Construction de la réponse : "NUMERO - SOLDE DH; NUMERO2..."
                            StringBuilder sb = new StringBuilder("SUCCES_LIST:");
                            for (Account a : myAccounts) {
                                sb.append(a.getAccountNumber())
                                        .append(" - ")
                                        .append(a.getBalance())
                                        .append(" DH;");
                            }
                            out.println(sb.toString());
                        }
                    }
                }

                // --- GESTION TRANSFER ---
                else if ("TRANSFER".equals(command)) {
                    // Format: TRANSFER <src> <dest> <amount>
                    if (parts.length < 4) {
                        out.println("ERREUR: Usage TRANSFER <src> <dest> <amount>");
                    } else {
                        String srcId = parts[1];
                        String destId = parts[2];
                        double amount;
                        try {
                            amount = Double.parseDouble(parts[3]);
                        } catch (NumberFormatException e) {
                            out.println("ERREUR: Montant invalide.");
                            continue; // Passe au tour suivant de la boucle
                        }

                        // 1. Récupérer les objets comptes
                        Account srcAccount = accountDAO.findAccount(srcId);
                        Account destAccount = accountDAO.findAccount(destId);

                        if (srcAccount == null || destAccount == null) {
                            out.println("ERREUR: Un des comptes n'existe pas.");
                        } else {
                            // 2. Vérification de sécurité (Est-ce MON compte ?)
                            if (currentUser.getId() != srcAccount.getUserId() && !currentUser.isAdmin()) {
                                out.println("ERREUR: Vous ne pouvez pas débiter ce compte.");
                            } else {
                                // 3. EXECUTION SYNCHRONISÉE (Thread-Safe)
                                // On verrouille le compte source pour éviter qu'il soit vidé en double
                                synchronized (srcAccount) {
                                    if (srcAccount.withdraw(amount)) {
                                        // Si le retrait marche, on dépose sur l'autre
                                        synchronized (destAccount) {
                                            destAccount.deposit(amount);
                                        }

                                        // 4. Mettre à jour la Base de Données
                                        accountDAO.updateBalance(srcAccount);
                                        accountDAO.updateBalance(destAccount);

                                        out.println("SUCCES: Virement effectué !");
                                        System.out.println("Virement : " + amount + " de " + srcId + " vers " + destId);
                                    } else {
                                        out.println("ERREUR: Solde insuffisant.");
                                    }
                                }
                            }
                        }
                    }
                }

                // --- GESTION BALANCE (Ancien test console) ---
                else if ("BALANCE".equals(command)) {
                    // Ta logique existante pour BALANCE (si tu veux la garder)
                    if (parts.length < 2) {
                        out.println("ERREUR: Usage BALANCE <id>");
                    } else {
                        Account acc = accountDAO.findAccount(parts[1]);
                        if (acc != null) {
                            out.println("SUCCES: Solde = " + acc.getBalance());
                        } else {
                            out.println("ERREUR: Compte introuvable.");
                        }
                    }
                }

                // --- GESTION BATCH (CSV) ---
                else if ("BATCH".equals(command)) {
                    // Sécurité : Seul l'ADMIN a le droit de faire ça
                    if (currentUser == null || !currentUser.isAdmin()) {
                        out.println("ERREUR: Accès réservé aux administrateurs.");
                    } else {
                        String filename = (parts.length > 1) ? parts[1] : "virements_batch.csv";

                        // On lance le traitement dans un nouveau Thread pour ne pas bloquer le serveur
                        // C'est ici qu'on valide l'exigence "Parallélisme"
                        new Thread(() -> {
                            System.out.println("Lancement du batch CSV...");
                            com.bank.data.CsvProcessor processor = new com.bank.data.CsvProcessor();
                            processor.processBatchFile(filename);
                        }).start();

                        out.println("SUCCES: Traitement par lots démarré en arrière-plan.");
                    }
                }

                // --- GESTION EXIT ---
                else if ("EXIT".equals(command)) {
                    out.println("AU REVOIR");
                    break;
                }

                else {
                    out.println("ERREUR: Commande inconnue.");
                }
            }
        } catch (IOException e) {
            System.err.println("Client déconnecté.");
        }
    }
}