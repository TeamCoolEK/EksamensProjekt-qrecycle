package org.example.eksamensprojektqrecycle.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessResponseDTO {

    private int id;
    private String companyName;
    private String contactPerson;
    private String phoneNumber;
    private String address;

    public BusinessResponseDTO(int id, String companyName, String contactPerson, String phoneNumber, String address) {
        this.id = id;
        this.companyName = companyName;
        this.contactPerson = contactPerson;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

}