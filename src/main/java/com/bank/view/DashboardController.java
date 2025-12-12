package com.bank.view;

import com.bank.network.NetworkClient;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private ListView<String> accountsList;
    @FXML private javafx.scene.control.Button adminBatchButton;

    public void initData(String username) {
        welcomeLabel.setText("Bienvenue, " + username);
        if ("admin".equalsIgnoreCase(username)) {
            adminBatchButton.setVisible(true);
        }

        handleRefresh();
    }

    @FXML
    private void handleRefresh() {
        accountsList.getItems().clear();

        // C'est ici que la magie opère : on demande NOS comptes
        String response = NetworkClient.getInstance().sendRequest("MY_ACCOUNTS");

        if (response.startsWith("SUCCES_LIST:")) {
            String rawData = response.substring(12); // Enlever le préfixe
            String[] accounts = rawData.split(";"); // Séparer les comptes

            for (String acc : accounts) {
                if (!acc.isBlank()) {
                    accountsList.getItems().add(acc);
                }
            }
        } else {
            accountsList.getItems().add(response); // Afficher l'erreur ou l'info
        }
    }

    @FXML
    private void handleTransfer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/transfer_view.fxml"));
            Scene scene = new Scene(loader.load(), 300, 300);

            Stage stage = new Stage();
            stage.setTitle("Virement");
            stage.setScene(scene);
            stage.show(); // Ouvre une nouvelle petite fenêtre

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBatch() {
        // Envoie la commande pour traiter le fichier qui est à la racine du serveur
        String response = NetworkClient.getInstance().sendRequest("BATCH virements_batch.csv");

        // Affiche une petite alerte (ou juste en console pour faire simple)
        System.out.println("Réponse Serveur : " + response);

        // Rafraîchir la liste pour voir les changements éventuels
        handleRefresh();
    }

    @FXML
    private void handleLogout() throws IOException {
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login_view.fxml"));
        stage.setScene(new Scene(loader.load()));
    }
}