package com.bank.view;

import com.bank.model.Transaction;
import com.bank.network.NetworkClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Button adminBatchButton;
    @FXML private Button adminPanelButton;

    @FXML private ListView<String> accountsList;

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
            adminPanelButton.setVisible(true);
        } else {
        adminBatchButton.setVisible(false);
        adminPanelButton.setVisible(false);
        }

        colDate.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colSource.setCellValueFactory(new PropertyValueFactory<>("sourceAccountId"));
        colDest.setCellValueFactory(new PropertyValueFactory<>("destAccountId"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        accountsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String accountNumber = newVal.split(" - ")[0];
                loadHistory(accountNumber);
            }
        });

        handleRefresh();
    }

    @FXML
    private void handleOpenAdmin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin_panel.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Administration - JavaBank");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur: Impossible d'ouvrir admin_panel.fxml. Vérifie le chemin !");
        }
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
            String rawData = response.substring(15);
            String[] lines = rawData.split(";");

            ObservableList<Transaction> transactions = FXCollections.observableArrayList();

            for (String line : lines) {
                if (line.isBlank()) continue;
                String[] parts = line.split("\\|");

                if (parts.length >= 6) {
                    try {
                        Transaction tx = new Transaction(
                                Integer.parseInt(parts[0]),
                                parts[3],
                                parts[4],
                                Double.parseDouble(parts[2]),
                                com.bank.model.Transaction.TransactionType.valueOf(parts[1]),
                                LocalDateTime.parse(parts[5])
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
            Scene scene = new Scene(loader.load(), 350, 400);
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