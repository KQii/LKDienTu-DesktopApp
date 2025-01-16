package com.example.lkdientu.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Role {
    @JsonProperty("RoleID")
    private int roleID;
    @JsonProperty("RoleName")
    private String roleName;

    public Role() {
    }

    public Role(int roleID, String roleName) {
        this.roleID = roleID;
        this.roleName = roleName;
    }

    public int getRoleID() {
        return roleID;
    }

    public void setRoleID(int roleID) {
        this.roleID = roleID;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
