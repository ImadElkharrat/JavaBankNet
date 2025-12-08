package com.bank.data;

import com.bank.model.Account;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDAO {

    private Connection connection;

    public AccountDAO() {
        this.connection = DatabaseSource.getDataSource().getConnection();
    }


    public boolean createAccount(Account account, int userId) {
        String sql = "INSERT INTO accounts (account_number, user_id, balance, type) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, account.getAccountNumber());
            stmt.setInt(2, userId);
            stmt.setDouble(3, account.getBalance());
            stmt.setString(4, "COURANT");

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.err.println("Erreur SQL (Insert) : " + e.getMessage());
            return false;
        }
    }


    public Account findAccount(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Account(
                            rs.getString("account_number"),
                            rs.getInt("user_id"),
                            rs.getDouble("balance"),
                            rs.getString("type")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL (Select) : " + e.getMessage());
        }
        return null;
    }


    public boolean updateBalance(Account account) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, account.getBalance());
            stmt.setString(2, account.getAccountNumber());

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.err.println("Erreur SQL (Update) : " + e.getMessage());
            return false;
        }
    }
}