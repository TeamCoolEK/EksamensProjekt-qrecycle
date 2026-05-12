package org.example.eksamensprojektqrecycle.config;

import org.example.eksamensprojektqrecycle.model.entity.*;
import org.example.eksamensprojektqrecycle.model.enums.Role;
import org.example.eksamensprojektqrecycle.model.enums.Status;
import org.example.eksamensprojektqrecycle.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Profile("dev") // køre kun på dev
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final RouteRepository routeRepository;
    private final TempStopRepository tempStopRepository;
    private final CollectionRepository collectionRepository;
    private final ExpenseRepository expenseRepository;

    public DataLoader(UserRepository userRepository,
                      BusinessRepository businessRepository,
                      RouteRepository routeRepository,
                      TempStopRepository tempStopRepository,
                      CollectionRepository collectionRepository,
                      ExpenseRepository expenseRepository) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.routeRepository = routeRepository;
        this.tempStopRepository = tempStopRepository;
        this.collectionRepository = collectionRepository;
        this.expenseRepository = expenseRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) { // hvis userRepository er over 0, så skal den ikke køre commandLineRunner
            return;
        }

        // Brugere
        AppUser admin = userRepository.save(new AppUser("admin", "admin", Role.ADMIN));
        AppUser driver1 = userRepository.save(new AppUser("driver1", "driver1", Role.DRIVER));
        AppUser driver2 = userRepository.save(new AppUser("driver2", "driver2", Role.DRIVER));
        AppUser bizUser1 = userRepository.save(new AppUser("Franks Pizza APS", "password", Role.BUSINESS));
        AppUser bizUser2 = userRepository.save(new AppUser("genbrug_syd", "password", Role.BUSINESS));
        AppUser bizUser3 = userRepository.save(new AppUser("genbrug_oest", "password", Role.BUSINESS));

        // Virksomheder
        Business biz1 = businessRepository.save(new Business("Franks Pizza ApS", "Frank Adamsen", "28123456", "Nørrebrogade 12, 2200 København N", bizUser1));
        Business biz2 = businessRepository.save(new Business("Genbrug Syd ApS", "Frederik Jensen", "28765432", "Amager Landevej 88, 2300 København S", bizUser2));
        Business biz3 = businessRepository.save(new Business("Genbrug Øst ApS", "Emma Nielsen", "29112233", "Østerbrogade 45, 2100 København Ø", bizUser3));

        // Ruter
        Route route1 = routeRepository.save(new Route(Status.KLAR));
        Route route2 = routeRepository.save(new Route(Status.IKKE_KLAR));

        // Midlertidige stop
        tempStopRepository.save(new TempStop("Nørrebrogade 12, 2200 København N", route1));
        tempStopRepository.save(new TempStop("Amager Landevej 88, 2300 København S", route1));
        tempStopRepository.save(new TempStop("Østerbrogade 45, 2100 København Ø", route2));
        tempStopRepository.save(new TempStop("Vesterbrogade 3, 1620 København V", route2));

        // Afhentninger
        collectionRepository.save(new Collection(Status.AFHENTET, 5, 5, LocalDate.now().minusDays(3), biz1, route1));
        collectionRepository.save(new Collection(Status.KLAR,     5, 0, LocalDate.now(),              biz2, route1));
        collectionRepository.save(new Collection(Status.IKKE_KLAR, 0, 0, LocalDate.now().plusDays(2), biz3, route2));

        // Udgifter
        expenseRepository.save(new Expense(124.50, "Diesel", null, null, LocalDate.now().minusDays(1), driver1));
        expenseRepository.save(new Expense(49.00,  "Motorvejsbillet", null, null, LocalDate.now().minusDays(5), driver1));
        expenseRepository.save(new Expense(220.00, "Reparation af bil", null, null, LocalDate.now().minusDays(2), driver2));

        System.out.println("Dev dummy data indlæst.");
    }
}
