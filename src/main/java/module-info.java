module com.example.lkdientu {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;


    opens com.example.lkdientu to javafx.fxml;
    exports com.example.lkdientu;
    exports com.example.lkdientu.controllers;
    opens com.example.lkdientu.controllers to javafx.fxml;
    opens com.example.lkdientu.models to javafx.base, com.fasterxml.jackson.databind;
    opens com.example.lkdientu.utils to javafx.base, com.fasterxml.jackson.databind;
}