package com.example.lkdientu.controllers;

import com.example.lkdientu.SessionManager;
import com.example.lkdientu.models.*;
import com.example.lkdientu.utils.ApiResponses;
import com.example.lkdientu.utils.LocalDateDeserializer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

import static com.example.lkdientu.utils.AlertUtils.showAlert;

public class AccountController {
    String token = SessionManager.getInstance().getToken();
    private String APIAccount_URL = "http://127.0.0.1:3000/api/v1/accounts/?limit=100";
    private String APIInfo_URL = "http://127.0.0.1:3000/api/v1/info/?limit=100";
    private boolean accountMode = true;
    private boolean isThem = false;

    @FXML
    private AnchorPane ancAccountView;

    @FXML
    private AnchorPane ancLoc;

    @FXML
    private AnchorPane ancNhapLieuAccount;

    @FXML
    private AnchorPane ancNhapLieuInfo;

    @FXML
    private Button btnHuyLoc;

    @FXML
    private Button btnLoc;

    @FXML
    private Button btnLuu;

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
    private Button btnXacNhanLoc;

    @FXML
    private Button btnXoa;

    @FXML
    private ToggleButton btnCheDo;

    @FXML
    private ComboBox<String> cmbRole;

    @FXML
    private ComboBox<String> cmbRoleLoc;

    @FXML
    private TableColumn<Account, Integer> colAccountID;

    @FXML
    private TableColumn<Account, String> colAccountName;

    @FXML
    private TableColumn<Account, String> colCIC;

    @FXML
    private TableColumn<Account, String> colMail;

    @FXML
    private TableColumn<Account, String> colRole;

    @FXML
    private TableColumn<Info, String> colCity;

    @FXML
    private TableColumn<Info, String> colDOB;

    @FXML
    private TableColumn<Info, String> colDistrict;

    @FXML
    private TableColumn<Info, String> colFirstName;

    @FXML
    private TableColumn<Info, String> colHouseNumber;

    @FXML
    private TableColumn<Info, Integer> colInfoID;

    @FXML
    private TableColumn<Info, String> colLastName;

    @FXML
    private TableColumn<Info, String> colMiddleName;

    @FXML
    private TableColumn<Info, String> colPhoneNumber;

    @FXML
    private TableColumn<Info, String> colSex;

    @FXML
    private TableColumn<Info, String> colStreet;

    @FXML
    private TableColumn<Info, String> colWard;

    @FXML
    private DatePicker dateDOB;

    @FXML
    private RadioButton rdNam;

    @FXML
    private RadioButton rdNu;

    @FXML
    private ToggleGroup sexGroup;

    @FXML
    private TableView<Account> tableAccount;

    @FXML
    private TableView<Info> tableInfo;

    @FXML
    private TextField txtAccountName;

    @FXML
    private TextField txtAccountNameLoc;

    @FXML
    private TextField txtCIC;

    @FXML
    private TextField txtCICLoc;

    @FXML
    private TextField txtCity;

    @FXML
    private TextField txtDistrict;
    @FXML
    private TextField txtDOB;

    @FXML
    private TextField txtFirstName;

    @FXML
    private TextField txtHouseNumber;

    @FXML
    private TextField txtID;

    @FXML
    private TextField txtIDLoc;

    @FXML
    private TextField txtInfoID;

    @FXML
    private TextField txtLastName;

    @FXML
    private TextField txtMiddleName;

    @FXML
    private TextField txtPhoneNumber;

    @FXML
    private TextField txtStreet;

    @FXML
    private TextField txtTimKiem;

    @FXML
    private TextField txtWard;

    private ObservableList<Account> accountList = FXCollections.observableArrayList();
    private ObservableList<Info> infoList = FXCollections.observableArrayList();
    private ObservableList<Role> roleList = FXCollections.observableArrayList();
    private ObservableList<String> roleNameList = FXCollections.observableArrayList();
    private Map<String, Role> roleMap = new HashMap<>();

