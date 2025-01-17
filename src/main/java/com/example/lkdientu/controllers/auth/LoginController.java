package com.example.lkdientu.controllers.auth;

import com.example.lkdientu.LKDienTuApplication;
import com.example.lkdientu.SessionManager;
import com.example.lkdientu.models.Account;
import com.example.lkdientu.models.Product;
import com.example.lkdientu.models.ProductCatalog;
import com.example.lkdientu.utils.ApiAuthResponse;
import com.example.lkdientu.utils.ApiErrorResponse;
import com.example.lkdientu.utils.ApiResponses;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.example.lkdientu.utils.AlertUtils.showAlert;

public class LoginController {

    @FXML
    private AnchorPane ancLogin;

    @FXML
    private Button btnDangKy;

    @FXML
    private Button btnDangNhap;

    @FXML
    private Button btnQuenMatKhau;

    @FXML
    private TextField txtAccountName;

    @FXML
    private TextField txtPassword;

    @FXML
    public void initialize() {
        ancLogin.requestFocus();
    }

    @FXML
    void handleBtnDangKy(ActionEvent event) {

    }

    @FXML
    void handleBtnDangNhap(ActionEvent event) {
        postLogin();
    }

    @FXML
    void handleBtnQuenMatKhau(ActionEvent event) {

    }

    public void postLogin() {
        String apiUrl = "http://127.0.0.1:3000/api/v1/accounts/login";

        // Thu thập dữ liệu từ giao diện
        String accountName = txtAccountName.getText();
        String password = txtPassword.getText();

        // Tạo đối tượng JSON chỉ chứa thông tin cần gửi
        String requestBody = String.format(
                "{" +
                        "\"AccountName\": \"%s\"," +
                        "\"Password\": \"%s\"" +
                        "}",
                accountName, password
        );

        // Tạo HTTP Client
        HttpClient client = HttpClient.newHttpClient();

        // Tạo Request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse JSON thành ApiResponse
            ObjectMapper objectMapper = new ObjectMapper();

            if (response.statusCode() == 200) {
                System.out.println("Đăng nhập thành công");
                // Ánh xạ phản hồi
                ApiAuthResponse<ApiAuthResponse.AccountData> apiAuthResponse = objectMapper.readValue(
                        response.body(),
                        new TypeReference<>() {}
                );

                // Lấy sản phẩm từ ApiResponse
                Account account = apiAuthResponse.getData().getAccount();
                String receivedToken = apiAuthResponse.getToken();

                // Hiển thị thông báo thành công
                String contentText = String.format(
                        "Thông tin tài khoản:\n" +
//                                "TOKEN: %s\n" +
                                "AccountID: %d\n" +
                                "AccountName: %s\n" +
                                "CIC: %s\n" +
                                "Mail: %s\n" +
                                "Role: %s\n",
//                        receivedToken,
                        account.getAccountID(),
                        account.getAccountName(),
                        account.getCIC(),
                        account.getMail(),
                        account.getRole() != null ? account.getRole().getRoleName() : "N/A"
                );

                showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Đăng nhập thành công!", contentText);

                SessionManager.getInstance().setToken(receivedToken);
                switchToMainView();
            } else {
                // Xử lý lỗi từ API
                String responseBody = response.body();
                ApiErrorResponse errorResponse = objectMapper.readValue(responseBody, ApiErrorResponse.class);

                // Hiển thị thông báo lỗi
                showAlert(
                        Alert.AlertType.ERROR,
                        "Lỗi từ API " + errorResponse.getError().getStatusCode(),
                        "Đăng nhập thất bại",
                        errorResponse.getMessage()
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void switchToMainView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LKDienTuApplication.class.getResource("views/lkdientu-view.fxml"));
            Stage stage = (Stage) btnDangNhap.getScene().getWindow();
            Scene mainScene = new Scene(fxmlLoader.load());
            stage.setScene(mainScene);
            stage.setTitle("Giao diện chính");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
