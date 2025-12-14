package com.bank.data;

import com.bank.model.Transaction;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    private Connection connection;

    public TransactionDAO() {
        this.connection = DatabaseSource.getDataSource().getConnection();
    }


    public void logTransaction(String sourceAccountId, String destAccountId, double amount, String type) {
        String sql = "INSERT INTO transactions (source_acc, dest_acc, amount, type, timestamp) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, sourceAccountId);
            stmt.setString(2, destAccountId);
            stmt.setDouble(3, amount);
            stmt.setString(4, type);
            stmt.setTimestamp(5, Timestamp.valueOf(java.time.LocalDateTime.now()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur logTransaction: " + e.getMessage());
        }
    }


    public List<Transaction> getHistory(String accountId) {
        List<Transaction> history = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE source_acc = ? OR dest_acc = ? ORDER BY timestamp DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, accountId);
            stmt.setString(2, accountId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Transaction tx = new Transaction(
                            rs.getInt("id"),
                            rs.getString("source_acc"),
                            rs.getString("dest_acc"),
                            rs.getDouble("amount"),
                            Transaction.TransactionType.valueOf(rs.getString("type")),
                            rs.getTimestamp("timestamp").toLocalDateTime()
                    );
                    history.add(tx);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur getHistory: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur conversion Type transaction: " + e.getMessage());
        }
        return history;
    }
}