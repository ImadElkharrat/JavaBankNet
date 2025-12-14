package com.bank.view;

import com.bank.model.Transaction;
import com.bank.network.NetworkClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Button adminBatchButton;

    // Liste des comptes (Gauche)
    @FXML private ListView<String> accountsList;

    // Tableaux des transactions (Centre)
    @FXML private TableView<Transaction> historyTable;
    @FXML private TableColumn<Transaction, String> colDate;
    @FXML private TableColumn<Transaction, String> colType;
    @FXML private TableColumn<Transaction, String> colSource;
    @FXML private TableColumn<Transaction, String> colDest;
    @FXML private TableColumn<Transaction, Double> colAmount;

    public void initData(String username) {
        welcomeLabel.setText("Bonjour, " + username);

        if ("admin".equalsIgnoreCase(username)) {
            adminBatchButton.setVisible(true);
        }

        // Configuration des colonnes du tableau
        // Ces noms ("timestamp", "type", etc.) doivent correspondre aux Getters de ton modèle Transaction
        colDate.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colSource.setCellValueFactory(new PropertyValueFactory<>("sourceAccountId"));
        colDest.setCellValueFactory(new PropertyValueFactory<>("destAccountId"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        // Ajout d'un écouteur : Quand on clique sur un compte, on charge son historique
        accountsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // Le format dans la liste est "NUMERO - SOLDE DH..."
                // On extrait juste le numéro (tout ce qui est avant le " - ")
                String accountNumber = newVal.split(" - ")[0];
                loadHistory(accountNumber);
            }
        });

        handleRefresh();
    }

    @FXML
    private void handleRefresh() {
        accountsList.getItems().clear();
        String response = NetworkClient.getInstance().sendRequest("MY_ACCOUNTS");

        if (response.startsWith("SUCCES_LIST:")) {
            String rawData = response.substring(12);
            String[] accounts = rawData.split(";");
            for (String acc : accounts) {
                if (!acc.isBlank()) accountsList.getItems().add(acc);
            }
        } else {
            accountsList.getItems().add("Erreur connexion");
        }
    }

    private void loadHistory(String accountId) {
        historyTable.getItems().clear();
        String response = NetworkClient.getInstance().sendRequest("GET_HISTORY " + accountId);

        if (response.startsWith("SUCCES_HISTORY:")) {
            String rawData = response.substring(15); // Enlever le préfixe
            String[] lines = rawData.split(";");

            ObservableList<Transaction> transactions = FXCollections.observableArrayList();

            for (String line : lines) {
                if (line.isBlank()) continue;
                String[] parts = line.split("\\|"); // Le séparateur est |

                // Format attendu: ID|TYPE|AMOUNT|SOURCE|DEST|DATE
                if (parts.length >= 6) {
                    try {
                        Transaction tx = new Transaction(
                                Integer.parseInt(parts[0]),     // ID
                                parts[3],                       // Source
                                parts[4],                       // Dest
                                Double.parseDouble(parts[2]),   // Amount
                                com.bank.model.Transaction.TransactionType.valueOf(parts[1]), // Type
                                LocalDateTime.parse(parts[5])   // Date (ISO Format)
                        );
                        transactions.add(tx);
                    } catch (Exception e) {
                        System.err.println("Erreur parsing transaction: " + e.getMessage());
                    }
                }
            }
            historyTable.setItems(transactions);
        }
    }

    @FXML
    private void handleTransfer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/transfer_view.fxml"));
            Scene scene = new Scene(loader.load(), 350, 400); // Légèrement plus grand pour le style
            Stage stage = new Stage();
            stage.setTitle("Nouveau Virement");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBatch() {
        NetworkClient.getInstance().sendRequest("BATCH virements_batch.csv");
        handleRefresh();
    }

    @FXML
    private void handleLogout() throws IOException {
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login_view.fxml"));
        stage.setScene(new Scene(loader.load()));
    }
}