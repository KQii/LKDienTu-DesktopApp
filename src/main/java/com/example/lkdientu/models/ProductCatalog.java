package com.example.lkdientu.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProductCatalog {
    private int productCatalogID;
    private String productCatalogName;
    private List<ProductCatalog> childs = new ArrayList<>(); // Danh sách các ProductCatalog con
    private ProductCatalog parent;

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
        this.childs = (childs != null) ? childs : new ArrayList<>();
    }

    public ProductCatalog getParent() {
        return parent;
    }

    public void setParent(ProductCatalog parent) {
        this.parent = parent;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductCatalog that = (ProductCatalog) o;
        return productCatalogID == that.productCatalogID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productCatalogID);
    }
}
