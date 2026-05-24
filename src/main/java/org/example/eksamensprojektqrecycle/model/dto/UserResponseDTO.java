package org.example.eksamensprojektqrecycle.model.dto;

import lombok.Data;

@Data
public class UserResponseDTO {
    private int id;
    private String username;
    private String role;

    public UserResponseDTO() {
        this.id = id;
        this.username = username;
        this.role = role;


    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

}