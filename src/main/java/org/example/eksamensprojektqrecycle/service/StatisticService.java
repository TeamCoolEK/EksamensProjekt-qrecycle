package org.example.eksamensprojektqrecycle.service;

import org.example.eksamensprojektqrecycle.model.enums.Status;
import org.example.eksamensprojektqrecycle.repository.CollectionRepository;
import org.springframework.stereotype.Service;

@Service
public class StatisticService {

private final CollectionRepository collectionRepository;

public StatisticService(CollectionRepository collectionRepository) {
    this.collectionRepository = collectionRepository;
}

public int getBagsReadyForPickup(){

    Integer bags = collectionRepository.sumBusinessBagsByStatus(Status.KLAR);
    if(bags == null){
        return 0;
    }
    return bags;
}

}
