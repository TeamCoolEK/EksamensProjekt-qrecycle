package org.example.eksamensprojektqrecycle.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.eksamensprojektqrecycle.model.enums.Role;

@Getter
@Setter
public class CreateUserDTO {

    private String username;
    private String password;
    private Role role;
}