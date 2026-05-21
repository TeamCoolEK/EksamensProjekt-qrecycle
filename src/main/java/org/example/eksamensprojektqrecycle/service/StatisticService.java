package org.example.eksamensprojektqrecycle.service;

import org.example.eksamensprojektqrecycle.model.dto.AdminCollectionStatisticDTO;
import org.example.eksamensprojektqrecycle.model.entity.Collection;
import org.example.eksamensprojektqrecycle.repository.CollectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticService {

    private final CollectionRepository collectionRepository;

    public StatisticService(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    public int getBagsReadyForPickup() {
        return collectionRepository.findAll()
                .stream()
                .filter(collection -> collection.getStatus().name().equals("KLAR"))
                .mapToInt(Collection::getBusinessBags)
                .sum();
    }

    public List<AdminCollectionStatisticDTO> getCollectionStatisticsForAdmin() {

        return collectionRepository.findAll()
                .stream()
                .map(collection -> new AdminCollectionStatisticDTO(
                        collection.getId(),
                        collection.getBusiness().getCompanyName(),
                        "Ukendt chauffør",
                        collection.getCreatedAt(),
                        collection.getUpdatedAt(),
                        collection.getBusinessBags(),
                        collection.getDriverBags(),
                        collection.getBusinessBags() + collection.getDriverBags(),
                        collection.getStatus()
                ))
                .toList();
    }
}