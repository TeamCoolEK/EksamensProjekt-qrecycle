package org.example.eksamensprojektqrecycle.model.dto;

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

    public int getId() {
        return id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }
}