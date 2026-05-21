package org.example.eksamensprojektqrecycle.service;

import org.example.eksamensprojektqrecycle.model.dto.DriverLocationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DriverLocationServiceTest {

    private DriverLocationService service;

    // Opretter en frisk service før hver test så ingen tests påvirker hinanden
    @BeforeEach
    void setUp() {
        service = new DriverLocationService();
    }

    // Verificerer at en position gemmes korrekt i ConcurrentHashMap
    @Test
    void updateLocation_gemmerPosition() {
        service.updateLocation("driver1", new DriverLocationDTO(55.6761, 12.5683));

        DriverLocationDTO result = service.getLocation("driver1");
        assertNotNull(result);
        assertEquals(55.6761, result.getLatitude());
        assertEquals(12.5683, result.getLongitude());
    }

    /* Verificerer at update og insert-logikken virker — kun én række per chauffør
    altså når vi kalder updateLocation to gange med samme username, så gemmes kun den seneste position — ikke begge.*/
    @Test
    void updateLocation_overskriverEksisterendePosition() {
        service.updateLocation("driver1", new DriverLocationDTO(55.6761, 12.5683));
        service.updateLocation("driver1", new DriverLocationDTO(55.9999, 12.9999));

        DriverLocationDTO result = service.getLocation("driver1");
        assertEquals(55.9999, result.getLatitude());
        assertEquals(12.9999, result.getLongitude());
    }

    // Verificerer at getLocation returnerer null når chaufføren ikke findes i mappet
    @Test
    void getLocation_returnerNull_naarIngenPosition() {
        assertNull(service.getLocation("ukendt_bruger"));
    }

    // Verificerer at removeLocation sletter chaufføren fra mappet — bruges ved ruteafslutning
    @Test
    void removeLocation_fjernerPosition() {
        service.updateLocation("driver1", new DriverLocationDTO(55.6761, 12.5683));
        service.removeLocation("driver1");

        assertNull(service.getLocation("driver1"));
    }

    // Verificerer at to chauffører ikke blander hinandens positioner i mappet
    @Test
    void toChauffoererHarSeparatePositioner() {
        service.updateLocation("driver1", new DriverLocationDTO(55.6761, 12.5683));
        service.updateLocation("driver2", new DriverLocationDTO(56.1234, 10.5678));

        assertEquals(55.6761, service.getLocation("driver1").getLatitude());
        assertEquals(56.1234, service.getLocation("driver2").getLatitude());
    }
}