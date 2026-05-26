package org.example.eksamensprojektqrecycle.service;

import org.example.eksamensprojektqrecycle.model.dto.BusinessResponseDTO;
import org.example.eksamensprojektqrecycle.model.dto.CreateBusinessDTO;
import org.example.eksamensprojektqrecycle.model.dto.UpdateBusinessDTO;
import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.model.entity.Business;
import org.example.eksamensprojektqrecycle.model.enums.Role;
import org.example.eksamensprojektqrecycle.repository.BusinessRepository;
import org.example.eksamensprojektqrecycle.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public BusinessService(
            BusinessRepository businessRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Business createBusiness(CreateBusinessDTO dto) {
        validateBusiness(dto);

        // Opretter bruger
        AppUser user = new AppUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
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
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new RuntimeException("Password mangler");
        }
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Brugernavn er allerede i brug");
        }
    }

    public List<BusinessResponseDTO> getAllBusinesses() {
        return businessRepository.findAll()
                .stream()
                .map(business -> new BusinessResponseDTO(
                        business.getId(),
                        business.getCompanyName(),
                        business.getContactPerson(),
                        business.getPhoneNumber(),
                        business.getAddress()
                ))
                .toList();
    }

    public BusinessResponseDTO updateBusiness(int id, UpdateBusinessDTO dto){

        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Virksomhed blev ikke fundet"));

        validateUpdateBusiness(dto);

        business.setCompanyName(dto.getCompanyName());
        business.setContactPerson(dto.getContactPerson());
        business.setPhoneNumber(dto.getPhoneNumber());
        business.setAddress(dto.getAddress());

        Business savedBusiness = businessRepository.save(business);

        return new BusinessResponseDTO(
                savedBusiness.getId(),
                savedBusiness.getCompanyName(),
                savedBusiness.getContactPerson(),
                savedBusiness.getPhoneNumber(),
                savedBusiness.getAddress()
        );
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

        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Virksomheden blev ikke fundet"));

        AppUser appUser = business.getAppUser();

        // Bryder relation begge veje
        if (appUser != null) {
            appUser.setBusiness(null);
        }

        business.setAppUser(null);

        // Gem ændringerne først
        if (appUser != null) {
            userRepository.save(appUser);
        }

        businessRepository.save(business);

        // Slet derefter business
        businessRepository.delete(business);

        // Slet til sidst user
        if (appUser != null) {
            userRepository.delete(appUser);
        }
    }
}