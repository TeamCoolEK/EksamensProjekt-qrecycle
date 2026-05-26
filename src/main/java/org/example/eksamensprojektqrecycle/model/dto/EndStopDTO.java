package org.example.eksamensprojektqrecycle.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EndStopDTO {

    private String address;

    public EndStopDTO() {}

    public EndStopDTO(String address) {
        this.address = address;
    }
}
