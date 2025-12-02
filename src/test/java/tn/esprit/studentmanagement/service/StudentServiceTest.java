package tn.esprit.studentmanagement.service;



import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.studentmanagement.entities.Department;
import tn.esprit.studentmanagement.entities.Student;
import tn.esprit.studentmanagement.repositories.StudentRepository;
import tn.esprit.studentmanagement.services.StudentService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudentService - Tests Unitaires")
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student student1;
    private Student student2;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {

        Department dept = new Department();
        dept.setIdDepartment(1L);
        dept.setName("Informatique");

        student1 = new Student();
        student1.setIdStudent(1L);
        student1.setFirstName("Ali");
        student1.setLastName("Ben Salem");
        student1.setEmail("ali.bensalem@esprit.tn");
        student1.setPhone("22123456");
        student1.setDateOfBirth(LocalDate.of(2000, 5, 15));
        student1.setDepartment(dept);

        student2 = new Student();
        student2.setIdStudent(2L);
        student2.setFirstName("Sarra");
        student2.setLastName("Trabelsi");
        student2.setEmail("sarra.trabelsi@esprit.tn");
        student2.setDateOfBirth(LocalDate.of(2001, 3, 22));
        student2.setDepartment(dept);
    }

    @Test
    @DisplayName("getAllStudents() - Retourne tous les étudiants")
    void shouldReturnAllStudents() {
        // Given
        List<Student> expectedList = Arrays.asList(student1, student2);
        when(studentRepository.findAll()).thenReturn(expectedList);

        // When
        List<Student> result = studentService.getAllStudents();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Ali", result.get(0).getFirstName());
        assertEquals("Sarra", result.get(1).getFirstName());
        verify(studentRepository).findAll();
    }

    @Test
    @DisplayName("getStudentById() - Étudiant existe")
    void shouldReturnStudentWhenExists() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));

        // When
        Student found = studentService.getStudentById(1L);

        // Then
        assertNotNull(found);
        assertEquals("Ali", found.getFirstName());
        assertEquals("ali.bensalem@esprit.tn", found.getEmail());
        verify(studentRepository).findById(1L);
    }

    @Test
    @DisplayName("getStudentById() - Étudiant n'existe pas → retourne null")
    void shouldReturnNullWhenStudentNotFound() {
        // Given
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Student found = studentService.getStudentById(999L);

        // Then
        assertNull(found);  // ← Comportement actuel de ton service
        verify(studentRepository).findById(999L);
    }

    @Test
    @DisplayName("saveStudent() - Création d'un nouvel étudiant")
    void shouldSaveNewStudent() {
        // Given
        Student newStudent = new Student();
        newStudent.setFirstName("Mohamed");
        newStudent.setLastName("Jlassi");
        newStudent.setEmail("mohamed.jlassi@esprit.tn");

        when(studentRepository.save(any(Student.class))).thenReturn(newStudent);

        // When
        Student saved = studentService.saveStudent(newStudent);

        // Then
        assertNotNull(saved);
        assertEquals("Mohamed", saved.getFirstName());
        verify(studentRepository).save(newStudent);
    }

    @Test
    @DisplayName("saveStudent() - Mise à jour d'un étudiant existant")
    void shouldUpdateExistingStudent() {
        // Given
        student1.setPhone("98765432"); // modification
        when(studentRepository.save(student1)).thenReturn(student1);

        // When
        Student updated = studentService.saveStudent(student1);

        // Then
        assertEquals("98765432", updated.getPhone());
        verify(studentRepository).save(student1);
    }

    @Test
    @DisplayName("deleteStudent() - Supprime un étudiant")
    void shouldDeleteStudent() {
        // Given - deleteById est void → on utilise doNothing()
        doNothing().when(studentRepository).deleteById(anyLong());

        // When
        studentService.deleteStudent(10L);

        // Then
        verify(studentRepository).deleteById(10L);
    }
}