package com.example.lkdientu.controllers;

import com.example.lkdientu.SessionManager;
import com.example.lkdientu.models.Product;
import com.example.lkdientu.models.ProductCatalog;
import com.example.lkdientu.utils.ApiErrorResponse;
import com.example.lkdientu.utils.ApiResponses;
import com.example.lkdientu.utils.AlertUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import static com.example.lkdientu.utils.AlertUtils.showAlert;
import static com.example.lkdientu.utils.UrlUtils.encodeUrlParam;

public class ProductCatalogController {
    String token = SessionManager.getInstance().getToken();
    private String API_URL = "http://127.0.0.1:3000/api/v1/productCatalogs/?limit=100";
    private boolean isThem = false;

    @FXML
    private AnchorPane ancLoc;

    @FXML
    private AnchorPane ancNhapLieu;

    @FXML
    private AnchorPane ancProductCatalogView;

    @FXML
    private Button btnHuyLoc;

    @FXML
    private Button btnLoc;

    @FXML
    private Button btnLuu;

    @FXML
    private Button btnRefresh;

    @FXML
    private Button btnResetCmbProductCatalog;

    @FXML
    private Button btnSua;

    @FXML
    private Button btnThem;

    @FXML
    private Button btnTimKiem;

    @FXML
    private Button btnUndo;

    @FXML
    private Button btnXacNhanLoc;

    @FXML
    private Button btnXoa;

    @FXML
    private Button btnResetCmbProductCatalogParent;

    @FXML
    private CheckBox chkHideActive;

    @FXML
    private CheckBox chkHideLoc;

    @FXML
    private ComboBox<String> cmbProductCatalogParent;

    @FXML
    private ComboBox<String> cmbProductCatalogLoc;

    @FXML
    private TableColumn<ProductCatalog, Integer> colChildCount;

    @FXML
    private TableColumn<ProductCatalog, Integer> colProductCatalogID;

    @FXML
    private TableColumn<ProductCatalog, String> colProductCatalogName;

    @FXML
    private TableColumn<ProductCatalog, String> colRelation;

    @FXML
    private Label lblHideLoc;

    @FXML
    private TableView<ProductCatalog> tableProductCatalog;

    @FXML
    private TextArea txtChilds;

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
    private TextField txtProductCatalogName;

    @FXML
    private TextField txtTimKiem;

    private ObservableList<ProductCatalog> productCatalogList = FXCollections.observableArrayList();
    private ObservableList<String> productCatalogNameList = FXCollections.observableArrayList();
//    private Map<String, ProductCatalog> catalogParentMap = new HashMap<>();
    private Map<String, List<ProductCatalog>> catalogParentMap = new HashMap<>();
    private Map<String, ProductCatalog> catalogMap = new HashMap<>();

    @FXML
    public void initialize() {
        String token = SessionManager.getInstance().getToken();
        System.out.println("ProductCatalogController loaded");

        // Liên kết các cột với thuộc tính trong ProductCatalog
        colProductCatalogID.setCellValueFactory(new PropertyValueFactory<>("productCatalogID"));
        colProductCatalogName.setCellValueFactory(new PropertyValueFactory<>("productCatalogName"));
        colRelation.setCellValueFactory(cellData -> {
            ProductCatalog catalog = cellData.getValue();
            if (catalog.getParent() != null) {
                return new SimpleStringProperty(catalog.getParent().getProductCatalogName() + " - " + catalog.getProductCatalogName());
            }
            return new SimpleStringProperty(catalog.getProductCatalogName());
        });
        colChildCount.setCellValueFactory(cellData -> {
            List<ProductCatalog> childs = cellData.getValue().getChilds();
            return new SimpleIntegerProperty((childs != null) ? childs.size() : 0).asObject();
        });

        tableProductCatalog.setItems(productCatalogList);

        // Fill table
        fetchProductCatalogs(API_URL);

        // Fill combobox
        cmbProductCatalogParent.setItems(productCatalogNameList);
        cmbProductCatalogLoc.setItems(productCatalogNameList);

        // Thêm listener cho TableView
        tableProductCatalog.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateFields(newValue); // Gọi phương thức để đổ dữ liệu vào các trường
                btnSua.setDisable(false);
                btnXoa.setDisable(false);
            }
//            System.out.println(newValue);
        });

        // Search bar
        setupAutoCompleteTextField(txtTimKiem);

        // Test combobox
