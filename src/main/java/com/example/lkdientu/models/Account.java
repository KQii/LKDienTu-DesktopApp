package com.example.lkdientu.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Account {
    @JsonProperty("AccountID")
    private int accountID;
    @JsonProperty("AccountName")
    private String accountName;
    @JsonProperty("CIC")
    private String CIC;
    @JsonProperty("Mail")
    private String mail;
    @JsonProperty("Role")
    private Role role;

    public Account(int accountID, String accountName, String CIC, String mail, Role role) {
        this.accountID = accountID;
        this.accountName = accountName;
        this.CIC = CIC;
        this.mail = mail;
        this.role = role;
    }

    public Account() {
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getCIC() {
        return CIC;
    }

    public void setCIC(String CIC) {
        this.CIC = CIC;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
