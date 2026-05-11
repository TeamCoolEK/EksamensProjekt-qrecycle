package org.example.eksamensprojektqrecycle.controller;

import org.example.eksamensprojektqrecycle.model.dto.UpdateCollectionStatusDTO;
import org.example.eksamensprojektqrecycle.model.entity.Collection;
import org.example.eksamensprojektqrecycle.service.PickupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


    @RestController
    @RequestMapping("/virksomhed")
    @CrossOrigin(origins = "*")

    public class BusinessController {

        //Spring injecter automatisk PickupService//
        @Autowired
        private PickupService pickupService;

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
    }