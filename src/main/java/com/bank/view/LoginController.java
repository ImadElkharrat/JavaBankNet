package com.bank.view;

import com.bank.network.NetworkClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (user.isEmpty() || pass.isEmpty()) return;

        String response = NetworkClient.getInstance().sendRequest("LOGIN " + user + " " + pass);

        if (response != null && response.startsWith("SUCCES")) {
            try {
                // Charger le Dashboard
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard_view.fxml"));
                javafx.scene.Parent root = loader.load();

                // Passer le nom d'utilisateur au contrôleur du Dashboard
                DashboardController dashboard = loader.getController();
                dashboard.initData(user);

                // Changer de scène
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root));

            } catch (java.io.IOException e) {
                e.printStackTrace();
                showError("Erreur chargement Dashboard.");
            }
        } else {
            showError("Identifiants incorrects.");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}