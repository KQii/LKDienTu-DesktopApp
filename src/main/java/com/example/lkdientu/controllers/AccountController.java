package com.example.lkdientu.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class AccountController {

    @FXML
    private Button btnTimKiem;

    @FXML
    private TableColumn<?, ?> colProductCatalog;

    @FXML
    private TableColumn<?, ?> colProductDescription;

    @FXML
    private TableColumn<?, ?> colProductID;

    @FXML
    private TableColumn<?, ?> colProductName;

    @FXML
    private TableView<?> tableProduct;

    @FXML
    private TextField txtTimKiem;

    @FXML
    public void initialize() {
        System.out.println("AccountController loaded");
    }
}