    @FXML
    public void initialize() {
        System.out.println(btnCheDo.getText());
        // Gắn các cột trong TableView với thuộc tính của Account
        colAccountID.setCellValueFactory(new PropertyValueFactory<>("accountID"));
        colAccountName.setCellValueFactory(new PropertyValueFactory<>("accountName"));
        colCIC.setCellValueFactory(new PropertyValueFactory<>("CIC"));
        colMail.setCellValueFactory(new PropertyValueFactory<>("mail"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colRole.setCellValueFactory(cellData -> {
            Role role = cellData.getValue().getRole();
            return new SimpleStringProperty(role != null ? role.getRoleName() : "N/A");
        });

        // Gắn các cột trong TableView với thuộc tính của Info
        colInfoID.setCellValueFactory(new PropertyValueFactory<>("infoID"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colMiddleName.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
//        colDOB.setCellValueFactory(cellData -> {
//            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//            LocalDate localDate = cellData.getValue().getDateOfBirth();
//            return new SimpleStringProperty(localDate.format(formatter));
//        });
        colDOB.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        colDOB.setCellFactory(column -> new TableCell<Info, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Chuyển định dạng từ ISO sang dd/MM/yyyy
                    String formattedDate = item.substring(8, 10) + "/" + item.substring(5, 7) + "/" + item.substring(0, 4);
                    setText(formattedDate);
                }
            }
        });
        colSex.setCellValueFactory(cellData -> {
            boolean sex = cellData.getValue().getSex();
            return new SimpleStringProperty(sex ? "Nam" : "Nữ");
        });
        colHouseNumber.setCellValueFactory(new PropertyValueFactory<>("houseNumber"));
        colStreet.setCellValueFactory(new PropertyValueFactory<>("street"));
        colWard.setCellValueFactory(new PropertyValueFactory<>("ward"));
        colDistrict.setCellValueFactory(new PropertyValueFactory<>("district"));
        colCity.setCellValueFactory(new PropertyValueFactory<>("city"));

        tableAccount.setItems(accountList);
        tableInfo.setItems(infoList);

        // Fetch accounts
        fetchAccounts(APIAccount_URL);

        // Fill combobox
        fetchRoles(cmbRole);

        // Thêm listener cho TableView Account
        tableAccount.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateAccountFields(newValue); // Gọi phương thức để đổ dữ liệu vào các trường
                btnSua.setDisable(false);
                btnXoa.setDisable(false);

//                String CIC = newValue.getCIC();
//                System.out.println(APIInfo_URL + "&CIC=" + CIC);
//                fetchInfo(APIInfo_URL + "&CIC=" + CIC);

                // Thực hiện listener của tableInfo
                fireTableInfoListener(newValue);
            }
        });

        // Thêm listener cho TableView Info
        tableInfo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateInfoFields(newValue); // Gọi phương thức để đổ dữ liệu vào các trường
                btnSua.setDisable(false);
                btnXoa.setDisable(false);
            }
        });

        // Init toggle button btnCheDo
        btnCheDo.selectedProperty().addListener((observable, oldValue, newValue) -> {
            btnCheDo.setText(newValue ? "Tài khoản" : "Thông tin");
            System.out.println(btnCheDo.getText());
        });
    }

    @FXML
    void handleBtnHuyLoc(ActionEvent event) {

    }

    @FXML
    void handleBtnLoc(ActionEvent event) {

    }

    @FXML
    void handleBtnLuu(ActionEvent event) {

    }

    @FXML
    void handleBtnRefresh(ActionEvent event) {
        handleRefresh();
        fetchAccounts(APIAccount_URL);
        infoList.clear();
    }

    @FXML
    void handleBtnSua(ActionEvent event) {
        isThem = false;
        if (Objects.equals(btnCheDo.getText(), "Click để chọn")) {
            showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Chưa chọn chế độ", "Hãy chọn bảng mà bạn muốn thao tác");
            btnCheDo.requestFocus();
            return;
        }
        accountMode = btnCheDo.getText() == "Tài khoản";

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

        if (accountMode) {
            // Create an empty account row
            Account newAccount = new Account();

            // Thêm account vào TableView
            accountList.add(newAccount);

            // Focus vào dòng mới
            tableAccount.getSelectionModel().select(newAccount);
            tableAccount.scrollTo(newAccount);

            tableAccount.setDisable(true);
            ancNhapLieuAccount.setDisable(false);
            ancAccountView.requestFocus();

            // Clear inputs
            clearAccountFields();
        } else {
            // Create an empty account row
            Info newInfo = new Info();

            // Thêm account vào TableView
            infoList.add(newInfo);

            // Focus vào dòng mới
            tableInfo.getSelectionModel().select(newInfo);
            tableInfo.scrollTo(newInfo);

            tableInfo.setDisable(true);
            ancNhapLieuInfo.setDisable(false);
            ancAccountView.requestFocus();

            // Clear inputs
            clearInfoFields();
        }
    }

    @FXML
    void handleBtnThem(ActionEvent event) {
        isThem = true;
        if (Objects.equals(btnCheDo.getText(), "Click để chọn")) {
            showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Chưa chọn chế độ", "Hãy chọn bảng mà bạn muốn thao tác");
            btnCheDo.requestFocus();
            return;
        }
        accountMode = Objects.equals(btnCheDo.getText(), "Tài khoản");

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

        if (accountMode) {
            // Create an empty account row
            Account newAccount = new Account();

            // Thêm account vào TableView
            accountList.add(newAccount);

            // Focus vào dòng mới
            tableAccount.getSelectionModel().select(newAccount);
            tableAccount.scrollTo(newAccount);

            tableAccount.setDisable(true);
            tableInfo.setDisable(true);
            ancNhapLieuAccount.setDisable(false);
            ancAccountView.requestFocus();

            // Clear inputs
            clearAccountFields();
        } else {
            // Kiểm tra nếu chưa chọn dòng nào trong tableAccount
            if (tableAccount.getSelectionModel().getSelectedItem() == null) {
                showAlert(Alert.AlertType.WARNING, "Thông báo", "Chưa chọn tài khoản", "Hãy chọn một dòng trong bảng Tài khoản để thêm thông tin.");
                handleRefresh();
                return;
            }

            // Kiểm tra nếu tableInfo có dữ liệu
            if (!infoList.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Thông báo", "Thông tin đã tồn tại", "Tài khoản đã có thông tin trong bảng Info.");
                handleRefresh();
                return;
            }

            // Create an empty account row
            Info newInfo = new Info();

            // Thêm account vào TableView
            infoList.add(newInfo);

            // Focus vào dòng mới
            tableInfo.getSelectionModel().select(newInfo);
            tableInfo.scrollTo(newInfo);

            tableInfo.setDisable(true);
            tableAccount.setDisable(true);
            ancNhapLieuInfo.setDisable(false);
            ancAccountView.requestFocus();

            // Clear inputs
            clearInfoFields();
        }
    }

    @FXML
    void handleBtnTimKiem(ActionEvent event) {

    }

    @FXML
    void handleBtnUndo(ActionEvent event) {
        isThem = false;

        if (accountMode) {
            // Xóa dòng đang được chọn (nếu là dòng mới)
            Account selectedAccount = tableAccount.getSelectionModel().getSelectedItem();
            if (selectedAccount != null && selectedAccount.getAccountID() == 0) { // Kiểm tra nếu ID = 0 là dòng mới
                accountList.remove(selectedAccount);
            }

            // Hủy focus
            tableAccount.getSelectionModel().clearSelection();
        } else {
            // Xóa dòng đang được chọn (nếu là dòng mới)
            Info selectedInfo = tableInfo.getSelectionModel().getSelectedItem();
            if (selectedInfo != null && selectedInfo.getInfoID() == 0) { // Kiểm tra nếu ID = 0 là dòng mới
                infoList.remove(selectedInfo);
            }

            // Hủy focus
            tableInfo.getSelectionModel().clearSelection();
        }

        // Toggle Button Visibility
        handleRefresh();

        // Clear inputs
        clearAccountFields();
        clearInfoFields();
    }

    @FXML
    void handleBtnXacNhanLoc(ActionEvent event) {

    }

    @FXML
    void handleBtnXoa(ActionEvent event) {

    }

