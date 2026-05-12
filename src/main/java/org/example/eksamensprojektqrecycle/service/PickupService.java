package org.example.eksamensprojektqrecycle.service;

import org.example.eksamensprojektqrecycle.model.entity.Collection;
import org.example.eksamensprojektqrecycle.model.enums.Status;
import org.example.eksamensprojektqrecycle.repository.CollectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
}