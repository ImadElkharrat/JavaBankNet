package com.bank.model;

import java.io.Serializable;

// Serializable pour pouvoir envoyer l'objet via les Sockets plus tard
public class Account implements Serializable {

    private String accountNumber;
    private int userId;
    private double balance;
    private String type; // "COURANT" ou "EPARGNE"

    public Account(String accountNumber, int userId, double balance, String type) {
        this.accountNumber = accountNumber;
        this.userId = userId;
        this.balance = balance;
        this.type = type;
    }

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public synchronized double getBalance() { return balance; } // Lecture synchronisée aussi !

    /**
     * CRITIQUE : Méthode Synchronized
     * Empêche deux threads (deux clients) de modifier le solde en même temps.
     */
    public synchronized void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
            System.out.println("Dépot effectué sur " + accountNumber + ". Nouveau solde : " + this.balance);
        }
    }

    /**
     * CRITIQUE : Méthode Synchronized
     * Vérifie le solde ET retire l'argent de manière atomique.
     */
    public synchronized boolean withdraw(double amount) {
        if (amount > 0 && this.balance >= amount) {
            this.balance -= amount;
            System.out.println("Retrait effectué sur " + accountNumber + ". Nouveau solde : " + this.balance);
            return true; // Succès
        }
        return false; // Fonds insuffisants
    }

    @Override
    public String toString() {
        return "Compte " + accountNumber + " [" + type + "] : " + balance + " DH";
    }

    public int getUserId() { return userId; }
}