//        cmbProductCatalogParent.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue != null) {
//                System.out.println(newValue);
//                // Lấy danh sách các danh mục tương ứng từ catalogParentMap
//                List<ProductCatalog> selectedCatalogs = catalogParentMap.get(newValue);
//
//                if (selectedCatalogs != null && !selectedCatalogs.isEmpty()) {
//                    // Hiển thị key (newValue) của catalog được chọn
//                    System.out.println("Key: " + newValue);
//
//                    // Hiển thị danh sách các catalog tương ứng
//                    System.out.println("Catalogs associated with this key:");
//                    for (ProductCatalog catalog : selectedCatalogs) {
//                        System.out.println("ID: " + catalog.getProductCatalogID() +
//                                ", Name: " + catalog.getProductCatalogName() +
//                                ", Parent: " + (catalog.getParent() != null ? catalog.getParent().getProductCatalogName() : "None"));
//                    }
//                } else {
//                    System.out.println("Key: " + newValue + " has no associated catalogs.");
//                }
//            }
//        });

    }

    public void fetchProductCatalogs(String ApiUrl) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiUrl))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper();
                ApiResponses<ApiResponses.ProductCatalogListData> apiResponse = objectMapper.readValue(
                        response.body(),
                        new TypeReference<>() {}
                );

                List<ProductCatalog> catalogs = apiResponse.getData().getProductCatalogs();

                // Gán cha cho danh mục con
                ObservableList<ProductCatalog> processedCatalogs = FXCollections.observableArrayList();

