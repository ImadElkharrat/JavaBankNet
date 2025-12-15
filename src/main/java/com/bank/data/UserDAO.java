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
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String roleStr = rs.getString("role");
                    User.UserRole role = User.UserRole.valueOf(roleStr);

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

    public com.bank.model.User findUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (java.sql.PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // On reconstruit l'objet User
                    // Adaptez "role" selon comment il est stocké (String ou Enum)
                    String roleStr = rs.getString("role");
                    /* Si votre constructeur attend une String pour le rôle :
                    return new com.bank.model.User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            roleStr
                    );
                    */

                    // SI VOTRE CONSTRUCTEUR ATTEND UN ENUM (comme vu précédemment), UTILISEZ CECI À LA PLACE :
                    return new com.bank.model.User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        com.bank.model.User.UserRole.valueOf(roleStr)
                    );

                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}