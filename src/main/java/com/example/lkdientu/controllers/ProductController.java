package com.example.lkdientu.controllers;

import com.example.lkdientu.LKDienTuApplication;
import com.example.lkdientu.models.Product;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProductController {
    @FXML
    private Button btnHomePage;

    @FXML
    private Button btnProductPage;

    @FXML
    private Button btnAddProduct;

    @FXML
    private TableColumn<Product, Integer> colHide;

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
    private TableColumn<Product, String> colActions;

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab tabHomePage;

    @FXML
    private Tab tabProductPage;

    @FXML
    private TableView<Product> tableProduct;

    private ObservableList<Product> productList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        tabPane.getTabs().remove(tabProductPage);

        btnHomePage.setOnAction(e -> {
            tabPane.getSelectionModel().select(tabHomePage);
        });

        btnProductPage.setOnAction(e -> {
            if (!tabPane.getTabs().contains(tabProductPage)) {
                tabPane.getTabs().add(tabProductPage);
            }
            tabPane.getSelectionModel().select(tabProductPage);
        });

        btnAddProduct.setOnAction(e -> openAddProductModal());

        colProductID.setCellValueFactory(new PropertyValueFactory<Product, Integer>("productID"));
        colProductName.setCellValueFactory(new PropertyValueFactory<Product, String>("productName"));
        colProductCatalog.setCellValueFactory(new PropertyValueFactory<Product, String>("productCatalog"));
        colProductDescription.setCellValueFactory(new PropertyValueFactory<Product, String>("productDescription"));
        colProductInformation.setCellValueFactory(new PropertyValueFactory<Product, String>("productInformation"));
        colImage.setCellValueFactory(new PropertyValueFactory<Product, String>("image"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<Product, Integer>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<Product, Double>("price"));
        colSale.setCellValueFactory(new PropertyValueFactory<Product, Double>("sale"));
        colHide.setCellValueFactory(new PropertyValueFactory<Product, Integer>("hide"));

        // Tải dữ liệu từ API
        loadProductsFromAPI("http://localhost:3000/api/v1/products/?limit=100", "GET");

        // Đưa dữ liệu vào TableView
        tableProduct.setItems(productList);

        // Tạo cột Action với nút sửa và xóa
        colActions.setCellFactory(new Callback<TableColumn<Product, String>, TableCell<Product, String>>() {
            @Override
            public TableCell<Product, String> call(TableColumn<Product, String> param) {
                return new TableCell<Product, String>() {
                    private final Button btnEdit = new Button("Sửa");
                    private final Button btnDelete = new Button("Xóa");

                    {
                        btnEdit.setOnAction(e -> {
                            Product product = getTableRow().getItem();
                            if (product != null) {
                                // Xử lý sửa sản phẩm
                                System.out.println("Edit product: " + product.getProductName());
                            }
                        });

                        btnDelete.setOnAction(e -> {
                            Product product = getTableRow().getItem();
                            if (product != null) {
                                // Xử lý xóa sản phẩm
                                System.out.println("Delete product: " + product.getProductName());
                                tableProduct.getItems().remove(product);
                            }
                        });
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            // Đặt các nút vào mỗi ô của cột Action
                            HBox hbox = new HBox(10, btnEdit, btnDelete);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });
    }

    private void loadProductsFromAPI(String apiUrl, String method) {
        try {
            // URL của API
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("HTTP GET Request Failed with Error Code : " + conn.getResponseCode());
            }

            // Đọc dữ liệu JSON trả về
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder jsonBuilder = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                jsonBuilder.append(output);
            }

            conn.disconnect();

            // Parse JSON
            JSONObject jsonResponse = new JSONObject(jsonBuilder.toString());
            JSONArray products = jsonResponse.getJSONObject("data").getJSONArray("products");

            // Duyệt qua danh sách sản phẩm và thêm vào ObservableList
            for (int i = 0; i < products.length(); i++) {
                JSONObject product = products.getJSONObject(i);

                int productID = product.getInt("ProductID");
                String productName = product.getString("ProductName");
                String productCatalog = product.getJSONObject("ProductCatalog").getString("ProductCatalogName");
                String productDescription = product.getString("DescribeProduct");
                String productInformation = product.optString("Product_Information", "Empty");
                String image = product.getString("Image");
                int quantity = product.getInt("Quantity");
                double price = product.getDouble("Price");
                double sale = product.getDouble("Sale");
                int hide = product.getInt("Hide");

                productList.add(new Product(productID, productName, productCatalog, productDescription, productInformation, image, quantity, price, sale, hide));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openAddProductModal() {
        try {
            // Nạp FXML cho cửa sổ modal
            FXMLLoader loader = new FXMLLoader(LKDienTuApplication.class.getResource("addProductModal-view.fxml"));
            Parent modalRoot = loader.load();

            // Tạo Stage mới cho modal
            Stage modalStage = new Stage();
            modalStage.setTitle("Thêm Sản Phẩm");
            modalStage.initModality(Modality.APPLICATION_MODAL); // Chặn giao diện khác khi modal mở
            modalStage.setResizable(false);
            modalStage.setScene(new Scene(modalRoot));

            // Thiết lập controller cho modal
            AddProductModalController controller = loader.getController();
            controller.setModalStage(modalStage);

            // Hiển thị modal
            modalStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}