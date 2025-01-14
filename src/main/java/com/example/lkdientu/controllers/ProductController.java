package com.example.lkdientu.controllers;

import com.example.lkdientu.LKDienTuApplication;
import com.example.lkdientu.models.ApiResponse;
import com.example.lkdientu.models.Product;
import com.example.lkdientu.models.ProductCatalog;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.List;

public class ProductController {
    private String API_URL = "http://127.0.0.1:3000/api/v2/products/";

    @FXML
    private Button btnLoc;

    @FXML
    private Button btnRefresh;

    @FXML
    private Button btnSua;

    @FXML
    private Button btnThem;

    @FXML
    private Button btnTimKiem;

    @FXML
    private Button btnUndo;

    @FXML
    private Button btnXoa;

    @FXML
    private TableColumn<Product, Boolean> colHide;

    @FXML
    private TableColumn<Product, String> colImage;

    @FXML
    private TableColumn<Product, Double> colPrice;

    @FXML
    private TableColumn<Product, String> colProductCatalog;

    @FXML
    private TableColumn<Product, String> colProductDescription;

    @FXML
    private TableColumn<Product, Integer> colProductID;

    @FXML
    private TableColumn<Product, String> colProductInformation;

    @FXML
    private TableColumn<Product, String> colProductName;

    @FXML
    private TableColumn<Product, Integer> colQuantity;

    @FXML
    private TableColumn<Product, Double> colSale;

    @FXML
    private TableView<Product> tableProduct;

    @FXML
    private TextField txtID;

    @FXML
    private TextField txtPrice;

    @FXML
    private TextArea txtProductDescription;

    @FXML
    private TextArea txtProductInformation;

    @FXML
    private TextArea txtProductName;

    @FXML
    private TextField txtTimKiem;

    @FXML
    private Spinner<Integer> spnQuantity;

    @FXML
    private CheckBox chkHide;

    @FXML
    private ComboBox<String> cmbProductCatalog;

    private ObservableList<Product> productList = FXCollections.observableArrayList();

    private ObservableList<String> productCatalogList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        System.out.println("ProductController loaded");

        // Initialize ValueFactory for Spinner
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 0);
        spnQuantity.setValueFactory(valueFactory);

        cmbProductCatalog.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println("Selected: " + newValue); // Log ra mục được chọn
            }
        });


        // Gắn các cột trong TableView với thuộc tính của Product
        colProductID.setCellValueFactory(new PropertyValueFactory<>("productID"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colProductDescription.setCellValueFactory(new PropertyValueFactory<>("describeProduct"));
        colImage.setCellValueFactory(new PropertyValueFactory<>("image"));
        colProductInformation.setCellValueFactory(new PropertyValueFactory<>("productInformation"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colSale.setCellValueFactory(new PropertyValueFactory<>("sale"));
        colHide.setCellValueFactory(new PropertyValueFactory<>("hide"));
//        colProductCatalog.setCellValueFactory(new PropertyValueFactory<>("productCatalog"));
        colProductCatalog.setCellValueFactory(cellData -> {
            ProductCatalog catalog = cellData.getValue().getProductCatalog();
            return new SimpleStringProperty(catalog != null ? catalog.getProductCatalogName() : "N/A");
        });

        // Gắn ObservableList vào TableView
        tableProduct.setItems(productList);

        // Gọi API để lấy dữ liệu
        fetchProducts();

        // Fill combobox
        fetchProductCatalogs();

        // Thêm listener cho TableView
        tableProduct.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateFields(newValue); // Gọi phương thức để đổ dữ liệu vào các trường
            }
        });
    }

    public void fetchProducts() {
        try {
            // Tạo HttpClient
            HttpClient client = HttpClient.newHttpClient();

            // Tạo HttpRequest
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL)) // URL của API
                    .GET()
                    .build();

            // Gửi request và nhận response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Kiểm tra trạng thái
            if (response.statusCode() == 200) {
                // Parse JSON thành ApiResponse
                ObjectMapper objectMapper = new ObjectMapper();
                ApiResponse apiResponse = objectMapper.readValue(response.body(), ApiResponse.class);

                // Lấy danh sách sản phẩm từ ApiResponse
                List<Product> products = apiResponse.getData().getProducts();

                // Cập nhật dữ liệu vào ObservableList
                productList.setAll(products);
            } else {
                System.out.println("Lỗi API: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fetchProductCatalogs() {
        String catalogApiUrl = "http://127.0.0.1:3000/api/v1/productCatalogs/"; // Thay bằng API thực tế

        try {
            // Tạo HttpClient
            HttpClient client = HttpClient.newHttpClient();

            // Tạo HttpRequest
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(catalogApiUrl)) // URL của API
                    .GET()
                    .build();

            // Gửi request và nhận response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Kiểm tra trạng thái
            if (response.statusCode() == 200) {
                // Parse JSON thành danh sách ProductCatalog
                ObjectMapper objectMapper = new ObjectMapper();
                ApiResponse apiResponse = objectMapper.readValue(response.body(), ApiResponse.class);

                // Lấy danh sách ProductCatalog từ ApiResponse
                List<ProductCatalog> catalogs = apiResponse.getData().getProductCatalogs();

                // Cập nhật productCatalogList
                productCatalogList.clear();
                for (ProductCatalog catalog : catalogs) {
                    // Nếu không có childs, thêm tên parent
                    if (catalog.getChilds().isEmpty()) {
                        productCatalogList.add(catalog.getProductCatalogName());
                    } else {
                        // Nếu có childs, thêm tên parent - child
                        for (ProductCatalog child : catalog.getChilds()) {
                            productCatalogList.add(catalog.getProductCatalogName() + " - " + child.getProductCatalogName());
                        }
                    }
                }

                // Gắn danh sách vào ComboBox
                cmbProductCatalog.setItems(productCatalogList);
            } else {
                System.out.println("Lỗi API: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateFields(Product selectedProduct) {
        // Đổ dữ liệu vào TextField
        txtID.setText(String.valueOf(selectedProduct.getProductID()));
        txtProductName.setText(selectedProduct.getProductName());
        txtProductDescription.setText(selectedProduct.getDescribeProduct());
        txtProductInformation.setText(selectedProduct.getProductInformation());
        txtPrice.setText(String.valueOf(selectedProduct.getPrice()));

        // Đổ dữ liệu vào Spinner
        spnQuantity.getValueFactory().setValue(selectedProduct.getQuantity());

        // Đổ dữ liệu vào CheckBox
        chkHide.setSelected(!selectedProduct.isHide());

        // Đổ dữ liệu vào ComboBox
        if (selectedProduct.getProductCatalog() != null) {
            cmbProductCatalog.setValue(selectedProduct.getProductCatalog().getProductCatalogName());
        } else {
            cmbProductCatalog.setValue(null);
        }
    }
}
