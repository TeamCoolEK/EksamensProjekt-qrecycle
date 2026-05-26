package org.example.eksamensprojektqrecycle.service;

import org.example.eksamensprojektqrecycle.model.dto.EndStopDTO;
import org.example.eksamensprojektqrecycle.model.entity.EndStop;
import org.example.eksamensprojektqrecycle.repository.EndStopRepository;
import org.springframework.stereotype.Service;

@Service
public class EndStopService {

    private final EndStopRepository endStopRepository;

    public EndStopService(EndStopRepository endStopRepository) {
        this.endStopRepository = endStopRepository;
    }

    public EndStop saveEndStop(EndStopDTO dto) {
        EndStop findEndStop = endStopRepository.findById(1);
        findEndStop.setAddress(dto.getAddress());
        return endStopRepository.save(findEndStop);
    }

    public EndStop findEndStop() {
        return endStopRepository.findById(1);
    }
}
