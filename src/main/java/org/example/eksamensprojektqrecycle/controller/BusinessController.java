package org.example.eksamensprojektqrecycle.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

    @RestController
    @RequestMapping("/restaurant")
    @CrossOrigin(origins = "*")
    public class BusinessController {

        @GetMapping("/getRestaurants")
        public List<String> getRestaurants() {
            return List.of("Restaurant 1", "Restaurant 2", "Restaurant 3");
        }
    }
