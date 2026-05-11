package org.example.eksamensprojektqrecycle.repository;

import org.example.eksamensprojektqrecycle.model.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Integer> {
}
