package org.example.eksamensprojektqrecycle.service;

import org.example.eksamensprojektqrecycle.model.dto.CreateBusinessDTO;
import org.example.eksamensprojektqrecycle.model.dto.UpdateBusinessDTO;
import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.model.entity.Business;
import org.example.eksamensprojektqrecycle.model.enums.Role;
import org.example.eksamensprojektqrecycle.repository.BusinessRepository;
import org.example.eksamensprojektqrecycle.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;

    public BusinessService(
            BusinessRepository businessRepository,
            UserRepository userRepository
    ) {
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
    }

    public Business createBusiness(CreateBusinessDTO dto) {
        validateBusiness(dto);
        // Genererer 4-cifret kode
        String generatedPassword = generateFourDigitPassword();

        // Opretter bruger
        AppUser user = new AppUser();
        user.setUsername(dto.getUsername());
        user.setPassword(generatedPassword);
        user.setRole(Role.BUSINESS);
        AppUser savedUser = userRepository.save(user);

        // Opretter virksomhed
        Business business = new Business();
        business.setCompanyName(dto.getCompanyName());
        business.setContactPerson(dto.getContactPerson());
        business.setPhoneNumber(dto.getPhoneNumber());
        business.setAddress(dto.getAddress());

        // Kobler bruger på virksomhed
        business.setAppUser(savedUser);

        return businessRepository.save(business);
    }

    private void validateBusiness(CreateBusinessDTO dto) {
        if (dto.getCompanyName() == null || dto.getCompanyName().isBlank()) {
            throw new RuntimeException("Virksomhedsnavn mangler");
        }

        if (dto.getUsername() == null || dto.getUsername().isBlank()) {
            throw new RuntimeException("Username mangler");
        }
    }

    private String generateFourDigitPassword() {
        Random random = new Random();
        int number = 1000 + random.nextInt(9000);

        return String.valueOf(number);
    }

    public List<Business> getAllBusinesses() {
        return businessRepository.findAll();
    }

    public Business updateBusiness(int id, UpdateBusinessDTO dto) {

        Business business = businessRepository.findById(id).orElseThrow(() ->
                        new RuntimeException("Virksomhed blev ikke fundet"));

        validateUpdateBusiness(dto);

        business.setCompanyName(dto.getCompanyName());
        business.setContactPerson(dto.getContactPerson());
        business.setPhoneNumber(dto.getPhoneNumber());
        business.setAddress(dto.getAddress());

        return businessRepository.save(business);
    }

    private void validateUpdateBusiness(UpdateBusinessDTO dto) {

        if (dto.getCompanyName() == null || dto.getCompanyName().isBlank()) {
            throw new RuntimeException("Virksomhedsnavn mangler");
        }
        if (dto.getContactPerson() == null || dto.getContactPerson().isBlank()) {
            throw new RuntimeException("Kontaktperson mangler");
        }
        if (dto.getPhoneNumber() == null || dto.getPhoneNumber().isBlank()) {
            throw new RuntimeException("Telefonnummer mangler");
        }
        if (dto.getAddress() == null || dto.getAddress().isBlank()) {
            throw new RuntimeException("Adresse mangler");
        }
    }

    public void deleteBusiness(int id) {
        Business business = businessRepository.findById(id).orElseThrow(()-> new RuntimeException("Virksomheden blev ikke fundet"));

        businessRepository.delete(business);
    }
}