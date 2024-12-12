package com.example.lkdientu.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddProductModalController {
    @FXML
    private Spinner<Integer> inputQuantity;

    @FXML
    private TextField txtPrice;

    @FXML
    private TextArea txtProductDescription;

    @FXML
    private TextArea txtProductInformation;

    @FXML
    private TextField txtProductName;

    @FXML
    private TextField txtSale;

    @FXML
    private Button btnClose;

    @FXML
    private Button btnSave;

    private Stage modalStage;

    public void setModalStage(Stage modalStage) {
        this.modalStage = modalStage;
    }

    @FXML
    public void initialize() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0);
        inputQuantity.setValueFactory(valueFactory);

        btnClose.setOnAction(e -> closeModal());
        btnSave.setOnAction(e -> saveProduct());
    }

    private void closeModal() {
        modalStage.close();
    }

    private void saveProduct() {
        String productName = txtProductName.getText();
        String productPrice = txtPrice.getText();

        // Logic xử lý lưu sản phẩm (ví dụ: thêm vào danh sách sản phẩm, lưu vào database, v.v.)
        System.out.println("Product Name: " + productName);
        System.out.println("Product Price: " + productPrice);

        // Sau khi lưu, đóng modal
        modalStage.close();
    }
}
