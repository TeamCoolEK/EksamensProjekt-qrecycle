package org.example.eksamensprojektqrecycle.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.eksamensprojektqrecycle.model.enums.Status;

@Getter
@Setter
public class CollectionRequestDTO {

    private int id;
    private String businessName;
    private String address;
    private Status status;
    private int businessBags;
    private int driverBags;
}