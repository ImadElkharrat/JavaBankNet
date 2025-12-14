package com.bank.network;

import com.bank.data.AccountDAO;
import com.bank.data.TransactionDAO;
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
    private TransactionDAO transactionDAO;
    private User currentUser;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.accountDAO = new AccountDAO();
        this.userDAO = new UserDAO();
        this.transactionDAO = new TransactionDAO();
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

                else if ("MY_ACCOUNTS".equals(command)) {
                    if (currentUser == null) {
                        out.println("ERREUR: Non connecté.");
                    } else {
                        List<Account> myAccounts = accountDAO.getAccountsByUserId(currentUser.getId());

                        if (myAccounts.isEmpty()) {
                            out.println("INFO: Aucun compte trouvé.");
                        } else {
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

                else if ("TRANSFER".equals(command)) {
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
                            continue;
                        }

                        Account srcAccount = accountDAO.findAccount(srcId);
                        Account destAccount = accountDAO.findAccount(destId);

                        if (srcAccount == null || destAccount == null) {
                            out.println("ERREUR: Un des comptes n'existe pas.");
                        } else {
                            if (currentUser.getId() != srcAccount.getUserId() && !currentUser.isAdmin()) {
                                out.println("ERREUR: Vous ne pouvez pas débiter ce compte.");
                            } else {
                                synchronized (srcAccount) {
                                    if (srcAccount.withdraw(amount)) {
                                        synchronized (destAccount) {
                                            destAccount.deposit(amount);
                                        }

                                        accountDAO.updateBalance(srcAccount);
                                        accountDAO.updateBalance(destAccount);

                                        transactionDAO.logTransaction(srcId, destId, amount, "VIREMENT");

                                        out.println("SUCCES: Virement effectué !");
                                    } else {
                                        out.println("ERREUR: Solde insuffisant.");
                                    }
                                }
                            }
                        }
                    }
                }

                else if ("GET_HISTORY".equals(command)) {
                    if (parts.length < 2) {
                        out.println("ERREUR: Usage GET_HISTORY <accountId>");
                    } else {
                        String targetAccountId = parts[1];

                        Account acc = accountDAO.findAccount(targetAccountId);
                        if (acc == null) {
                            out.println("ERREUR: Compte introuvable.");
                        }
                        else if (currentUser.getId() != acc.getUserId() && !currentUser.isAdmin()) {
                            out.println("ERREUR: Accès refusé à l'historique de ce compte.");
                        }
                        else {
                            List<com.bank.model.Transaction> history = transactionDAO.getHistory(targetAccountId);

                            if (history.isEmpty()) {
                                out.println("INFO: Aucune transaction trouvée.");
                            } else {
                                StringBuilder sb = new StringBuilder("SUCCES_HISTORY:");

                                for (com.bank.model.Transaction tx : history) {
                                    sb.append(tx.getId()).append("|")
                                            .append(tx.getType()).append("|")
                                            .append(tx.getAmount()).append("|")
                                            .append(tx.getSourceAccountId()).append("|")
                                            .append(tx.getDestAccountId()).append("|")
                                            .append(tx.getTimestamp().toString())
                                            .append(";");
                                }
                                out.println(sb.toString());
                            }
                        }
                    }
                }

                else if ("BALANCE".equals(command)) {
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

                else if ("BATCH".equals(command)) {
                    if (currentUser == null || !currentUser.isAdmin()) {
                        out.println("ERREUR: Accès réservé aux administrateurs.");
                    } else {
                        String filename = (parts.length > 1) ? parts[1] : "virements_batch.csv";

                        new Thread(() -> {
                            System.out.println("Lancement du batch CSV...");
                            com.bank.data.CsvProcessor processor = new com.bank.data.CsvProcessor();
                            processor.processBatchFile(filename);
                        }).start();

                        out.println("SUCCES: Traitement par lots démarré en arrière-plan.");
                    }
                }

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