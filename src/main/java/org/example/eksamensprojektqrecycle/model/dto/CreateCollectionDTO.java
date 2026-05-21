package org.example.eksamensprojektqrecycle.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCollectionDTO {
    private int businessBags;

    //TOM TIL JSON PARSE
    public CreateCollectionDTO() {
    }

    //constructor til test
    public CreateCollectionDTO(int businessBags) {
        this.businessBags = businessBags;
    }
}
