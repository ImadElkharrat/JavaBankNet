package com.bank.view;

import com.bank.network.NetworkClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.PrintWriter;

public class AdminController {

    @FXML private TextField fundAccountField, fundAmountField;
    @FXML private TextField newAccNumField, newAccUserField, newAccBalanceField;
    @FXML private TextField delAccNumField;
    @FXML private TextField searchUserField, historyAccField;
    @FXML private Label statusLabel, searchResultLabel;


    @FXML
    private void handleGoToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard_view.fxml"));
            Parent root = loader.load();

            DashboardController controller = loader.getController();
            controller.initData("admin");

            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.setTitle("Tableau de bord (Vue Admin)");
            stage.setScene(new Scene(root));

        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Erreur: Impossible d'ouvrir le Dashboard.");
        }
    }


    @FXML
    private void handleDeposit() {
        String acc = fundAccountField.getText();
        String amt = fundAmountField.getText();
        sendCmd("ADMIN_DEPOSIT " + acc + " " + amt + " DEPOSIT ");
    }

    @FXML
    private void handleWithdraw() {
        String acc = fundAccountField.getText();
        String amt = fundAmountField.getText();
        sendCmd("ADMIN_WITHDRAW " + acc + " " + amt + " WITHDRAW ");
    }

    @FXML
    private void handleCreate() {
        String acc = newAccNumField.getText();
        String user = newAccUserField.getText();
        String bal = newAccBalanceField.getText();
        sendCmd("ADMIN_CREATE " + acc + " " + user + " COURANT " + bal);
    }

    @FXML
    private void handleDelete() {
        String acc = delAccNumField.getText();
        sendCmd("ADMIN_DELETE " + acc);
    }

    @FXML
    private void handleSearchUser() {
        String user = searchUserField.getText();
        String response = NetworkClient.getInstance().sendRequest("ADMIN_FIND_ACCOUNTS " + user);

        if (response.startsWith("SUCCES_LIST:")) {
            String clean = response.substring(12).replace(";", "\n");
            searchResultLabel.setText("Comptes de " + user + " :\n" + clean);
        } else {
            searchResultLabel.setText(response);
        }
    }

    @FXML
    private void handleGetHistory() {
        String acc = historyAccField.getText();
        String response = NetworkClient.getInstance().sendRequest("GET_HISTORY " + acc);

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Historique Admin");
        alert.setHeaderText("Historique du compte " + acc);
        alert.setContentText(response.length() > 500 ? response.substring(0, 500) + "..." : response);
        alert.showAndWait();
    }

    @FXML
    private void handleExportExcel() {
        String acc = historyAccField.getText();
        if (acc.isEmpty()) {
            statusLabel.setText("Veuillez entrer un numéro de compte.");
            return;
        }

        String response = NetworkClient.getInstance().sendRequest("GET_HISTORY " + acc);

        if (response.startsWith("SUCCES_HISTORY:")) {
            StringBuilder csvContent = new StringBuilder();
            csvContent.append("ID;Type;Montant;Source;Destination;Date\n");

            String rawData = response.substring(15);
            String[] lines = rawData.split(";");

            for (String line : lines) {
                if (!line.isBlank()) {
                    csvContent.append(line.replace("|", ";")).append("\n");
                }
            }

            saveToCsvFile(acc, csvContent.toString());

        } else {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Erreur Export");
            alert.setHeaderText("Impossible d'exporter l'historique");
            alert.setContentText(response);
            alert.showAndWait();
        }
    }

    private void saveToCsvFile(String accountId, String content) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer l'historique Excel");
        fileChooser.setInitialFileName("Historique_" + accountId + ".csv");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Excel/CSV", "*.csv"));

        Stage stage = (Stage) statusLabel.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.write(content);
                statusLabel.setText("Export réussi : " + file.getName());
            } catch (Exception e) {
                e.printStackTrace();
                statusLabel.setText("Erreur lors de l'enregistrement du fichier.");
            }
        }
    }

    private void sendCmd(String cmd) {
        String response = NetworkClient.getInstance().sendRequest(cmd);
        statusLabel.setText(response);
    }
}