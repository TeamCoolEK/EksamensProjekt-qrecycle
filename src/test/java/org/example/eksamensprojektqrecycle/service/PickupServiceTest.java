package org.example.eksamensprojektqrecycle.service;

import org.example.eksamensprojektqrecycle.model.dto.UpdateCollectionStatusDTO;
import org.example.eksamensprojektqrecycle.model.entity.Collection;
import org.example.eksamensprojektqrecycle.model.enums.Status;
import org.example.eksamensprojektqrecycle.repository.CollectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//Unit tests for PickupService//
//QE-86, QE-87: Test status og antal pant poser opdatering//
//QE-124, QE-125: Test annullering af afhentninger//
class PickupServiceTest {

    // Mock repository (fake database)
    @Mock
    private CollectionRepository collectionRepository;

    // Service vi tester (får injected mock repository)
    @InjectMocks
    private PickupService pickupService;

    // Setup før hver test
    @BeforeEach
    void setUp() {
        // Initialiserer mocks
        MockitoAnnotations.openMocks(this);
    }

    //Tests for markér KLAR funktionalitet//
    //QE-86: Test at status ændres fra IKKE_KLAR til KLAR//
    @Test
    @DisplayName("QE-86: Status skal ændres til KLAR når markReadyForPickup kaldes")
    void testMarkReadyForPickup_StatusChangesToKLAR() {
        // Arrange (forbered test data)
        UpdateCollectionStatusDTO dto = new UpdateCollectionStatusDTO(1, 5);

        Collection existingCollection = new Collection();
        existingCollection.setId(1);
        existingCollection.setStatus(Status.IKKE_KLAR); // Start status
        existingCollection.setBusinessBags(0);

        // Mock repository til at returnere collection
        when(collectionRepository.findById(1)).thenReturn(Optional.of(existingCollection));
        when(collectionRepository.save(any(Collection.class))).thenReturn(existingCollection);

        // Act (kør metoden vi tester)
        Collection result = pickupService.markReadyForPickup(dto);

        // Assert (verificer resultat)
        assertEquals(Status.KLAR, result.getStatus(), "Status skal være KLAR");
        verify(collectionRepository, times(1)).save(any(Collection.class));
    }

    //QE-87: Test at antal poser gemmes korrekt//
    @Test
    @DisplayName("QE-87: businessBags skal gemmes korrekt")
    void testMarkReadyForPickup_BusinessBagsSavedCorrectly() {
        // Arrange
        UpdateCollectionStatusDTO dto = new UpdateCollectionStatusDTO(1, 5);

        Collection existingCollection = new Collection();
        existingCollection.setId(1);
        existingCollection.setStatus(Status.IKKE_KLAR);
        existingCollection.setBusinessBags(0);

        when(collectionRepository.findById(1)).thenReturn(Optional.of(existingCollection));
        when(collectionRepository.save(any(Collection.class))).thenReturn(existingCollection);

        // Act
        Collection result = pickupService.markReadyForPickup(dto);

        // Assert
        assertEquals(5, result.getBusinessBags(), "businessBags skal være 5");
        verify(collectionRepository, times(1)).save(any(Collection.class));
    }

    //QE-75: Test validering - skal kaste fejl hvis businessBags < 1 //
    @Test
    @DisplayName("QE-75: Skal kaste exception hvis businessBags er 0")
    void testMarkReadyForPickup_ThrowsExceptionWhenBagsIsZero() {
        // Arrange
        UpdateCollectionStatusDTO dto = new UpdateCollectionStatusDTO(1, 0);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pickupService.markReadyForPickup(dto);
        });

        assertTrue(exception.getMessage().contains("mindst 1"),
                "Fejlbesked skal nævne mindst 1 pose");
    }

    //QE-86 + QE-87: Test komplet flow med både status og poser//
    @Test
    @DisplayName("QE-86 + QE-87: Kompllet opdatering af status og poser")
    void testMarkReadyForPickup_CompleteUpdate() {
        //Arrange
        UpdateCollectionStatusDTO dto = new UpdateCollectionStatusDTO(1, 3);

        Collection existingCollection = new Collection();
        existingCollection.setId(1);
        existingCollection.setStatus(Status.IKKE_KLAR);
        existingCollection.setBusinessBags(0);
        existingCollection.setDate(LocalDate.now());

        when(collectionRepository.findById(1)).thenReturn(Optional.of(existingCollection));
        when(collectionRepository.save(any(Collection.class))).thenReturn(existingCollection);

        //Act//
        Collection result = pickupService.markReadyForPickup(dto);

        //Assert//
        assertAll("Alle felter skal opdateres korrektt",
                () -> assertEquals(Status.KLAR, result.getStatus(), "Status skal være KLAR"),
                () -> assertEquals(3, result.getBusinessBags(), "businessBags skal være 3"),
                () -> assertEquals(1, result.getId(), "ID skal forblive 1")
        );

        //Verificer at save blev kaldt præcis én gang//
        verify(collectionRepository, times(1)).save(existingCollection);

    }

    //QE-77: Test at collection hentes korrekt//
    @Test
    @DisplayName("QE-77: getCollectionById skal returnere collection når den findes")
    void testGetCollectionById_ReturnsCollection() {
        // Arrange
        Collection collection = new Collection();
        collection.setId(1);
        collection.setStatus(Status.IKKE_KLAR);

        when(collectionRepository.findById(1)).thenReturn(Optional.of(collection));

        // Act
        Collection result = pickupService.getCollectionById(1);

        // Assert
        assertNotNull(result, "Collection skal ikke være null");
        assertEquals(1, result.getId(), "ID skal være 1");
    }

    // QE-77: Test at exception kastes hvis collection ikke findes//
    @Test
    @DisplayName("QE-77: Skal kaste exception hvis collection ikke findes")
    void testGetCollectionById_ThrowsExceptionWhenNotFound() {
        // Arrange
        when(collectionRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pickupService.getCollectionById(999);
        });

        assertTrue(exception.getMessage().contains("Afhentningen blev ikke fundet i systemet."),
                "Fejlbesked skal sige at afhentning ikke findes");
    }

    //Tests for Annullering funktionalitet//
    //QE-124: Test annulering af gyldig afhentning (Status = KLAR)

    @Test
    @DisplayName("QE-124: Annullering skal ændre status fra KLLAR til IKKE_KLAR")
    void testCancelPickup_ShouldChangeStatusToIkkeKlar_WhenStatusIsKlar() {
        //Arrange: Opret collection med status KLAR//
        Collection collection = new Collection();
        collection.setId(1);
        collection.setStatus(Status.KLAR);
        collection.setBusinessBags(5);
        collection.setDate(LocalDate.now());

        //Mock repository//
        when(collectionRepository.findById(1)).thenReturn(Optional.of(collection));
        when(collectionRepository.save(any(Collection.class))).thenReturn(collection);

        Collection result = pickupService.cancelPickup(1);

    }
}