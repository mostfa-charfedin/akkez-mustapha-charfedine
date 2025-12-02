package tn.esprit.studentmanagement.service;


import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.studentmanagement.entities.Enrollment;
import tn.esprit.studentmanagement.entities.Status;
import tn.esprit.studentmanagement.repositories.EnrollmentRepository;
import tn.esprit.studentmanagement.services.EnrollmentService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EnrollmentService - Tests Unitaires")
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private Enrollment enrollment1;
    private Enrollment enrollment2;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        enrollment1 = new Enrollment();
        enrollment1.setIdEnrollment(1L);
        enrollment1.setEnrollmentDate(LocalDate.of(2025, 9, 15));
        enrollment1.setGrade(14.5);
        enrollment1.setStatus(Status.ACTIVE);

        enrollment2 = new Enrollment();
        enrollment2.setIdEnrollment(2L);
        enrollment2.setEnrollmentDate(LocalDate.of(2025, 9, 20));
        enrollment2.setGrade(null);
        enrollment2.setStatus(Status.ACTIVE);
    }

    @Test
    @DisplayName("getAllEnrollments() - Retourne toutes les inscriptions")
    void shouldReturnAllEnrollments() {
        // Given
        List<Enrollment> expected = Arrays.asList(enrollment1, enrollment2);
        when(enrollmentRepository.findAll()).thenReturn(expected);

        // When
        List<Enrollment> result = enrollmentService.getAllEnrollments();

        // Then
        assertEquals(2, result.size());
        assertSame(enrollment1, result.get(0));
        verify(enrollmentRepository).findAll();
    }

    @Test
    @DisplayName("getEnrollmentById() - Inscription existe")
    void shouldReturnEnrollmentWhenFound() {
        // Given
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment1));

        // When
        Enrollment found = enrollmentService.getEnrollmentById(1L);

        // Then
        assertNotNull(found);
        assertEquals(1L, found.getIdEnrollment());
        assertEquals(14.5, found.getGrade());
        assertEquals(Status.ACTIVE, found.getStatus());
        verify(enrollmentRepository).findById(1L);
    }

    @Test
    @DisplayName("getEnrollmentById() - Inscription n'existe pas → Exception")
    void shouldThrowExceptionWhenEnrollmentNotFound() {
        // Given
        when(enrollmentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> enrollmentService.getEnrollmentById(999L)
        );

        assertTrue(exception.getMessage().contains("Enrolment with id 999 not found"));
        verify(enrollmentRepository).findById(999L);
    }

    @Test
    @DisplayName("saveEnrollment() - Sauvegarde une nouvelle inscription")
    void shouldSaveNewEnrollment() {
        // Given
        Enrollment newEnrollment = new Enrollment();
        newEnrollment.setEnrollmentDate(LocalDate.now());
        newEnrollment.setStatus(Status.ACTIVE);

        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(newEnrollment);

        // When
        Enrollment saved = enrollmentService.saveEnrollment(newEnrollment);

        // Then
        assertNotNull(saved);
        assertEquals(Status.ACTIVE, saved.getStatus());
        verify(enrollmentRepository).save(newEnrollment);
    }

    @Test
    @DisplayName("saveEnrollment() - Mise à jour d'une inscription existante")
    void shouldUpdateExistingEnrollment() {
        // Given - on simule la sauvegarde d'une entité déjà persistée
        Enrollment existing = new Enrollment();
        existing.setIdEnrollment(1L);
        existing.setGrade(16.0);
        existing.setStatus(Status.ACTIVE);

        when(enrollmentRepository.save(existing)).thenReturn(existing);

        // When
        Enrollment updated = enrollmentService.saveEnrollment(existing);

        // Then
        assertEquals(16.0, updated.getGrade());
        assertEquals(Status.ACTIVE, updated.getStatus());
        verify(enrollmentRepository).save(existing);
    }

    @Test
    @DisplayName("deleteEnrollment() - Supprime une inscription")
    void shouldDeleteEnrollment() {
        // Given
        doNothing().when(enrollmentRepository).deleteById(anyLong());

        // When
        enrollmentService.deleteEnrollment(5L);

        // Then
        verify(enrollmentRepository).deleteById(5L);
    }
}