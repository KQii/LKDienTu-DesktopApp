package com.example.lkdientu.controllers;

import com.example.lkdientu.LKDienTuApplication;
import com.example.lkdientu.SessionManager;
import com.example.lkdientu.models.Product;
import com.example.lkdientu.models.ProductCatalog;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainController {
    @FXML
    private Button btnHomePage;

    @FXML
    private Button btnProductPage;

    @FXML
    private Button btnDangXuat;

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab tabHomePage;

    @FXML
    public void initialize() {
    }

    private void openOrSwitchTab(String tabTitle, String fxmlPath) {
        // Kiểm tra xem TabPane đã có tab với tiêu đề `tabTitle` chưa
        for (Tab tab : tabPane.getTabs()) {
            if (tabTitle.equals(tab.getText())) {
                // Nếu tab đã tồn tại, chuyển đến tab đó
                tabPane.getSelectionModel().select(tab);
                return; // Thoát ra, không tạo tab mới
            }
        }

        // Nếu tab chưa tồn tại, tạo tab mới
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            AnchorPane tabContent = loader.load();

            Tab newTab = new Tab(tabTitle); // Tạo tab mới
            newTab.setContent(tabContent); // Đặt nội dung cho tab

            tabPane.getTabs().add(newTab); // Thêm tab vào TabPane
            tabPane.getSelectionModel().select(newTab); // Chuyển đến tab vừa tạo
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHomeTab() {
        tabPane.getSelectionModel().select(tabHomePage);
    }

    @FXML
    private void handleAccountTab() {
        openOrSwitchTab("Tài khoản", "/com/example/lkdientu/views/account-view.fxml");
    }

    @FXML
    private void handleProductTab() {
        openOrSwitchTab("Sản phẩm", "/com/example/lkdientu/views/product-view.fxml");
    }

    @FXML
    private void handleProductCatalogTab() { openOrSwitchTab("Danh mục", "/com/example/lkdientu/views/productCatalog-view.fxml"); }

    @FXML
    void handleBtnDangXuat(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc muốn đăng xuất?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            // Xóa token và quay lại màn hình đăng nhập
            SessionManager.getInstance().logout();
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(LKDienTuApplication.class.getResource("views/login-view.fxml"));
                Stage stage = (Stage) btnDangXuat.getScene().getWindow();
                Scene loginScene = new Scene(fxmlLoader.load());
                stage.setScene(loginScene);
                stage.setTitle("Đăng nhập");
                stage.centerOnScreen();
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}