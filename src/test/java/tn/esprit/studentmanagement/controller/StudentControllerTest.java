package tn.esprit.studentmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.studentmanagement.controllers.StudentController;
import tn.esprit.studentmanagement.entities.Department;
import tn.esprit.studentmanagement.entities.Student;
import tn.esprit.studentmanagement.services.IStudentService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@DisplayName("StudentController - Tests REST (Spring Boot 3.4+)")
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Nouvelle annotation officielle (remplace @MockBean déprécié)
    @MockitoBean
    private IStudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    private Student createStudent(Long id, String firstName, String email) {
        Student s = new Student();
        s.setIdStudent(id);
        s.setFirstName(firstName);
        s.setLastName("Test");
        s.setEmail(email);
        s.setPhone("20123456");
        s.setDateOfBirth(LocalDate.of(2000, 5, 15));
        s.setAddress("Tunis");


        Department dept = new Department();
        dept.setIdDepartment(1L);
        dept.setName("Informatique");
        s.setDepartment(dept);

        return s;
    }

    @Test
    @DisplayName("GET /students/getAllStudents → 200 + liste")
    void shouldReturnAllStudents() throws Exception {
        List<Student> students = Arrays.asList(
                createStudent(1L, "Ali", "ali@esprit.tn"),
                createStudent(2L, "Sarra", "sarra@esprit.tn")
        );
        when(studentService.getAllStudents()).thenReturn(students);

        mockMvc.perform(get("/students/getAllStudents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Ali"))
                .andExpect(jsonPath("$[1].email").value("sarra@esprit.tn"));

        verify(studentService).getAllStudents();
    }

    @Test
    @DisplayName("GET /students/getStudent/{id} → 200 quand étudiant existe")
    void shouldReturnStudentWhenFound() throws Exception {
        Student student = createStudent(10L, "Mohamed", "mohamed@esprit.tn");
        when(studentService.getStudentById(10L)).thenReturn(student);

        mockMvc.perform(get("/students/getStudent/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idStudent").value(10))
                .andExpect(jsonPath("$.firstName").value("Mohamed"))
                .andExpect(jsonPath("$.email").value("mohamed@esprit.tn"))
                .andExpect(jsonPath("$.department.name").value("Informatique"));
    }

    @Test
    @DisplayName("GET /students/getStudent/{id} → 200 + null quand non trouvé (ton comportement actuel)")
    void shouldReturnNullWhenStudentNotFound() throws Exception {
        when(studentService.getStudentById(999L)).thenReturn(null);

        mockMvc.perform(get("/students/getStudent/999"))
                .andExpect(status().isOk())
                .andExpect(content().json("null")); // JSON null

        verify(studentService).getStudentById(999L);
    }

    @Test
    @DisplayName("POST /students/createStudent → 200 (ou 201 si tu ajoutes @ResponseStatus)")
    void shouldCreateStudent() throws Exception {
        Student toSave = createStudent(null, "Yassine", "yassine@esprit.tn");
        Student saved = createStudent(50L, "Yassine", "yassine@esprit.tn");

        when(studentService.saveStudent(any(Student.class))).thenReturn(saved);

        mockMvc.perform(post("/students/createStudent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toSave)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idStudent").value(50))
                .andExpect(jsonPath("$.firstName").value("Yassine"));

        verify(studentService).saveStudent(any(Student.class));
    }

    @Test
    @DisplayName("PUT /students/updateStudent → 200")
    void shouldUpdateStudent() throws Exception {
        Student updated = createStudent(5L, "Fatma", "fatma.updated@esprit.tn");
        updated.setPhone("98765432");

        when(studentService.saveStudent(any(Student.class))).thenReturn(updated);

        mockMvc.perform(put("/students/updateStudent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("98765432"))
                .andExpect(jsonPath("$.email").value("fatma.updated@esprit.tn"));
    }

    @Test
    @DisplayName("DELETE /students/deleteStudent/{id} → 200 (void method)")
    void shouldDeleteStudent() throws Exception {
        doNothing().when(studentService).deleteStudent(anyLong());

        mockMvc.perform(delete("/students/deleteStudent/25"))
                .andExpect(status().isOk());

        verify(studentService).deleteStudent(25L);
    }
}