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

    public void logTransaction(Transaction tx) {
        String sql = "INSERT INTO transactions (sourceAccountId, destAccountId, amount, timestamp) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tx.getSourceAccountId());
            stmt.setString(2, tx.getDestAccountId());
            stmt.setDouble(3, tx.getAmount());
            stmt.setTimestamp(4, Timestamp.valueOf(tx.getTimestamp()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Transaction> getHistory(String accountId) {
        List<Transaction> history = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE sourceAccountId = ? OR destAccountId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, accountId);
            stmt.setString(2, accountId);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }
}