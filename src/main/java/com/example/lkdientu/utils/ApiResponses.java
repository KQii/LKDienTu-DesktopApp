package com.example.lkdientu.utils;

import com.example.lkdientu.models.*;

import java.util.List;

public class ApiResponses<T> {
    private String status;
    private int results; // Chỉ dùng cho phản hồi chứa danh sách
    private T data;

    // Getters và Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getResults() {
        return results;
    }

    public void setResults(int results) {
        this.results = results;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    // Lớp lồng bên trong để ánh xạ "data" cho danh sách sản phẩm
    public static class ProductListData {
        private List<Product> products;

        public List<Product> getProducts() {
            return products;
        }

        public void setProducts(List<Product> products) {
            this.products = products;
        }
    }

    // Lớp lồng bên trong để ánh xạ "data" cho một sản phẩm
    public static class ProductData {
        private Product product;

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }
    }

    public static class ProductCatalogListData {
        private List<ProductCatalog> productCatalogs;

        public List<ProductCatalog> getProductCatalogs() {
            return productCatalogs;
        }

        public void setProductCatalogs(List<ProductCatalog> productCatalogs) {
            this.productCatalogs = productCatalogs;
        }
    }

    public static class ProductCatalogData {
        private ProductCatalog productCatalog;

        public ProductCatalog getProductCatalog() {
            return productCatalog;
        }

        public void setProductCatalog(ProductCatalog productCatalog) {
            this.productCatalog = productCatalog;
        }
    }

    public static class AccountListData {
        private List<Account> accounts;

        public List<Account> getAccounts() {
            return accounts;
        }

        public void setAccounts(List<Account> accounts) {
            this.accounts = accounts;
        }
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

    public static class RoleListData {
        private List<Role> roles;

        public List<Role> getRoles() {
            return roles;
        }

        public void setRoles(List<Role> roles) {
            this.roles = roles;
        }
    }

    public static class RoleData {
        private Role role;

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }
    }

    public static class InfoListData {
        private List<Info> infos;

        public List<Info> getInfos() {
            return infos;
        }

        public void setInfos(List<Info> infos) {
            this.infos = infos;
        }
    }

    public static class InfoData {
        private Info info;

        public Info getInfo() {
            return info;
        }

        public void setInfo(Info info) {
            this.info = info;
        }
    }
}
