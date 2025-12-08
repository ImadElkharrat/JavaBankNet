package com.bank.model;

import java.io.Serializable;

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

    public String getAccountNumber() { return accountNumber; }
    public synchronized double getBalance() { return balance; }


    public synchronized void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
            System.out.println("Dépot effectué sur " + accountNumber + ". Nouveau solde : " + this.balance);
        }
    }


    public synchronized boolean withdraw(double amount) {
        if (amount > 0 && this.balance >= amount) {
            this.balance -= amount;
            System.out.println("Retrait effectué sur " + accountNumber + ". Nouveau solde : " + this.balance);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Compte " + accountNumber + " [" + type + "] : " + balance + " DH";
    }
}