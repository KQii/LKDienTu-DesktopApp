package com.example.lkdientu.models;

import java.util.List;

public class ProductCatalog {
    private int productCatalogID;
    private String productCatalogName;
    private List<ProductCatalog> childs; // Danh sách các ProductCatalog con

    public ProductCatalog() {
    }

    public ProductCatalog(int productCatalogID, String productCatalogName, List<ProductCatalog> childs) {
        this.productCatalogID = productCatalogID;
        this.productCatalogName = productCatalogName;
        this.childs = childs;
    }

    // Getters và Setters
    public int getProductCatalogID() {
        return productCatalogID;
    }

    public void setProductCatalogID(int productCatalogID) {
        this.productCatalogID = productCatalogID;
    }

    public String getProductCatalogName() {
        return productCatalogName;
    }

    public void setProductCatalogName(String productCatalogName) {
        this.productCatalogName = productCatalogName;
    }

    public List<ProductCatalog> getChilds() {
        return childs;
    }

    public void setChilds(List<ProductCatalog> childs) {
        this.childs = childs;
    }

    // Override phương thức toString() (Tùy chọn)
    @Override
    public String toString() {
        return "ProductCatalog{" +
                "productCatalogID=" + productCatalogID +
                ", productCatalogName='" + productCatalogName + '\'' +
                ", childs=" + (childs != null ? childs.size() : 0) + // Hiển thị số lượng con
                '}';
    }
}
