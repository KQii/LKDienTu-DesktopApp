package com.example.lkdientu.utils;

import com.example.lkdientu.models.Account;

public class ApiAuthResponse<T> {
    private String status;
    private String token;
    private T data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static class AccountData {
        private Account account;

        public Account getAccount() {
            return account;
        }

        public void setAccount(Account account) {
            this.account = account;
        }
    }
}
