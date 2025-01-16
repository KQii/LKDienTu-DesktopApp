package com.example.lkdientu.controllers;

import com.example.lkdientu.utils.ApiErrorResponse;
import com.example.lkdientu.utils.ApiResponse;
import com.example.lkdientu.models.Product;
import com.example.lkdientu.models.ProductCatalog;
import com.example.lkdientu.utils.ApiResponses;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductController {
    private String API_URL = "http://127.0.0.1:3000/api/v2/products/?limit=100";
    private boolean isThem = false;

    @FXML
    private AnchorPane ancNhapLieu;

    @FXML
    private AnchorPane ancLoc;

    @FXML
    private AnchorPane ancProductView;

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
    private Button btnLuu;

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
    private Label lblHideLoc;

    @FXML
    private TextField txtID;

    @FXML
    private TextField txtIDLoc;

    @FXML
    private TextField txtMaxPriceLoc;

    @FXML
    private TextField txtMaxQuantityLoc;

    @FXML
    private TextField txtMaxSaleLoc;

    @FXML
    private TextField txtMinPriceLoc;

    @FXML
    private TextField txtMinQuantityLoc;

    @FXML
    private TextField txtMinSaleLoc;

    @FXML
    private TextField txtPrice;

    @FXML
    private TextField txtSale;

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
    private CheckBox chkHideLoc;

    @FXML
    private CheckBox chkHideActive;

    @FXML
    private ComboBox<String> cmbProductCatalog;

    @FXML
    private ComboBox<String> cmbProductCatalogLoc;

    private ObservableList<Product> productList = FXCollections.observableArrayList();
    private ObservableList<ProductCatalog> productCatalogList = FXCollections.observableArrayList();
    private ObservableList<String> productCatalogNameList = FXCollections.observableArrayList();
    private Map<String, ProductCatalog> catalogMap = new HashMap<>();

    @FXML
    public void initialize() {
        System.out.println("ProductController loaded");

        // Initialize ValueFactory for Spinner
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 0);
        spnQuantity.setValueFactory(valueFactory);

        cmbProductCatalog.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ProductCatalog selectedCatalog = catalogMap.get(newValue);
                if (selectedCatalog != null) {
                    System.out.println("Selected Catalog: " + selectedCatalog);
                }
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
        fetchProducts(API_URL);

        // Fill combobox
        fetchProductCatalogs(cmbProductCatalog);

        // Search bar
        setupAutoCompleteTextField(txtTimKiem);

        showNhapLieuPane();

        // Checkbox Loc Active
        chkHideActive.selectedProperty().addListener((observable, oldValue, newValue) -> {
            chkHideLoc.setDisable(!newValue);
            lblHideLoc.setDisable(!newValue);
        });

        // Thêm listener cho TableView
        tableProduct.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateFields(newValue); // Gọi phương thức để đổ dữ liệu vào các trường
                btnSua.setDisable(false);
                btnXoa.setDisable(false);
            }
        });
    }

    public void fetchProducts(String ApiUrl) {
        try {
            // Tạo HttpClient
            HttpClient client = HttpClient.newHttpClient();

            // Tạo HttpRequest
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiUrl)) // URL của API
                    .GET()
                    .build();

            // Gửi request và nhận response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Kiểm tra trạng thái
            if (response.statusCode() == 200) {
                // Parse JSON thành ApiResponse
                ObjectMapper objectMapper = new ObjectMapper();
//                ApiResponse apiResponse = objectMapper.readValue(response.body(), ApiResponse.class);

                // Ánh xạ phản hồi
                ApiResponses<ApiResponses.ProductListData> apiResponse = objectMapper.readValue(
                        response.body(),
                        new TypeReference<>() {}
                );

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

    public void fetchProductCatalogs(ComboBox<String> comboBox) {
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
//                ApiResponse apiResponse = objectMapper.readValue(response.body(), ApiResponse.class);

                // Ánh xạ phản hồi
                ApiResponses<ApiResponses.ProductCatalogListData> apiResponse = objectMapper.readValue(
                        response.body(),
                        new TypeReference<>() {}
                );

                // Lấy danh sách ProductCatalog từ ApiResponse
                List<ProductCatalog> catalogs = apiResponse.getData().getProductCatalogs();

                // Cập nhật productCatalogList
                productCatalogList.clear();
                productCatalogNameList.clear();
                catalogMap.clear();

                for (ProductCatalog catalog : catalogs) {
                    productCatalogList.add(catalog);
                    // Nếu không có childs, thêm tên parent
                    if (catalog.getChilds().isEmpty()) {
                        String displayName = catalog.getProductCatalogName();
                        productCatalogNameList.add(displayName);
                        catalogMap.put(displayName, catalog);
                    } else {
                        // Nếu có childs, thêm tên parent - child
                        for (ProductCatalog child : catalog.getChilds()) {
                            productCatalogList.add(child);
                            String displayName = catalog.getProductCatalogName() + " - " + child.getProductCatalogName();
                            productCatalogNameList.add(displayName);
                            catalogMap.put(displayName, child);
                        }
                    }
                    System.out.println(productCatalogList);
                }

                // Gắn danh sách vào ComboBox
                comboBox.setItems(productCatalogNameList);
            } else {
                System.out.println("Lỗi API: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> fetchProductNames() {
        String apiUrl = "http://127.0.0.1:3000/api/v2/products/?limit=100";
        List<String> productNames = new ArrayList<>();

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper();
                ApiResponses<ApiResponses.ProductListData> apiResponse = objectMapper.readValue(
                        response.body(),
                        new TypeReference<>() {}
                );

                // Lấy tên sản phẩm từ danh sách sản phẩm
                List<Product> products = apiResponse.getData().getProducts();
                for (Product product : products) {
                    productNames.add(product.getProductName());
                }
            } else {
                System.out.println("Lỗi khi gọi API: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return productNames;
    }

    public void setupAutoCompleteTextField(TextField textField) {
        // Gọi API để lấy danh sách sản phẩm
        List<String> productNames = fetchProductNames();

        // Tạo ContextMenu cho danh sách gợi ý
        ContextMenu contextMenu = new ContextMenu();

        // Hiển thị danh sách gợi ý khi người dùng nhập
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                contextMenu.hide();
            } else {
                // Lọc danh sách gợi ý
                List<String> filteredSuggestions = productNames.stream()
                        .filter(item -> item.toLowerCase().contains(newValue.toLowerCase()))
                        .toList();

                if (filteredSuggestions.isEmpty()) {
                    contextMenu.hide();
                } else {
                    // Cập nhật các mục trong ContextMenu
                    contextMenu.getItems().clear();
                    for (String suggestion : filteredSuggestions) {
                        MenuItem item = new MenuItem(suggestion);
                        item.setOnAction(event -> {
                            textField.setText(suggestion);
                            contextMenu.hide();
                        });
                        contextMenu.getItems().add(item);
                    }

                    // Hiển thị ContextMenu
                    if (!contextMenu.isShowing()) {
                        // Lấy vị trí của TextField
                        Bounds boundsInScreen = textField.localToScreen(textField.getBoundsInLocal());
                        double x = boundsInScreen.getMinX(); // Tọa độ X (bên trái TextField)
                        double y = boundsInScreen.getMaxY();

                        // Hiển thị ContextMenu tại vị trí chính xác
                        contextMenu.show(textField, x, y);
                    }
                }
            }
        });

        // Đóng ContextMenu khi nhấn ESC
        textField.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE -> contextMenu.hide();
            }
        });
    }


        public void postProduct(boolean isThem) {
            String apiUrl = "http://127.0.0.1:3000/api/v2/products/";

            try {
                // Thu thập dữ liệu từ giao diện
                int productID = txtID.getText() == null ? 0 : Integer.parseInt(txtID.getText());
                String productName = txtProductName.getText();
                String describeProduct = txtProductDescription.getText();
                String productInformation = txtProductInformation.getText();
                double price = txtPrice.getText().isEmpty() ? 0.0 : Double.parseDouble(txtPrice.getText());
                double sale = txtSale.getText().isEmpty() ? 0.0 : Double.parseDouble(txtSale.getText());
                int quantity = spnQuantity.getValue();
                int hideValue = chkHide.isSelected() ? 0 : 1;
                String image = null;

                // Lấy ProductCatalog từ ComboBox
                String selectedCatalogName = cmbProductCatalog.getValue();
                ProductCatalog selectedCatalog = catalogMap.get(selectedCatalogName);

                int productCatalogID = selectedCatalog.getProductCatalogID();

                if (!isThem) {
                    apiUrl += productID;
                }

                // Tạo đối tượng JSON chỉ chứa thông tin cần gửi
                String requestBody = String.format(
                        "{" +
                                "\"productCatalogID\": %d," +
                                "\"productName\": \"%s\"," +
                                "\"describeProduct\": \"%s\"," +
                                "\"image\": \"%s\"," +
                                "\"productInformation\": \"%s\"," +
                                "\"quantity\": %d," +
                                "\"price\": %.2f," +
                                "\"sale\": %.2f," +
                                "\"hide\": %d" +
                                "}",
                        productCatalogID, productName, describeProduct, image, productInformation, quantity, price, sale, hideValue
                );

                // Xác định phương thức HTTP
                HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl))
                        .header("Content-Type", "application/json")
                        .method(isThem ? "POST" : "PATCH", HttpRequest.BodyPublishers.ofString(requestBody));

                // Gửi request và nhận response
                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());

                // Parse JSON thành ApiResponse
                ObjectMapper objectMapper = new ObjectMapper();
                // ApiResponse apiResponse = objectMapper.readValue(response.body(), ApiResponse.class);

            if ((response.statusCode() == 201 && isThem) || (response.statusCode() == 200 && !isThem)) {
                // Ánh xạ phản hồi
                ApiResponses<ApiResponses.ProductData> apiResponse = objectMapper.readValue(
                        response.body(),
                        new TypeReference<>() {}
                );

                // Lấy sản phẩm từ ApiResponse
                Product product = apiResponse.getData().getProduct();

                String action = isThem ? "Thêm" : "Cập nhật";
                System.out.printf("%s sản phẩm thành công!%n", action);

                // Hiển thị thông báo thành công
                String contentText = String.format(
                        "Sản phẩm được %s:\n" +
                                "ID: %d\n" +
                                "Tên sản phẩm: %s\n" +
                                "Mô tả: %s\n" +
                                "Thông tin sản phẩm: %s\n" +
                                "Hình ảnh: %s\n" +
                                "Giá: %.2f\n" +
                                "Giảm giá: %.2f%%\n" +
                                "Số lượng: %d\n" +
                                "Hiển thị: %s\n" +
                                "Danh mục: %s",
                        action.toLowerCase(),
                        product.getProductID(),
                        product.getProductName(),
                        product.getDescribeProduct(),
                        product.getProductInformation(),
                        product.getImage() != null && !product.getImage().isEmpty() ? product.getImage() : "Không có",
                        product.getPrice(),
                        product.getSale(),
                        product.getQuantity(),
                        product.isHide() ? "Không" : "Có",
                        product.getProductCatalog() != null ? product.getProductCatalog().getProductCatalogName() : "N/A"
                );

                showAlert(Alert.AlertType.INFORMATION, "Thông báo", action + " sản phẩm thành công!", contentText);

            } else {
                // Xử lý lỗi từ API
                String responseBody = response.body();
                ApiErrorResponse errorResponse = objectMapper.readValue(responseBody, ApiErrorResponse.class);

                // Hiển thị thông báo lỗi
                showAlert(
                        Alert.AlertType.ERROR,
                        "Lỗi từ API " + errorResponse.getError().getStatusCode(),
                        "Không thể thêm/cập nhật sản phẩm",
                        errorResponse.getMessage()
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteProduct() {
        int productID = txtID.getText() == null ? 0 : Integer.parseInt(txtID.getText());
        // Hiển thị thông báo xác nhận
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText("Bạn có chắc chắn muốn xóa sản phẩm này?");
        confirmAlert.setContentText("Sản phẩm với ID: " + productID);

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            String apiUrl = "http://127.0.0.1:3000/api/v2/products/" + productID;

            try {
                // Tạo HttpRequest với phương thức DELETE
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl))
                        .DELETE()
                        .build();

                // Tạo HttpClient và gửi yêu cầu
                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 204) {
                    // Hiển thị thông báo thành công
                    String contentText = "Sản phầm với ID " + productID + " đã được xóa.";
                    showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Xóa sản phẩm thành công!", contentText);
                } else {
                    showAlert(
                            Alert.AlertType.ERROR,
                            "Lỗi từ API",
                            "Xóa sản phẩm thất bại",
                            "Không thể xóa sản phẩm do thông tin sản phẩm đã tồn tại ở nơi khác"
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Đã xảy ra lỗi khi xóa sản phẩm", e.getMessage());
            }
        } else {
            System.out.println("Hủy xóa sản phẩm với ID: " + productID);
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }


    private void populateFields(Product selectedProduct) {
        // Đổ dữ liệu vào TextField
        txtID.setText(String.valueOf(selectedProduct.getProductID()));
        txtProductName.setText(selectedProduct.getProductName());
        txtProductDescription.setText(selectedProduct.getDescribeProduct());
        txtProductInformation.setText(selectedProduct.getProductInformation());
        txtPrice.setText(String.valueOf(selectedProduct.getPrice()));
        txtSale.setText(String.valueOf(selectedProduct.getSale()));

        // Đổ dữ liệu vào Spinner
        spnQuantity.getValueFactory().setValue(selectedProduct.getQuantity());

        // Đổ dữ liệu vào CheckBox
        chkHide.setSelected(!selectedProduct.isHide());

        // Đổ dữ liệu vào ComboBox
        if (selectedProduct.getProductCatalog() != null) {
            ProductCatalog catalog = selectedProduct.getProductCatalog();

            for (Map.Entry<String, ProductCatalog> entry : catalogMap.entrySet()) {
                if (entry.getValue().getProductCatalogID() == catalog.getProductCatalogID()) {
                    cmbProductCatalog.setValue(entry.getKey());
                    break;
                }
            }
        } else {
            cmbProductCatalog.setValue(null);
        }
    }

    private boolean validateInputs() {
        StringBuilder errorMessage = new StringBuilder();

        // Kiểm tra Product Name
        if (txtProductName.getText() == null || txtProductName.getText().trim().isEmpty()) {
            errorMessage.append("Tên sản phẩm không được để trống.\n");
        }

        // Kiểm tra Product Description
        if (txtProductDescription.getText() == null || txtProductDescription.getText().trim().isEmpty()) {
            errorMessage.append("Mô tả sản phẩm không được để trống.\n");
        }

        // Kiểm tra Product Information
        if (txtProductInformation.getText() == null || txtProductInformation.getText().trim().isEmpty()) {
            errorMessage.append("Thông tin sản phẩm không được để trống.\n");
        }

        // Kiểm tra Price
        if (txtPrice.getText() == null || txtPrice.getText().trim().isEmpty()) {
            errorMessage.append("Giá sản phẩm không được để trống.\n");
        } else {
            try {
                double price = Double.parseDouble(txtPrice.getText());
                if (price < 0) {
                    errorMessage.append("Giá sản phẩm phải lớn hơn hoặc bằng 0.\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("Giá sản phẩm phải là số hợp lệ.\n");
            }
        }

        // Kiểm tra Sale
        if (txtSale.getText() == null || txtSale.getText().trim().isEmpty()) {
            errorMessage.append("Phần trăm giảm giá không được để trống.\n");
        } else {
            try {
                double sale = Double.parseDouble(txtSale.getText());
                if (sale < 0 || sale > 100) {
                    errorMessage.append("Phần trăm giảm giá phải nằm trong khoảng từ 0 đến 100.\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("Phần trăm giảm giá phải là số nguyên hợp lệ.\n");
            }
        }

        // Kiểm tra Product Catalog
        if (cmbProductCatalog.getValue() == null || cmbProductCatalog.getValue().trim().isEmpty()) {
            errorMessage.append("Danh mục sản phẩm không được để trống.\n");
        }

        // Nếu có lỗi, hiển thị và trả về false
        if (errorMessage.length() > 0) {
            System.out.println("Lỗi nhập liệu:\n" + errorMessage);
            // Hiển thị thông báo lỗi cho người dùng (nếu cần)
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi nhập liệu");
            alert.setHeaderText("Vui lòng kiểm tra lại thông tin");
            alert.setContentText(errorMessage.toString());
            alert.showAndWait();
            return false;
        }

        // Nếu tất cả dữ liệu hợp lệ
        return true;
    }

    public String encodeUrlParam(String param) {
        return URLEncoder.encode(param, StandardCharsets.UTF_8);
    }

    // Event listener: btnThem
    @FXML
    void handleBtnThem(ActionEvent event) {
        isThem = true;

        // Create an empty product row
        Product newProduct = new Product();

        // Thêm sản phẩm vào TableView
        productList.add(newProduct);

        // Focus vào dòng mới
        tableProduct.getSelectionModel().select(newProduct);
        tableProduct.scrollTo(newProduct);

        // Toggle Button Visibility
        btnThem.setDisable(true);
        btnSua.setDisable(true);
        btnXoa.setDisable(true);
        btnRefresh.setDisable(true);
        btnUndo.setDisable(false);
        btnLuu.setDisable(false);
        txtTimKiem.setDisable(true);
        btnTimKiem.setDisable(true);
        btnLoc.setDisable(true);
        tableProduct.setDisable(true);
        ancNhapLieu.setDisable(false);
        ancProductView.requestFocus();

        // Clear inputs
        txtID.setText(null);
        txtProductName.setText(null);
        txtProductDescription.setText(null);
        txtPrice.setText(null);
        txtSale.setText(null);
        txtProductInformation.setText(null);
        txtProductDescription.setText(null);
        spnQuantity.getValueFactory().setValue(0);
        chkHide.setSelected(false);
        cmbProductCatalog.setValue(null);
    }

    @FXML
    void handleBtnSua(ActionEvent event) {
        isThem = false;

        // Toggle Button Visibility
        btnThem.setDisable(true);
        btnSua.setDisable(true);
        btnXoa.setDisable(true);
        btnRefresh.setDisable(true);
        btnUndo.setDisable(false);
        btnLuu.setDisable(false);
        txtTimKiem.setDisable(true);
        btnTimKiem.setDisable(true);
        btnLoc.setDisable(true);
        tableProduct.setDisable(true);
        ancNhapLieu.setDisable(false);
        ancProductView.requestFocus();
    }

    @FXML
    void handleBtnXoa(ActionEvent event) {
        deleteProduct();
        handleRefresh();
        fetchProducts(API_URL);
    }

    @FXML
    void handleBtnUndo(ActionEvent actionEvent) {
        isThem = false;

        // Xóa dòng đang được chọn (nếu là dòng mới)
        Product selectedProduct = tableProduct.getSelectionModel().getSelectedItem();
        if (selectedProduct != null && selectedProduct.getProductID() == 0) { // Kiểm tra nếu ID = 0 là dòng mới
            productList.remove(selectedProduct);
        }

        // Hủy focus
        tableProduct.getSelectionModel().clearSelection();

        // Toggle Button Visibility
        btnThem.setDisable(false);
        btnSua.setDisable(true);
        btnXoa.setDisable(true);
        btnRefresh.setDisable(false);
        btnUndo.setDisable(true);
        btnLuu.setDisable(true);
        txtTimKiem.setDisable(false);
        btnTimKiem.setDisable(false);
        btnLoc.setDisable(false);

        tableProduct.setDisable(false);
        ancNhapLieu.setDisable(true);
        ancProductView.requestFocus();

        // Clear inputs
        txtID.setText(null);
        txtProductName.setText(null);
        txtProductDescription.setText(null);
        txtPrice.setText(null);
        txtSale.setText(null);
        txtProductInformation.setText(null);
        txtProductDescription.setText(null);
        spnQuantity.getValueFactory().setValue(0);
        chkHide.setSelected(false);
        cmbProductCatalog.setValue(null);
    }

    public void handleRefresh() {
        isThem = false;

        // Toggle Button Visibility
        btnThem.setDisable(false);
        btnSua.setDisable(true);
        btnXoa.setDisable(true);
        btnRefresh.setDisable(false);
        btnUndo.setDisable(true);
        btnLuu.setDisable(true);
        txtTimKiem.setDisable(false);
        btnTimKiem.setDisable(false);
        btnLoc.setDisable(false);

        tableProduct.setDisable(false);
        ancNhapLieu.setDisable(true);
        ancProductView.requestFocus();

        // Clear inputs
        txtID.setText(null);
        txtProductName.setText(null);
        txtProductDescription.setText(null);
        txtPrice.setText(null);
        txtSale.setText(null);
        txtProductInformation.setText(null);
        txtProductDescription.setText(null);
        spnQuantity.getValueFactory().setValue(0);
        chkHide.setSelected(false);
        cmbProductCatalog.setValue(null);
    }

    @FXML
    void handleBtnRefresh(ActionEvent event) {
        handleRefresh();
        fetchProducts(API_URL);
    }

    @FXML
    void handleBtnLuu(ActionEvent actionEvent) {
        if (validateInputs()) {
            postProduct(isThem);
            isThem = false;
            handleRefresh();
            fetchProducts(API_URL);
        }
    }

    @FXML
    void handleBtnTimKiem(ActionEvent actionEvent) {
        if (txtTimKiem.getText().isEmpty()) {
            fetchProducts(API_URL);
        } else {
            fetchProducts(API_URL + "&productName=" + encodeUrlParam(txtTimKiem.getText()));
        }
    }

    @FXML
    void handleBtnLoc(ActionEvent actionEvent) {
        // Toggle Button Visibility
        btnThem.setDisable(true);
        btnSua.setDisable(true);
        btnXoa.setDisable(true);
        btnRefresh.setDisable(true);
        btnUndo.setDisable(true);
        btnLuu.setDisable(true);
        txtTimKiem.setDisable(true);
        btnTimKiem.setDisable(true);
        btnLoc.setDisable(true);

        tableProduct.setDisable(true);
        ancProductView.requestFocus();
        showLocPane();
    }

    @FXML
    void handleBtnHuyLoc(ActionEvent actionEvent) {
        // Toggle Button Visibility
        btnThem.setDisable(false);
        btnSua.setDisable(true);
        btnXoa.setDisable(true);
        btnRefresh.setDisable(false);
        btnUndo.setDisable(true);
        btnLuu.setDisable(true);
        txtTimKiem.setDisable(false);
        btnTimKiem.setDisable(false);
        btnLoc.setDisable(false);

        tableProduct.setDisable(false);
        ancNhapLieu.setDisable(true);
        ancProductView.requestFocus();
        showNhapLieuPane();
    }

    @FXML
    void handleBtnXacNhanLoc(ActionEvent actionEvent) {
        String filterUrl = API_URL;
        if (hasValueTextField(txtIDLoc)) {
            filterUrl += "&productID=" + txtIDLoc.getText();
        }
        if (cmbProductCatalogLoc.getValue() != null) {
            String selectedCatalogName = cmbProductCatalogLoc.getValue();
            ProductCatalog selectedCatalog = catalogMap.get(selectedCatalogName);
            int productCatalogID = selectedCatalog.getProductCatalogID();
            filterUrl += "&productCatalogID=" + productCatalogID;
        }
        if (hasValueTextField(txtMinPriceLoc)) {
            filterUrl += "&price[gte]=" + txtMinPriceLoc.getText();
        }
        if (hasValueTextField(txtMaxPriceLoc)) {
            filterUrl += "&price[lte]=" + txtMaxPriceLoc.getText();
        }
        if (hasValueTextField(txtMinQuantityLoc)) {
            filterUrl += "&quantity[gte]=" + txtMinQuantityLoc.getText();
        }
        if (hasValueTextField(txtMaxQuantityLoc)) {
            filterUrl += "&quantity[lte]=" + txtMaxQuantityLoc.getText();
        }
        if (hasValueTextField(txtMinSaleLoc)) {
            filterUrl += "&sale[gte]=" + txtMinSaleLoc.getText();
        }
        if (hasValueTextField(txtMaxSaleLoc)) {
            filterUrl += "&sale[lte]=" + txtMaxSaleLoc.getText();
        }
        if (chkHideActive.isSelected()) {
            filterUrl += "&hide=" + (chkHideLoc.isSelected() ? 0 : 1);
        }
        System.out.println(filterUrl);
        handleRefresh();
        handleRefreshLoc();
        showNhapLieuPane();
        fetchProducts(filterUrl);
    }

    @FXML
    void handleResetComboBox(ActionEvent actionEvent) {
        cmbProductCatalogLoc.setValue(null);
        cmbProductCatalogLoc.setPromptText("Chọn danh mục");
    }

    private void handleRefreshLoc() {
        txtIDLoc.setText(null);
        txtMinPriceLoc.setText(null);
        txtMaxPriceLoc.setText(null);
        txtMinSaleLoc.setText(null);
        txtMaxSaleLoc.setText(null);
        txtMinQuantityLoc.setText(null);
        txtMaxQuantityLoc.setText(null);
        cmbProductCatalogLoc.setValue(null);
        chkHideActive.setSelected(false);
        chkHideLoc.setSelected(false);
    }

    private void showLocPane() {
        ancNhapLieu.setVisible(false);
        ancLoc.setVisible(true);
        fetchProductCatalogs(cmbProductCatalogLoc);
    }

    private void showNhapLieuPane() {
        ancLoc.setVisible(false);
        ancNhapLieu.setVisible(true);
    }

    private boolean hasValueTextField(TextField textField) {
        return textField.getText() != null && !textField.getText().trim().isEmpty();
    }
}
