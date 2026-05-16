package org.example.eksamensprojektqrecycle.controller;

import org.example.eksamensprojektqrecycle.model.dto.CreateBusinessDTO;
import org.example.eksamensprojektqrecycle.model.entity.Business;
import org.example.eksamensprojektqrecycle.service.BusinessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.eksamensprojektqrecycle.model.dto.UpdateCollectionStatusDTO;
import org.example.eksamensprojektqrecycle.model.entity.Collection;
import org.example.eksamensprojektqrecycle.service.PickupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// REST controller til virksomhed endpoints
@RestController
// Base URL til business endpoints
@RequestMapping("/business")
// Tillader requests fra frontend
@CrossOrigin(origins = "*")
public class BusinessController {

    // Service bruges til business logik
    private final BusinessService businessService;

    //Spring injecter automatisk PickupService//
    private final PickupService pickupService;

    // Constructor injection
    public BusinessController(BusinessService businessService, PickupService pickupService) {
        this.businessService = businessService;
        this.pickupService = pickupService;
    }


    @PostMapping("/afhentning/klar") //Håndterer POST requests//
    public ResponseEntity<?> markCollectionReady(@RequestBody UpdateCollectionStatusDTO dto) {  //Spring parser JSON fra request til DTO objekt.//
        //<?> betyder "kan returnere hvilken som helst type"//

        try {
            //Kald service til at opdatere collection//
            Collection updatedCollection = pickupService.markReadyForPickup(dto);

            //QE-82: Send bekræftelse tilbage til frontend//
            return ResponseEntity.ok(updatedCollection);

        } catch (RuntimeException e) {
            //Hvis noget gik galt, validering eller collection ikke fundet etc. //

            // -> send fejlbesked til frontend//
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage()); //fejlbeskeden om at afhentningen ikke findes//

        }
    }

    //Hent collection for at vise nuværende status//
    //GET /business/afhentning/{id}//

    @GetMapping("/afhentning/{id}")
    public ResponseEntity<?> getCollection(@PathVariable int id) {

        try {
            Collection collection = pickupService.getCollectionById(id);
            return ResponseEntity.ok(collection);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    //QE-114: Virksomhed kan annullere afhentning//
    @PostMapping("/afhentning/{id}/annuller")
    public ResponseEntity<?> cancelPickup(@PathVariable int id) {

        try {
            // QE-115, 116, 117, 118, 119: Kald service til at annullere
            Collection cancelledCollection = pickupService.cancelPickup(id);

            // QE-120: Send success response tilbage til frontend
            return ResponseEntity.ok(cancelledCollection);

        } catch (RuntimeException e) {
            // Håndter fejl (forkert status, ikke fundet, etc.)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}