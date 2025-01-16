package com.example.lkdientu.utils;

import javafx.scene.control.Alert;

public class AlertUtils {
    private AlertUtils() {
    }

    public static void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}
