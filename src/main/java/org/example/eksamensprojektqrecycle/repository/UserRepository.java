package org.example.eksamensprojektqrecycle.repository;

import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Integer> {
}