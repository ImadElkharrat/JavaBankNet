package com.bank.view;

import com.bank.network.NetworkClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

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
                if ("admin".equalsIgnoreCase(user)) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin_panel.fxml"));
                    Parent root = loader.load();


                    Stage stage = (Stage) usernameField.getScene().getWindow();
                    stage.setTitle("Administration - JavaBank");
                    stage.setScene(new Scene(root));

                } else {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard_view.fxml"));
                    Parent root = loader.load();

                    DashboardController controller = loader.getController();
                    controller.initData(user);

                    Stage stage = (Stage) usernameField.getScene().getWindow();
                    stage.setTitle("Tableau de bord");
                    stage.setScene(new Scene(root));
                }

            } catch (IOException e) {
                e.printStackTrace();
                showError("Erreur de chargement de la vue.");
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