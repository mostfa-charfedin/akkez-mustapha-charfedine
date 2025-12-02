package tn.esprit.studentmanagement.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.studentmanagement.entities.Department;
import tn.esprit.studentmanagement.repositories.DepartmentRepository;
import tn.esprit.studentmanagement.services.DepartmentService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DepartmentService - Tests Unitaires")
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentService departmentService;

    private Department department1;
    private Department department2;

    @BeforeEach
    void setUp() {
        department1 = new Department();
        department1.setIdDepartment(1L);
        department1.setName("Informatique");
        department1.setLocation("Bloc A");
        department1.setPhone("71234567");
        department1.setHead("Dr. Ahmed");

        department2 = new Department();
        department2.setIdDepartment(2L);
        department2.setName("Génie Civil");
        department2.setLocation("Bloc B");
        department2.setPhone("71876543");
        department2.setHead("Prof. Fatma");
    }

    @Test
    @DisplayName("getAllDepartments() - Retourne la liste des départements")
    void shouldReturnAllDepartments() {
        // Given
        List<Department> expectedList = Arrays.asList(department1, department2);
        when(departmentRepository.findAll()).thenReturn(expectedList);

        // When
        List<Department> result = departmentService.getAllDepartments();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Informatique", result.get(0).getName());
        verify(departmentRepository).findAll();
    }

    @Test
    @DisplayName("getDepartmentById() - Département existe")
    void shouldReturnDepartmentWhenExists() {
        // Given
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department1));

        // When
        Department found = departmentService.getDepartmentById(1L);

        // Then
        assertNotNull(found);
        assertEquals("Informatique", found.getName());
        assertEquals(1L, found.getIdDepartment());
        verify(departmentRepository).findById(1L);
    }

    @Test
    @DisplayName("getDepartmentById() - Département n'existe pas → Exception")
    void shouldThrowExceptionWhenDepartmentNotFound() {
        // Given
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> departmentService.getDepartmentById(99L)
        );

        assertEquals("Department with id 99 not found", exception.getMessage());
        verify(departmentRepository).findById(99L);
    }

    @Test
    @DisplayName("saveDepartment() - Sauvegarde un nouveau département")
    void shouldSaveDepartment() {
        // Given
        Department newDept = new Department();
        newDept.setName("Mathématiques");
        when(departmentRepository.save(any(Department.class))).thenReturn(newDept);

        // When
        Department saved = departmentService.saveDepartment(newDept);

        // Then
        assertNotNull(saved);
        assertEquals("Mathématiques", saved.getName());
        verify(departmentRepository).save(newDept);
    }

    @Test
    @DisplayName("deleteDepartment() - Supprime un département")
    void shouldDeleteDepartment() {
        // Given - On mocke deleteById pour qu'il ne fasse rien (void)
        doNothing().when(departmentRepository).deleteById(anyLong());

        // When
        departmentService.deleteDepartment(1L);

        // Then
        verify(departmentRepository).deleteById(1L);
    }
}