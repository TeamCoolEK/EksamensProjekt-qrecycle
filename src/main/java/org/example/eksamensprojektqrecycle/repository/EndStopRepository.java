package org.example.eksamensprojektqrecycle.repository;

import org.example.eksamensprojektqrecycle.model.entity.EndStop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EndStopRepository extends JpaRepository<EndStop, Integer> {
    EndStop findById(int id);
}
