package com.bank.model;

import java.io.Serializable;

public class User implements Serializable {

    private int id;
    private String username;
    private String passwordHash;
    private UserRole role;

    public enum UserRole {
        ADMIN, CLIENT
    }

    public User(String username, String passwordHash, UserRole role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public User(int id, String username, String passwordHash, UserRole role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public UserRole getRole() { return role; }

    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", username='" + username + '\'' + ", role=" + role + '}';
    }
}