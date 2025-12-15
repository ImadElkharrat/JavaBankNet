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
        sendCmd("ADMIN_DEPOSIT " + acc + " " + amt);
    }

    @FXML
    private void handleWithdraw() {
        String acc = fundAccountField.getText();
        String amt = fundAmountField.getText();
        sendCmd("ADMIN_WITHDRAW " + acc + " " + amt);
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

    private void sendCmd(String cmd) {
        String response = NetworkClient.getInstance().sendRequest(cmd);
        statusLabel.setText(response);
    }
}