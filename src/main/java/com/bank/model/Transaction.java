package com.bank.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Transaction implements Serializable {

    private int id;
    private String sourceAccountId;
    private String destAccountId;
    private double amount;
    private TransactionType type;
    private LocalDateTime timestamp;

    public enum TransactionType {
        TRANSFER, DEPOSIT, WITHDRAW, VIREMENT
    }

    public Transaction(String sourceAccountId, String destAccountId, double amount, TransactionType type) {
        this.sourceAccountId = sourceAccountId;
        this.destAccountId = destAccountId;
        this.amount = amount;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }

    public Transaction(int id, String sourceAccountId, String destAccountId, double amount, TransactionType type, LocalDateTime timestamp) {
        this.id = id;
        this.sourceAccountId = sourceAccountId;
        this.destAccountId = destAccountId;
        this.amount = amount;
        this.type = type;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public String getSourceAccountId() { return sourceAccountId; }
    public String getDestAccountId() { return destAccountId; }
    public double getAmount() { return amount; }
    public TransactionType getType() { return type; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("[%s] %s : %.2f (From: %s -> To: %s)",
                timestamp, type, amount, sourceAccountId, destAccountId);
    }
}