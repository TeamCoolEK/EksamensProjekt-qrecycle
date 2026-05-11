package org.example.eksamensprojektqrecycle.model.dto;


import lombok.Getter;
import lombok.Setter;

//Lombok -> genererer automatisk getCollectionID(), setCollectionId() osv. holder koden kort og læsbar//
@Getter
@Setter
public class UpdateCollectionStatusDTO {

    //Hvilken collection skal opdateres?//
    private int collectionId;

    //Hvor mange poser har virksomheden klar?//
    private int businessBags;

    //Tom constructor -> det kræves af Jackson/JSON parsing -> bruges af Spring når den automatisk parser JSON til DTO objekt//
    public UpdateCollectionStatusDTO() {
    }

    //Constructor med parametre(bruges til test)//
    public UpdateCollectionStatusDTO(int collectionId, int businessBags) {
        this.collectionId = collectionId;
        this.businessBags = businessBags;
    }


}
