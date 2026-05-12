package org.example.eksamensprojektqrecycle.service;

import org.example.eksamensprojektqrecycle.model.entity.Collection;
import org.example.eksamensprojektqrecycle.model.enums.Status;
import org.example.eksamensprojektqrecycle.repository.CollectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import org.example.eksamensprojektqrecycle.model.dto.UpdateCollectionStatusDTO;
import org.example.eksamensprojektqrecycle.model.entity.Collection;
import org.example.eksamensprojektqrecycle.model.enums.Status;
import org.example.eksamensprojektqrecycle.repository.CollectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


//Service til håndtering af pant-afhentninger (pickup/collections)//

@Service
public class PickupService {

    private final CollectionRepository collectionRepository;

    public PickupService(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    // Henter alle afhentninger med status KLAR
    public List<Collection> getActiveCollections() {
        return collectionRepository.findByStatus(Status.KLAR);
    }

    // Markerer afhentning som afsluttet og gemmer antal poser
    public void completeCollection(int id, int bags) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Afhentning ikke fundet"));

        collection.setStatus(Status.AFHENTET);
        collection.setDriverBags(bags);
        collectionRepository.save(collection);
    }


    public Collection getCollectionById(int collectionId) {
        //Kald repository for at søge i databasen//
        //finById() returnerer Optional<Collection> fordi den måske ikke findes//
        Optional<Collection> collectionOptional = collectionRepository.findById(collectionId);
        //Optional = en container der MÅSKE indeholder en værdi -> beskytter mod NullPointerException//

        //Tjek om collection blev fundet//
        if (collectionOptional.isEmpty()) {
            //hvis ikke fundet, kast en fejl med en beskrivelse//
            throw new RuntimeException("Afhentningen blev ikke fundet i systemet.");
        }

        //Hvis fundet, pak Collection ud af Optional og returner.//
        return collectionOptional.get();
    }

    public Collection markReadyForPickup(UpdateCollectionStatusDTO dto) {
        //QE-75: Validér at antal poser er mindst 1//
        if (dto.getBusinessBags() < 1) {
            throw new RuntimeException("Antal poser skal være mindst 1."); //fejlen fanges i controller og sendes til frontend//
        }

        //QE-77: Hent collection fra database//
        Collection collection = getCollectionById(dto.getCollectionId());

        //QE-78: Opdater status til "klar"//
        collection.setStatus(Status.KLAR);

        //QE-79: Gem antal poser (minimum 1)//
        collection.setBusinessBags(dto.getBusinessBags());

        //QE-80: Gem i database og returner//
        return collectionRepository.save(collection);

    }

}