//                productCatalogList.clear();
                productCatalogNameList.clear();
                catalogParentMap.clear();
                catalogMap.clear();

                for (ProductCatalog parentCatalog : catalogs) {
                    // Đảm bảo danh sách childs không null
                    if (parentCatalog.getChilds() == null) {
                        parentCatalog.setChilds(new ArrayList<>());
                    }

                    String displayName = parentCatalog.getProductCatalogName();
                    // Only add catalog which has no childs to cmbProductCatalogParent
                    productCatalogNameList.add(displayName);
//                    catalogParentMap.put(displayName, parentCatalog);
                    catalogParentMap.computeIfAbsent(parentCatalog.getProductCatalogName(), k -> new ArrayList<>())
                            .add(parentCatalog);
                    catalogMap.put(displayName, parentCatalog);

                    // Thêm danh mục cha vào danh sách
                    processedCatalogs.add(parentCatalog);

                    // Duyệt qua danh mục con và gán quan hệ cha
                    for (ProductCatalog childCatalog : parentCatalog.getChilds()) {
                        childCatalog.setParent(parentCatalog); // Gán parent cho danh mục con
                        processedCatalogs.add(childCatalog); // Thêm danh mục con vào danh sách
//                        catalogParentMap.put(displayName, childCatalog);
                        catalogParentMap.computeIfAbsent(childCatalog.getProductCatalogName(), k -> new ArrayList<>())
                                .add(childCatalog);
                    }
                }

                productCatalogList.setAll(processedCatalogs);
            } else {
                System.out.println("Lỗi khi gọi API: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fetchProductCatalogsToFillTableOnly(String ApiUrl) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiUrl))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper();
                ApiResponses<ApiResponses.ProductCatalogListData> apiResponse = objectMapper.readValue(
                        response.body(),
                        new TypeReference<>() {}
                );

                List<ProductCatalog> catalogs = apiResponse.getData().getProductCatalogs();

                Set<ProductCatalog> processedCatalogsSet = new HashSet<>();


                for (ProductCatalog parentCatalog : catalogs) {
                    // Đảm bảo danh sách childs không null
                    if (parentCatalog.getChilds() == null) {
                        parentCatalog.setChilds(new ArrayList<>());
                    }

                    // Thêm danh mục cha vào danh sách
                    processedCatalogsSet.add(parentCatalog);

                    // Duyệt qua danh mục con và gán quan hệ cha
                    for (ProductCatalog childCatalog : parentCatalog.getChilds()) {
                        childCatalog.setParent(parentCatalog); // Gán parent cho danh mục con
                        processedCatalogsSet.add(childCatalog); // Thêm danh mục con vào danh sách
                    }
                }
                // Gán cha cho danh mục con
                ObservableList<ProductCatalog> processedCatalogs = FXCollections.observableArrayList(processedCatalogsSet);
                productCatalogList.setAll(processedCatalogs);
                System.out.println(processedCatalogs);
            } else {
                System.out.println("Lỗi khi gọi API: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateFields(ProductCatalog selectedProductCatalog) {
        // Đổ dữ liệu vào TextField
        txtID.setText(String.valueOf(selectedProductCatalog.getProductCatalogID()));
        txtProductCatalogName.setText(selectedProductCatalog.getProductCatalogName());

        // Đổ dữ liệu vào ComboBox
        ProductCatalog catalog = selectedProductCatalog;

//        for (Map.Entry<String, ProductCatalog> entry : catalogParentMap.entrySet()) {
//            if (entry.getValue().getProductCatalogID() == catalog.getProductCatalogID()) {
//                cmbProductCatalogParent.setValue(entry.getKey());
//                break;
//            }
//        }

        if (catalog.getParent() != null) {
            txtChilds.setText(null);
            String parentName = catalog.getParent().getProductCatalogName();

            // Tìm danh mục cha trong catalogParentMap
            List<ProductCatalog> parentCatalogs = catalogParentMap.get(parentName);
            if (parentCatalogs != null) {
                for (ProductCatalog parentCatalog : parentCatalogs) {
                    if (parentCatalog.getProductCatalogID() == catalog.getParent().getProductCatalogID()) {
                        cmbProductCatalogParent.setValue(parentName);
                        break;
                    }
                }
            }
        } else {
            // Nếu không có danh mục cha, đặt ComboBox về null
            cmbProductCatalogParent.setValue(null);
            if (catalog.getChilds() != null) {
                String childName = "";
                for (ProductCatalog childCatalog : catalog.getChilds()) {
                    childName += "- " + childCatalog.getProductCatalogName() + "\n";
                }
                txtChilds.setText(childName);
            }
        }
    }

    public void postProduct(boolean isThem) {
        String apiUrl = "http://127.0.0.1:3000/api/v1/productCatalogs/";

        try {
            // Thu thập dữ liệu từ giao diện
            int productCatalogID = txtID.getText() == null ? 0 : Integer.parseInt(txtID.getText());
            String productCatalogName = txtProductCatalogName.getText();

//            // Lấy ProductCatalogParent từ ComboBox
//            String selectedParentCatalogName = cmbProductCatalogParent.getValue();
//            ProductCatalog selectedCatalog = catalogMap.get(selectedParentCatalogName);
//            int productCatalogParentID = selectedCatalog.getProductCatalogID();
//
//
//            if (!isThem) {
//                apiUrl += productCatalogID;
//            }
//
//            // Tạo đối tượng JSON chỉ chứa thông tin cần gửi
//            String requestBody = String.format(
//                    "{" +
//                            "\"productCatalogName\": \"%s\"," +
//                            "\"parentID\": \"%d\"," +
//                            "}",
//                    productCatalogName, selectedParentCatalogName != null ? productCatalogParentID : -1
//            );

            // Lấy ProductCatalogParent từ ComboBox
            String selectedParentCatalogName = cmbProductCatalogParent.getValue();
            Integer productCatalogParentID = null;

            if (selectedParentCatalogName != null) {
                ProductCatalog selectedCatalogs = catalogMap.get(selectedParentCatalogName);
                if (selectedCatalogs != null) {
                    productCatalogParentID = selectedCatalogs.getProductCatalogID();
                }
            }

            if (!isThem) {
                apiUrl += productCatalogID;
            }

            // Tạo đối tượng JSON chỉ chứa thông tin cần gửi
            String requestBody = String.format(
                    "{" +
                            "\"productCatalogName\": \"%s\"," +
                            "\"parentID\": %s" + // Sử dụng %s để cho phép giá trị null
                            "}",
                    productCatalogName,
                    productCatalogParentID != null ? productCatalogParentID.toString() : "null" // Kiểm tra null để gán giá trị
            );

            // Xác định phương thức HTTP
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .method(isThem ? "POST" : "PATCH", HttpRequest.BodyPublishers.ofString(requestBody));

            // Gửi request và nhận response
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());

            // Parse JSON thành ApiResponse
            ObjectMapper objectMapper = new ObjectMapper();
            // ApiResponse apiResponse = objectMapper.readValue(response.body(), ApiResponse.class);

            if ((response.statusCode() == 201 && isThem) || (response.statusCode() == 200 && !isThem)) {
                // Ánh xạ phản hồi
                ApiResponses<ApiResponses.ProductCatalogData> apiResponse = objectMapper.readValue(
                        response.body(),
                        new TypeReference<>() {
                        }
                );

                // Lấy danh mục từ ApiResponse
                ProductCatalog productCatalog = apiResponse.getData().getProductCatalog();

                String action = isThem ? "Thêm" : "Cập nhật";
                System.out.printf("%s danh mục thành công!%n", action);

                // Hiển thị thông báo thành công
                String contentText = String.format(
                        "Danh mục được %s:\n" +
                                "ID: %d\n" +
                                "Tên danh mục: %s\n",
                        action.toLowerCase(),
                        productCatalog.getProductCatalogID(),
                        productCatalog.getProductCatalogName()
                );

                showAlert(Alert.AlertType.INFORMATION, "Thông báo", action + " danh mục thành công!", contentText);

            } else {
                // Xử lý lỗi từ API
                String responseBody = response.body();
                ApiErrorResponse errorResponse = objectMapper.readValue(responseBody, ApiErrorResponse.class);

                // Hiển thị thông báo lỗi
                showAlert(
                        Alert.AlertType.ERROR,
                        "Lỗi từ API " + errorResponse.getError().getStatusCode(),
                        "Không thể thêm/cập nhật danh mục",
                        errorResponse.getMessage()
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateInputs() {
        StringBuilder errorMessage = new StringBuilder();

        // Kiểm tra Product Name
        if (txtProductCatalogName.getText() == null || txtProductCatalogName.getText().trim().isEmpty()) {
            errorMessage.append("Tên danh mục không được để trống.\n");
        }

        // Nếu có lỗi, hiển thị và trả về false
        if (errorMessage.length() > 0) {
            System.out.println("Lỗi nhập liệu:\n" + errorMessage);
            // Hiển thị thông báo lỗi cho người dùng (nếu cần)
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Lỗi nhập liệu");
//            alert.setHeaderText("Vui lòng kiểm tra lại thông tin");
//            alert.setContentText(errorMessage.toString());
//            alert.showAndWait();
            showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Vui lòng kiểm tra lại thông tin", errorMessage.toString());
            return false;
        }

        // Nếu tất cả dữ liệu hợp lệ
        return true;
    }

    @FXML
    void handleBtnResetCmbProductCatalogParent(ActionEvent actionEvent) {
        cmbProductCatalogParent.setValue(null);
        cmbProductCatalogParent.setPromptText("Chọn danh mục");
    }

    @FXML
    void handleResetComboBox(ActionEvent event) {
        cmbProductCatalogLoc.setValue(null);
        cmbProductCatalogLoc.setPromptText("Chọn danh mục");
    }

    @FXML
    void handleBtnHuyLoc(ActionEvent event) {
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

        tableProductCatalog.setDisable(false);
        ancNhapLieu.setDisable(true);
        ancProductCatalogView.requestFocus();
        showNhapLieuPane();
    }

    @FXML
    void handleBtnLoc(ActionEvent event) {
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

        tableProductCatalog.setDisable(true);
        ancProductCatalogView.requestFocus();
        showLocPane();
    }

    @FXML
    void handleBtnLuu(ActionEvent event) {
        if (validateInputs()) {
            postProduct(isThem);
            isThem = false;
            handleRefresh();
            fetchProductCatalogs(API_URL);
        }
    }

    @FXML
    void handleBtnRefresh(ActionEvent event) {
        handleRefresh();
        fetchProductCatalogs(API_URL);
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
        tableProductCatalog.setDisable(true);
        ancNhapLieu.setDisable(false);
        ancProductCatalogView.requestFocus();
    }

    @FXML
    void handleBtnThem(ActionEvent event) {
        isThem = true;

        // Create an empty product row
        ProductCatalog newProductCatalog = new ProductCatalog();

        // Thêm danh mục vào TableView
        productCatalogList.add(newProductCatalog);

        // Focus vào dòng mới
        tableProductCatalog.getSelectionModel().select(newProductCatalog);
        tableProductCatalog.scrollTo(newProductCatalog);

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
        tableProductCatalog.setDisable(true);
        ancNhapLieu.setDisable(false);
        ancProductCatalogView.requestFocus();

        // Clear inputs
        txtID.setText(null);
        txtProductCatalogName.setText(null);
        txtChilds.setText(null);
    }

    @FXML
    void handleBtnTimKiem(ActionEvent event) {
        if (txtTimKiem.getText().isEmpty()) {
            fetchProductCatalogs(API_URL);
        } else {
            fetchProductCatalogsToFillTableOnly(API_URL + "&productCatalogName=" + encodeUrlParam(txtTimKiem.getText()));
        }
    }

    @FXML
    void handleBtnUndo(ActionEvent event) {
        isThem = false;

        // Xóa dòng đang được chọn (nếu là dòng mới)
        ProductCatalog selectedProductCatalog = tableProductCatalog.getSelectionModel().getSelectedItem();
        if (selectedProductCatalog != null && selectedProductCatalog.getProductCatalogID() == 0) { // Kiểm tra nếu ID = 0 là dòng mới
            productCatalogList.remove(selectedProductCatalog);
        }

        // Hủy focus
        tableProductCatalog.getSelectionModel().clearSelection();

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

        tableProductCatalog.setDisable(false);
        ancNhapLieu.setDisable(true);
        ancProductCatalogView.requestFocus();

        // Clear inputs
        txtID.setText(null);
        txtProductCatalogName.setText(null);
        txtChilds.setText(null);
        cmbProductCatalogParent.setValue(null);
    }

    @FXML
    void handleBtnXacNhanLoc(ActionEvent event) {
        String filterUrl = API_URL;
        if (cmbProductCatalogLoc.getValue() != null) {
            String selectedCatalogName = cmbProductCatalogLoc.getValue();
            ProductCatalog selectedCatalog = catalogMap.get(selectedCatalogName);
            int productCatalogID = selectedCatalog.getProductCatalogID();
            filterUrl += "&productCatalogID=" + productCatalogID;
        }
        System.out.println(filterUrl);
        handleRefresh();
        handleRefreshLoc();
        showNhapLieuPane();
        fetchProductCatalogsToFillTableOnly(filterUrl);
    }

    @FXML
    void handleBtnXoa(ActionEvent event) {
        deleteProduct();
        handleRefresh();
        fetchProductCatalogs(API_URL);
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

        tableProductCatalog.setDisable(false);
        ancNhapLieu.setDisable(true);
        ancProductCatalogView.requestFocus();

        // Clear inputs
        txtID.setText(null);
        txtProductCatalogName.setText(null);
        txtChilds.setText(null);
        cmbProductCatalogParent.setValue(null);
    }

    private void handleRefreshLoc() {
        cmbProductCatalogLoc.setValue(null);
    }

    public void deleteProduct() {
        int productCatalogID = txtID.getText() == null ? 0 : Integer.parseInt(txtID.getText());
        // Hiển thị thông báo xác nhận
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText("Bạn có chắc chắn muốn xóa danh mục này?");
        confirmAlert.setContentText("Danh mục với ID: " + productCatalogID);

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            String apiUrl = "http://127.0.0.1:3000/api/v1/productCatalogs/" + productCatalogID;

            try {
                // Tạo HttpRequest với phương thức DELETE
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl))
                        .header("Authorization", "Bearer " + token)
                        .DELETE()
                        .build();

                // Tạo HttpClient và gửi yêu cầu
                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 204) {
                    // Hiển thị thông báo thành công
                    String contentText = "Danh mục với ID " + productCatalogID + " đã được xóa.";
                    showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Xóa danh mục thành công!", contentText);
                } else {
                    showAlert(
                            Alert.AlertType.ERROR,
                            "Lỗi từ API",
                            "Xóa danh mục thất bại",
                            "Không thể xóa danh mục do thông tin danh mục đã tồn tại ở nơi khác"
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Đã xảy ra lỗi khi xóa danh mục", e.getMessage());
            }
        } else {
            System.out.println("Hủy xóa danh mục với ID: " + productCatalogID);
        }
    }

    private List<String> fetchProductCatalogNames() {
        String apiUrl = "http://127.0.0.1:3000/api/v1/productCatalogs/?limit=100";
        List<String> productCatalogNames = new ArrayList<>();

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper();
                ApiResponses<ApiResponses.ProductCatalogListData> apiResponse = objectMapper.readValue(
                        response.body(),
                        new TypeReference<>() {}
                );

                // Lấy tên sản phẩm từ danh sách sản phẩm
                List<ProductCatalog> catalogs = apiResponse.getData().getProductCatalogs();

                for (ProductCatalog parentCatalog : catalogs) {
                    productCatalogNames.add(parentCatalog.getProductCatalogName());

                    if (parentCatalog.getChilds() != null) {
                        for (ProductCatalog childCatalog : parentCatalog.getChilds()) {
                            String childName = childCatalog.getProductCatalogName();
                            productCatalogNames.add(childName);
                        }
                    }
                }
            } else {
                System.out.println("Lỗi khi gọi API: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return productCatalogNames;
    }

    public void setupAutoCompleteTextField(TextField textField) {
        // Gọi API để lấy danh sách sản phẩm
        List<String> productCatalogNames = fetchProductCatalogNames();

        // Tạo ContextMenu cho danh sách gợi ý
        ContextMenu contextMenu = new ContextMenu();

        // Hiển thị danh sách gợi ý khi người dùng nhập
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                contextMenu.hide();
            } else {
                // Lọc danh sách gợi ý
                List<String> filteredSuggestions = productCatalogNames.stream()
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

    private void showLocPane() {
        ancNhapLieu.setVisible(false);
        ancLoc.setVisible(true);
    }

    private void showNhapLieuPane() {
        ancLoc.setVisible(false);
        ancNhapLieu.setVisible(true);
    }
}
