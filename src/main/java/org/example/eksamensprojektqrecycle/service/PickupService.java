package org.example.eksamensprojektqrecycle.service;

import org.example.eksamensprojektqrecycle.model.dto.CreateCollectionDTO;
import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.model.entity.Business;
import org.example.eksamensprojektqrecycle.model.entity.Collection;
import org.example.eksamensprojektqrecycle.model.enums.Status;
import org.example.eksamensprojektqrecycle.repository.BusinessRepository;
import org.example.eksamensprojektqrecycle.repository.CollectionRepository;
import org.example.eksamensprojektqrecycle.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import org.example.eksamensprojektqrecycle.model.dto.UpdateCollectionStatusDTO;
import java.util.Optional;


//Service til håndtering af pant-afhentninger (pickup/collections)//

@Service
public class PickupService {

    private final CollectionRepository collectionRepository;
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;

    public PickupService(CollectionRepository collectionRepository, UserRepository userRepository, BusinessRepository businessRepository) {
        this.collectionRepository = collectionRepository;
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
    }

    public Collection getCollectionForAuthenticatedUser(Authentication authentication) {
        String username = authentication.getName(); // ← principal er sat til username i JWT validator filter

        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Bruger ikke fundet: " + username));

        Business business = businessRepository.findByAppUser(user)
                .orElseThrow(() -> new RuntimeException("Ingen virksomhed fundet for bruger: " + username));

        //liste af aktive statuser som skal hente en collection frem
        List<Status> activeStatus = List.of(Status.KLAR, Status.IKKE_KLAR);

        //retunerer en collection med status klar/ikke_klar
        return collectionRepository.findByBusinessAndStatusIn(business, activeStatus)
                .or(() -> collectionRepository.findTopByBusinessAndStatusOrderByUpdatedAtDesc(business, Status.AFHENTET))
                .orElseThrow(() -> new RuntimeException("Ingen afhentning fundet for virksomhed"));
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

    //bruges til at markere collection klar. Hvis collection er klar eller ikke klar opdateres, hvis afhentet laves en ny
    public Collection markReadyForPickup(CreateCollectionDTO dto, Authentication authentication) {
        //QE-75: Validér at antal poser er mindst 1//
        if (dto.getBusinessBags() < 1) {
            throw new RuntimeException("Antal poser skal være mindst 1."); //fejlen fanges i controller og sendes til frontend//
        }

        //finder user fra auth
        String username = authentication.getName();
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Bruger ikke fundet: " + username));

        //finder business ud fra ID
        Business business = businessRepository.findByAppUser(user)
                .orElseThrow(() -> new RuntimeException("Virksomhed ikke fundet, Kontakt ADMIN"));

        //henter collection fra db ud fra business id
        Optional<Collection> existingCollection = collectionRepository.findByBusinessAndStatusNot(business, Status.AFHENTET);

        if (existingCollection.isEmpty() || existingCollection.get().getStatus() == Status.AFHENTET) {
            Collection newCollection = new Collection(Status.KLAR, dto.getBusinessBags(), 0, business, null);
            return collectionRepository.save(newCollection);
        }

        //QE-77: Hent collection fra database//Bruger eksisterende collection
        Collection collection = existingCollection.get();

        //QE-78: Opdater status til "klar"//
        collection.setStatus(Status.KLAR);

        //QE-79: Gem antal poser (minimum 1)//
        collection.setBusinessBags(dto.getBusinessBags());

        //QE-80: Gem i database og returner//
        return collectionRepository.save(collection);

    }

    public Collection cancelPickup(int collectionId) {
        //QE-116: Hent collection fra database via ID//
        Collection collection =getCollectionById(collectionId);

        //QE-115: validér at kun afhentninger med status KLAR kan annulleres - tjekker at status er KLAR før annullering//
        if (collection.getStatus() != Status.KLAR) {
            throw new RuntimeException(
                    "Kun  afhentninger med status 'Klar' kan annulleres." +
                            "Nuværende status: " + collection.getStatus()
            );
        }

        //QE-117: Opdaterer status fra KLAR tilbage til IKKE_KLAR//
        collection.setStatus(Status.IKKE_KLAR);

        //QE-118: Gem statusændringen i DB//
        //QE-119: @PreUpdate fra Collection entity klassen opdaterer//
        return collectionRepository.save(collection);
    }
}
