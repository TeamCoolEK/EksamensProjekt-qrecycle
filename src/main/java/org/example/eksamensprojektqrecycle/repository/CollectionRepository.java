package org.example.eksamensprojektqrecycle.repository;

import org.example.eksamensprojektqrecycle.model.entity.Business;
import org.example.eksamensprojektqrecycle.model.entity.Collection;
import org.example.eksamensprojektqrecycle.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Integer> {

    List<Collection> findByStatus(Status status);
  
    @Query("SELECT SUM(c.businessBags) FROM Collection c WHERE c.status = :status")
    Integer sumBusinessBagsByStatus(@Param("status") Status status);

    Integer Status(Status status);

    Optional<Collection> findByBusiness(Business business);

    //bruges til at finde collection at business AND status = klar, ikke_klar
    Optional<Collection> findByBusinessAndStatusIn(Business business, List<Status> status);

    //bruges til at finde collection at business AND status = afhentet
    Optional<Collection> findByBusinessAndStatusNot(Business business, Status status);

    //bruges til at finde seneste afhentet collection, til visning i business dashboard
    Optional<Collection> findTopByBusinessAndStatusOrderByUpdatedAtDesc(Business business, Status status);

    void deleteByBusiness(Business business);
}

