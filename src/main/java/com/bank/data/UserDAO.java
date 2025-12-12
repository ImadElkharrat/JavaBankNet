package com.bank.data;

import com.bank.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    private Connection connection;

    public UserDAO() {
        this.connection = DatabaseSource.getDataSource().getConnection();
    }

    public User login(String username, String password) {
        // Attention: Assure-toi que ta table s'appelle 'users' et la colonne mot de passe 'password_hash' ou 'password'
        // Adapte la requête SQL ci-dessous selon ta base actuelle !
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // On gère le rôle simplement
                    String roleStr = rs.getString("role");
                    User.UserRole role = User.UserRole.valueOf(roleStr); // "ADMIN" ou "CLIENT"

                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            role
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL Login : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur: Rôle inconnu en base.");
        }
        return null;
    }
}