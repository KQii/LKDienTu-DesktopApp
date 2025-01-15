package com.example.lkdientu.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Product {
    private int productID;
    private String productName;
    private String describeProduct;
    private String image;
    private String productInformation;
    private int quantity;
    private double price;
    private double sale;
    private boolean hide;
    private ProductCatalog productCatalog;

    public Product() {
    }

    public Product(int productID, String productName, String describeProduct, String image, String productInformation, int quantity, double price, double sale, boolean hide, ProductCatalog productCatalog) {
        this.productID = productID;
        this.productName = productName;
        this.describeProduct = describeProduct;
        this.image = image;
        this.productInformation = productInformation;
        this.quantity = quantity;
        this.price = price;
        this.sale = sale;
        this.hide = hide;
        this.productCatalog = productCatalog;
    }

    // Getters và Setters
    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescribeProduct() {
        return describeProduct;
    }

    public void setDescribeProduct(String describeProduct) {
        this.describeProduct = describeProduct;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProductInformation() {
        return productInformation;
    }

    public void setProductInformation(String productInformation) {
        this.productInformation = productInformation;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getSale() {
        return sale;
    }

    public void setSale(double sale) {
        this.sale = sale;
    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public ProductCatalog getProductCatalog() {
        return productCatalog;
    }

    public void setProductCatalog(ProductCatalog productCatalog) {
        this.productCatalog = productCatalog;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productID=" + productID +
                ", productName='" + productName + '\'' +
                ", describeProduct='" + describeProduct + '\'' +
                ", image='" + image + '\'' +
                ", productInformation='" + productInformation + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", sale=" + sale +
                ", hide=" + hide +
                ", productCatalog=" + productCatalog +
                '}';
    }
}
