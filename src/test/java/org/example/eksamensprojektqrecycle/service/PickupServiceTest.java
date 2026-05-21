package org.example.eksamensprojektqrecycle.service;

import org.example.eksamensprojektqrecycle.model.dto.CreateCollectionDTO;
import org.example.eksamensprojektqrecycle.model.dto.CreateUserDTO;
import org.example.eksamensprojektqrecycle.model.dto.UpdateCollectionStatusDTO;
import org.example.eksamensprojektqrecycle.model.entity.AppUser;
import org.example.eksamensprojektqrecycle.model.entity.Business;
import org.example.eksamensprojektqrecycle.model.entity.Collection;
import org.example.eksamensprojektqrecycle.model.enums.Status;
import org.example.eksamensprojektqrecycle.repository.BusinessRepository;
import org.example.eksamensprojektqrecycle.repository.CollectionRepository;
import org.example.eksamensprojektqrecycle.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//Unit/enhedstests for PickupService//
//QE-86, QE-87: Test status og antal pant poser opdatering//
//QE-124, QE-125: Test annullering af afhentninger//
class PickupServiceTest {

    // Mock repository (fake database)
    @Mock
    private CollectionRepository collectionRepository;

    @Mock  // ← add this
    private UserRepository userRepository;

    @Mock  // ← add this
    private BusinessRepository businessRepository;

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
        CreateCollectionDTO dto = new CreateCollectionDTO(5);

        Collection existingCollection = new Collection();
        existingCollection.setId(1);
        existingCollection.setStatus(Status.IKKE_KLAR); // Start status
        existingCollection.setBusinessBags(0);

        // Mock Authentication
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");

        // Stub user lookup
        AppUser mockUser = new AppUser();
        mockUser.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        // Stub business lookup
        var mockBusiness = new Business();
        when(businessRepository.findByAppUser(mockUser)).thenReturn(Optional.of(mockBusiness));

        // Stub the collection lookup used inside markReadyForPickup
        OngoingStubbing<Optional<Collection>> optionalOngoingStubbing = when(collectionRepository.findByBusinessAndStatusNot(eq(mockBusiness), eq(Status.AFHENTET)))
                .thenReturn(Optional.of(existingCollection));

        // Mock repository til at returnere collection
        when(collectionRepository.findById(1)).thenReturn(Optional.of(existingCollection));
        when(collectionRepository.save(any(Collection.class))).thenReturn(existingCollection);

        // Act (kør metoden vi tester)
        Collection result = pickupService.markReadyForPickup(dto, auth);

