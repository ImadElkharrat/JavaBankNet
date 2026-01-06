package com.bank.data;

import com.bank.model.Account;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CsvProcessor {

    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;

    public CsvProcessor() {
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
    }

    public void processBatchFile(String filePath) {
        System.out.println("Début du traitement du fichier : " + filePath);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");

                if (data.length >= 3) {
                    String srcId = data[0].trim();
                    String destId = data[1].trim();
                    double amount;

                    try {
                        amount = Double.parseDouble(data[2].trim());
                    } catch (NumberFormatException e) {
                        System.err.println("Erreur montant invalide ligne : " + line);
                        continue;
                    }

                    processSingleTransfer(srcId, destId, amount);
                }
            }
            System.out.println("Fin du traitement batch.");

        } catch (IOException e) {
            System.err.println("Erreur lecture fichier CSV : " + e.getMessage());
        }
    }

    private void processSingleTransfer(String srcId, String destId, double amount) {
        Account srcAccount = accountDAO.findAccount(srcId);
        Account destAccount = accountDAO.findAccount(destId);

        if (srcAccount != null && destAccount != null) {
            synchronized (srcAccount) {
                if (srcAccount.withdraw(amount)) {
                    synchronized (destAccount) {
                        destAccount.deposit(amount);
                    }

                    accountDAO.updateBalance(srcAccount);
                    accountDAO.updateBalance(destAccount);

                    transactionDAO.logTransaction(srcId, destId, amount, "VIREMENT");

                    System.out.println("Succès Batch : " + amount + " de " + srcId + " vers " + destId);
                } else {
                    System.err.println("Échec Batch : Solde insuffisant pour " + srcId);
                }
            }
        } else {
            System.err.println("Échec Batch : Compte introuvable (" + srcId + " ou " + destId + ")");
        }
    }
}