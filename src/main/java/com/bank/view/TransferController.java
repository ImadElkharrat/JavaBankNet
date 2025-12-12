package com.bank.view;

import com.bank.network.NetworkClient;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TransferController {

    @FXML private TextField sourceField;
    @FXML private TextField destField;
    @FXML private TextField amountField;
    @FXML private Label statusLabel;

    @FXML
    private void handleConfirm() {
        String source = sourceField.getText();
        String dest = destField.getText();
        String amount = amountField.getText();

        if (source.isEmpty() || dest.isEmpty() || amount.isEmpty()) {
            statusLabel.setText("Remplissez tous les champs.");
            return;
        }

        // Envoi de la commande au serveur
        String command = "TRANSFER " + source + " " + dest + " " + amount;
        String response = NetworkClient.getInstance().sendRequest(command);

        if (response.startsWith("SUCCES")) {
            // Fermer la fenêtre si c'est bon
            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.close();
        } else {
            statusLabel.setText(response); // Afficher l'erreur du serveur
        }
    }
}