        // Assert (verificer resultat)
        assertEquals(Status.KLAR, result.getStatus(), "Status skal være KLAR");
        verify(collectionRepository, times(1)).save(any(Collection.class));
    }

    //QE-87: Test at antal poser gemmes korrekt//
    @Test
    @DisplayName("QE-87: businessBags skal gemmes korrekt")
    void testMarkReadyForPickup_BusinessBagsSavedCorrectly() {
        // Arrange
        CreateCollectionDTO dto = new CreateCollectionDTO(5);

        Collection existingCollection = new Collection();
        existingCollection.setId(1);
        existingCollection.setStatus(Status.IKKE_KLAR);
        existingCollection.setBusinessBags(0);

        // Mock Authentication
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");

        // Stub user lookup
        AppUser mockUser = new AppUser();
        mockUser.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        // Stub business lookup
        var mockBusiness = new Business();
        when(businessRepository.findByAppUser(mockUser)).thenReturn(Optional.of(mockBusiness));

        // Stub the collection lookup used inside markReadyForPickup
        OngoingStubbing<Optional<Collection>> optionalOngoingStubbing = when(collectionRepository.findByBusinessAndStatusNot(eq(mockBusiness), eq(Status.AFHENTET)))
                .thenReturn(Optional.of(existingCollection));


        when(collectionRepository.findById(1)).thenReturn(Optional.of(existingCollection));
        when(collectionRepository.save(any(Collection.class))).thenReturn(existingCollection);

        // Act
        Collection result = pickupService.markReadyForPickup(dto, auth);

        // Assert
        assertEquals(5, result.getBusinessBags(), "businessBags skal være 5");
        verify(collectionRepository, times(1)).save(any(Collection.class));
    }

    //QE-75: Test validering - skal kaste fejl hvis businessBags < 1 //
    @Test
    @DisplayName("QE-75: Skal kaste exception hvis businessBags er 0")
    void testMarkReadyForPickup_ThrowsExceptionWhenBagsIsZero() {
        // Arrange
        CreateCollectionDTO dto = new CreateCollectionDTO(0);

        // Mock Authentication
        Authentication auth = mock(Authentication.class);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pickupService.markReadyForPickup(dto, auth);
        });

        assertTrue(exception.getMessage().contains("mindst 1"),
                "Fejlbesked skal nævne mindst 1 pose");
    }

    //QE-86 + QE-87: Test komplet flow med både status og poser//
    @Test
    @DisplayName("QE-86 + QE-87: Kompllet opdatering af status og poser")
    void testMarkReadyForPickup_CompleteUpdate() {
        //Arrange
        CreateCollectionDTO dto = new CreateCollectionDTO(3);

        Collection existingCollection = new Collection();
        existingCollection.setId(1);
        existingCollection.setStatus(Status.IKKE_KLAR);
        existingCollection.setBusinessBags(0);

        // Mock Authentication
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");

        // Stub user lookup
        AppUser mockUser = new AppUser();
        mockUser.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        // Stub business lookup
        var mockBusiness = new Business();
        when(businessRepository.findByAppUser(mockUser)).thenReturn(Optional.of(mockBusiness));

        // Stub the collection lookup used inside markReadyForPickup
        OngoingStubbing<Optional<Collection>> optionalOngoingStubbing = when(collectionRepository.findByBusinessAndStatusNot(eq(mockBusiness), eq(Status.AFHENTET)))
                .thenReturn(Optional.of(existingCollection));


        when(collectionRepository.findById(1)).thenReturn(Optional.of(existingCollection));
        when(collectionRepository.save(any(Collection.class))).thenReturn(existingCollection);

        //Act//
        Collection result = pickupService.markReadyForPickup(dto, auth);

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

        //Mock repository//
        when(collectionRepository.findById(1)).thenReturn(Optional.of(collection));
        when(collectionRepository.save(any(Collection.class))).thenReturn(collection);

        //Act: Kald cancelPickup//
        Collection result = pickupService.cancelPickup(1);

        //Assert: Verificer at status ændres til IKKE_KLAR//
        assertEquals(Status.IKKE_KLAR, result.getStatus(),
                "Status skal være IKKE_KLAR efter annulering");

        // Verificer at businessBags forbliver uændret//
        assertEquals(5, result.getBusinessBags(),
                "BusinessBags skal forblive uændret (5)");

        //Verificer at save blev kaldt//
        verify(collectionRepository, times(1)).save(collection);

    }

    //QE-125: Test at afhentninger med status IKKE_KLAR ikke kan annulleres//
    @Test
    @DisplayName("QE-125: Skal kaste exception når status er IKKE_KLAR")
    void testCancelPickup_ShouldThrowException_WhenStatusIsIkkeKlar() {
        //Arrange: Opret collection med status IKKE_KLAR//
        Collection collection = new Collection();
        collection.setId(2);
        collection.setStatus(Status.IKKE_KLAR);
        collection.setBusinessBags(3);

        //Mock repository//
        when(collectionRepository.findById(2)).thenReturn(Optional.of(collection));

        //Act & Assert: Forvent Exception//
        RuntimeException exception = assertThrows(RuntimeException.class, () ->  {
            pickupService.cancelPickup(2);
        });

        //Verificer fejlbesked//
        assertTrue(exception.getMessage().contains("Kun"),
                "Fejlbesked skal forklare at kun KLAR kan annulleres");
        assertTrue(exception.getMessage().contains("IKKE_KLAR"),
                "Fejlbesked skal vise nuværende status");

        //Verificer at save IKKE blev kaldt//
        verify(collectionRepository,never()).save(any());
    }

    //QE-125: Test at afhentninger med status AFHENTET ikke kan annulleres//
    @Test
    @DisplayName("QE-125: skal kaste exception når status er AFHENTET")
    void testCancelPickup_ShouldThrowException_WhenStatusIsAfhentet() {
        //Arrange: Opret collection med status AFHENTET//
        Collection collection = new Collection();
        collection.setId(3);
        collection.setStatus(Status.AFHENTET);
        collection.setBusinessBags(4);
        collection.setDriverBags(4);

        //Mock repository//
        when(collectionRepository.findById(3)).thenReturn(Optional.of(collection));

        //Act & Assert: Forvent exception//
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pickupService.cancelPickup(3);
        });

        //Verificer fejlbesked//
        assertTrue(exception.getMessage().contains("Kun"),
                "Fejlbesked skal være tydelig");
        assertTrue(exception.getMessage().contains("AFHENTET"),
                "Fejlbesked skal vise nuværende status AFHENTET");

        //Verificer at save IKKE blev kaldt//
        verify(collectionRepository,never()).save(any());

    }

    @Test
    @DisplayName("Annullering skal kaste exception hvis collecion ikke findes")
    void testCancelPickup_ShouldThrowException_WhenCollectionNotFound() {
        //Arrange: Mock repository til at returnere tom Optional//
        when(collectionRepository.findById(999)).thenReturn(Optional.empty());

        //Act & assert: forvent exception//
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pickupService.cancelPickup(999);
        });

        //Verificr fejlbesked//
        assertTrue(exception.getMessage().contains("ikke fundet"),
                "Fejlbesked skal sige at afhentning ikke findes");


        //Verificer at save ikke blev kaldt//
        verify(collectionRepository, never()).save(any());
    }

    //Test at kun status æmndres ved annullering (ikke businessBags)
    @Test
    @DisplayName("Annullering skal kun ændre status, ikke andre felter")
    void testCancelPickup_ShouldOnlyChangeStatus_NotOtherFields() {
        //Arrange: Opret collection med flere felter//
        Collection collection = new Collection();
        collection.setId(4);
        collection.setStatus(Status.KLAR);
        collection.setBusinessBags(7);
        collection.setDriverBags(0);

        when(collectionRepository.findById(4)).thenReturn(Optional.of(collection));
        when(collectionRepository.save(any(Collection.class))).thenReturn(collection);

        //Act: Annuller//
        Collection result = pickupService.cancelPickup(4);

        //Assert: Verificer at kun status ændres//
        assertAll("Kun status skal ændres",
                ()  -> assertEquals(Status.IKKE_KLAR, result.getStatus(),
                        "Status skal ændres til IKKE_KLAR"),
                ()-> assertEquals(7,result.getBusinessBags(),
                        "BusinessBags skal forblive 7"),
                ()-> assertEquals(0,result.getDriverBags(),
                        "DriverBags skal forblive 0")
        );
    }

    //Test at save kaldes præcis én gang ved annullering//
    @Test
    @DisplayName("Save skal kaldes præcis én gang ved annullering")
    void testCancelPickup_ShouldCallSaveOnce() {
        //Arrange//
        Collection collection = new Collection();
        collection.setId(5);
        collection.setStatus(Status.KLAR);

        when(collectionRepository.findById(5)).thenReturn(Optional.of(collection));
        when(collectionRepository.save(any(Collection.class))).thenReturn(collection);

        //Act//
        pickupService.cancelPickup(5);

        //Assert: Verificer at save kaldes præcis én gang//
        ArgumentCaptor<Collection> captor = ArgumentCaptor.forClass(Collection.class);
        verify(collectionRepository,times(1)).save(captor.capture());

        //Verificer at den gamle collection har korrekt status//
        Collection savedCollection = captor.getValue();
        assertEquals(Status.IKKE_KLAR,savedCollection.getStatus(),
                "Gemt collection skal have status IKKE_KLAR");
    }
}