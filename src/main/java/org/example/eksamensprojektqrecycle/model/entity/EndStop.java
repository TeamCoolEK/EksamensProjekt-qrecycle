package org.example.eksamensprojektqrecycle.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class EndStop {

    @Id
    private int id;

    String address;

    public EndStop() {}

    public EndStop(int id, String address) {
        this.id = id;
        this.address = address;
    }
}
