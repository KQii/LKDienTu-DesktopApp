package com.example.lkdientu.models;

import java.util.List;

public class ApiResponse {
    private String status;
    private int results;
    private Data data;

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    // Định nghĩa lớp con public static
    public static class Data {
        private List<Product> products;
        private List<ProductCatalog> productCatalogs;

        // Getters và Setters
        public List<Product> getProducts() {
            return products;
        }

        public void setProducts(List<Product> products) {
            this.products = products;
        }

        public List<ProductCatalog> getProductCatalogs() {
            return productCatalogs;
        }

        public void setProductCatalogs(List<ProductCatalog> productCatalogs) {
            this.productCatalogs = productCatalogs;
        }
    }
}