//    public void fetchInfo(String ApiUrl) {
//        try {
//            // Tạo HttpClient
//            HttpClient client = HttpClient.newHttpClient();
//
//            // Tạo HttpRequest
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create(ApiUrl))
//                    .header("Authorization", "Bearer " + token)
//                    .GET()
//                    .build();
//
//            // Gửi request và nhận response
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//            // Kiểm tra trạng thái
//            if (response.statusCode() == 200) {
//                // Parse JSON thành ApiResponse
//                ObjectMapper objectMapper = new ObjectMapper();
////                objectMapper.registerModule(new JavaTimeModule());
////                SimpleModule module = new SimpleModule();
////                module.addDeserializer(LocalDate.class, new LocalDateDeserializer());
////                objectMapper.registerModule(module);
//
//                // Ánh xạ phản hồi
//                ApiResponses<ApiResponses.InfoListData> apiResponse = objectMapper.readValue(
//                        response.body(),
//                        new TypeReference<>() {}
//                );
//
//                // Lấy danh sách sản phẩm từ ApiResponse
//                List<Info> infos = apiResponse.getData().getInfos();
//
//                // Cập nhật dữ liệu vào ObservableList
//                infoList.setAll(infos);
//
////                List<Info> infos = apiResponse.getData() != null ? apiResponse.getData().getInfo() : new ArrayList<>();
////                if (infos != null) {
////                    infoList.setAll(infos);
////                } else {
////                    infoList.clear(); // Clear the list if no data is available
////                }
//            } else {
//                System.out.println("Lỗi API: " + response.statusCode());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void fetchInfo(String apiUrl, Consumer<List<Info>> callback) {
        new Thread(() -> {
            try {
                // Tạo HttpClient
                HttpClient client = HttpClient.newHttpClient();

                // Tạo HttpRequest
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();

                // Gửi request và nhận response
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Kiểm tra trạng thái
                if (response.statusCode() == 200) {
                    // Parse JSON thành ApiResponse
                    ObjectMapper objectMapper = new ObjectMapper();

                    // Ánh xạ phản hồi
                    ApiResponses<ApiResponses.InfoListData> apiResponse = objectMapper.readValue(
                            response.body(),
                            new TypeReference<>() {}
                    );

                    // Lấy danh sách Info từ ApiResponse
                    List<Info> infos = apiResponse.getData().getInfos();

                    // Cập nhật dữ liệu vào ObservableList
                    infoList.setAll(infos);

                    // Gọi callback trên Application Thread
                    Platform.runLater(() -> callback.accept(infos));
                } else {
                    System.out.println("Lỗi API: " + response.statusCode());
                    Platform.runLater(() -> callback.accept(new ArrayList<>())); // Trả về danh sách rỗng
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> callback.accept(new ArrayList<>())); // Trả về danh sách rỗng khi lỗi
            }
        }).start();
    }


    public void fetchRoles(ComboBox<String> comboBox) {
        String roleApiUrl = "http://127.0.0.1:3000/api/v1/roles/?limit=100"; // Thay bằng API thực tế

        try {
            // Tạo HttpClient
            HttpClient client = HttpClient.newHttpClient();

            // Tạo HttpRequest
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(roleApiUrl))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            // Gửi request và nhận response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Kiểm tra trạng thái
            if (response.statusCode() == 200) {
                // Parse JSON thành danh sách Role
                ObjectMapper objectMapper = new ObjectMapper();

                // Ánh xạ phản hồi
                ApiResponses<ApiResponses.RoleListData> apiResponse = objectMapper.readValue(
                        response.body(),
                        new TypeReference<>() {}
                );

                // Lấy danh sách Role từ ApiResponse
                List<Role> roles = apiResponse.getData().getRoles();

                // Cập nhật roleList
                roleList.clear();
                roleNameList.clear();
                roleMap.clear();

                for (Role role : roles) {
                    roleList.add(role);
                    String displayName = role.getRoleName();
                    roleNameList.add(displayName);
                    roleMap.put(displayName, role);
                }

                // Gắn danh sách vào ComboBox
                comboBox.setItems(roleNameList);
            } else {
                System.out.println("Lỗi API: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fetchAccounts(String ApiUrl) {
        try {
            // Tạo HttpClient
            HttpClient client = HttpClient.newHttpClient();

            // Tạo HttpRequest
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiUrl))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            // Gửi request và nhận response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Kiểm tra trạng thái
            if (response.statusCode() == 200) {
                // Parse JSON thành ApiResponse
                ObjectMapper objectMapper = new ObjectMapper();

                // Ánh xạ phản hồi
                ApiResponses<ApiResponses.AccountListData> apiResponse = objectMapper.readValue(
                        response.body(),
                        new TypeReference<>() {}
                );

                // Lấy danh sách sản phẩm từ ApiResponse
                List<Account> accounts = apiResponse.getData().getAccounts();

                // Cập nhật dữ liệu vào ObservableList
                accountList.setAll(accounts);
            } else {
                System.out.println("Lỗi API: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateAccountFields(Account selectedAccount) {
        // Đổ dữ liệu vào TextField
        txtID.setText(String.valueOf(selectedAccount.getAccountID()));
        txtAccountName.setText(selectedAccount.getAccountName());
        txtCIC.setText(selectedAccount.getCIC());

        // Đổ dữ liệu vào ComboBox
        if (selectedAccount.getRole() != null) {
            Role role = selectedAccount.getRole();

            for (Map.Entry<String, Role> entry : roleMap.entrySet()) {
                if (entry.getValue().getRoleID() == role.getRoleID()) {
                    cmbRole.setValue(entry.getKey());
                    break;
                }
            }
        } else {
            cmbRole.setValue(null);
        }
    }

    private void populateInfoFields(Info selectedInfo) {
        // Đổ dữ liệu vào TextField
        txtInfoID.setText(String.valueOf(selectedInfo.getInfoID()));
        txtPhoneNumber.setText(selectedInfo.getPhoneNumber());
        txtFirstName.setText(selectedInfo.getFirstName());
        txtMiddleName.setText(selectedInfo.getMiddleName());
        txtLastName.setText(selectedInfo.getLastName());
        txtHouseNumber.setText(selectedInfo.getHouseNumber());
        txtStreet.setText(selectedInfo.getStreet());
        txtWard.setText(selectedInfo.getWard());
        txtDistrict.setText(selectedInfo.getDistrict());
        txtCity.setText(selectedInfo.getCity());
        String dob = selectedInfo.getDateOfBirth();
        if (dob != null) {
            txtDOB.setText(dob.substring(8, 10) + "/" + dob.substring(5, 7) + "/" + dob.substring(0, 4));
        } else {
            txtDOB.setText(null);
        }
        boolean sex = selectedInfo.getSex();
        if (sex) {
            rdNam.setSelected(true);
            rdNu.setSelected(false);
        } else {
            rdNu.setSelected(true);
            rdNam.setSelected(false);
        }
    }

    private void fireTableInfoListener(Account selectedAccount) {
//        fetchInfo(APIInfo_URL + "&CIC=" + selectedAccount.getCIC());
        fetchInfo(APIInfo_URL + "&CIC=" + selectedAccount.getCIC(), infos -> {
            if (infos != null && !infos.isEmpty()) {
                // Lấy thông tin đầu tiên từ danh sách và đổ vào textField
                populateInfoFields(infos.get(0));
            } else {
                System.out.println("Không tìm thấy thông tin liên quan.");
                clearInfoFields(); // Xóa dữ liệu textField nếu không tìm thấy Info
            }
        });
    }

    private void clearAccountFields() {
        txtID.setText(null);
        txtAccountName.setText(null);
        txtCIC.setText(null);
        cmbRole.setValue(null);
    }

    private void clearInfoFields() {
        txtInfoID.clear();
        txtPhoneNumber.clear();
        txtFirstName.clear();
        txtMiddleName.clear();
        txtLastName.clear();
        txtHouseNumber.clear();
        txtStreet.clear();
        txtWard.clear();
        txtDistrict.clear();
        txtCity.clear();
        txtDOB.clear();
        rdNam.setSelected(false);
        rdNu.setSelected(false);
    }

    public void handleRefresh() {
        isThem = false;
        accountMode = true;

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

        tableAccount.setDisable(false);
        tableInfo.setDisable(false);
        ancNhapLieuAccount.setDisable(true);
        ancNhapLieuInfo.setDisable(true);
        ancAccountView.requestFocus();

        // Clear inputs
        clearInfoFields();
    }
}
