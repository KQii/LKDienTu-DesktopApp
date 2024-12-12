package com.example.lkdientu.models;

public class Product {
    private int productID;
    private String productName;
    private String productCatalog;
    private String productDescription;
    private String productInformation;
    private String image;
    private int quantity;
    private double price;
    private double sale;
    private int hide;

    public Product(int productID, String productName, String productCatalog, String productDescription, String productInformation, String image, int quantity, double price, double sale, int hide) {
        this.productID = productID;
        this.productName = productName;
        this.productCatalog = productCatalog;
        this.productDescription = productDescription;
        this.productInformation = productInformation;
        this.image = image;
        this.quantity = quantity;
        this.price = price;
        this.sale = sale;
        this.hide = hide;
    }

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

    public String getProductCatalog() {
        return productCatalog;
    }

    public void setProductCatalog(String productCatalog) {
        this.productCatalog = productCatalog;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductInformation() {
        return productInformation;
    }

    public void setProductInformation(String productInformation) {
        this.productInformation = productInformation;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public int getHide() {
        return hide;
    }

    public void setHide(int hide) {
        this.hide = hide;
    }
}

