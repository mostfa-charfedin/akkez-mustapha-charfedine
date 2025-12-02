package tn.esprit.studentmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.studentmanagement.controllers.DepartmentController;
import tn.esprit.studentmanagement.entities.Department;
import tn.esprit.studentmanagement.services.IDepartmentService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DepartmentController.class)  // Charge uniquement le controller + couche web
@DisplayName("DepartmentController - Tests d'API REST")
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;                     // Simule les requêtes HTTP

    @MockitoBean
    private IDepartmentService departmentService; // On mocke le service injecté

    @Autowired
    private ObjectMapper objectMapper;           // Pour convertir objet ↔ JSON

    private Department createSampleDepartment(Long id, String name) {
        Department dept = new Department();
        dept.setIdDepartment(id);
        dept.setName(name);
        dept.setLocation("Bloc A");
        dept.setPhone("71234567");
        dept.setHead("Dr. Karim");
        return dept;
    }

    @Test
    @DisplayName("GET /Depatment/getAllDepartment → 200 OK + liste")
    void shouldReturnAllDepartments() throws Exception {
        // Given
        List<Department> departments = Arrays.asList(
                createSampleDepartment(1L, "Informatique"),
                createSampleDepartment(2L, "Génie Civil")
        );
        when(departmentService.getAllDepartments()).thenReturn(departments);

        // When & Then
        mockMvc.perform(get("/Depatment/getAllDepartment"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Informatique"))
                .andExpect(jsonPath("$[1].name").value("Génie Civil"));

        verify(departmentService).getAllDepartments();
    }

    @Test
    @DisplayName("GET /Depatment/getDepartment/{id} → 200 OK quand existe")
    void shouldReturnDepartmentWhenFound() throws Exception {
        // Given
        Department dept = createSampleDepartment(1L, "Mathématiques");
        when(departmentService.getDepartmentById(1L)).thenReturn(dept);

        // When & Then
        mockMvc.perform(get("/Depatment/getDepartment/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDepartment").value(1))
                .andExpect(jsonPath("$.name").value("Mathématiques"))
                .andExpect(jsonPath("$.head").value("Dr. Karim"));

        verify(departmentService).getDepartmentById(1L);
    }

    @Test
    @DisplayName("GET /Depatment/getDepartment/{id} → 404 quand exception (not found)")
    void shouldReturn404WhenDepartmentNotFound() throws Exception {
        // Given
        when(departmentService.getDepartmentById(999L))
                .thenThrow(new jakarta.persistence.EntityNotFoundException("Department with id 999 not found"));

        // When & Then
        mockMvc.perform(get("/Depatment/getDepartment/999"))
                .andExpect(status().isNotFound()); // Spring transforme EntityNotFoundException → 404

        verify(departmentService).getDepartmentById(999L);
    }

    @Test
    @DisplayName("POST /Depatment/createDepartment → 201 Created (par défaut)")
    void shouldCreateDepartment() throws Exception {
        // Given
        Department newDept = createSampleDepartment(null, "Électronique");
        Department savedDept = createSampleDepartment(10L, "Électronique");

        when(departmentService.saveDepartment(any(Department.class))).thenReturn(savedDept);

        // When & Then
        mockMvc.perform(post("/Depatment/createDepartment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDept)))
                .andExpect(status().isOk())  // Tu n'as pas mis @ResponseStatus(CREATED), donc c'est 200
                .andExpect(jsonPath("$.idDepartment").value(10))
                .andExpect(jsonPath("$.name").value("Électronique"));

        verify(departmentService).saveDepartment(any(Department.class));
    }

    @Test
    @DisplayName("PUT /Depatment/updateDepartment → 200 OK")
    void shouldUpdateDepartment() throws Exception {
        // Given
        Department updatedDept = createSampleDepartment(5L, "Physique Mise à jour");
        when(departmentService.saveDepartment(any(Department.class))).thenReturn(updatedDept);

        // When & Then
        mockMvc.perform(put("/Depatment/updateDepartment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDept)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Physique Mise à jour"));

        verify(departmentService).saveDepartment(any(Department.class));
    }

    @Test
    @DisplayName("DELETE /Depatment/deleteDepartment/{id} → 200 OK (void → 200 par défaut)")
    void shouldDeleteDepartment() throws Exception {
        // Given
        doNothing().when(departmentService).deleteDepartment(anyLong());

        // When & Then
        mockMvc.perform(delete("/Depatment/deleteDepartment/3"))
                .andExpect(status().isOk()); // void → Spring retourne 200

        verify(departmentService).deleteDepartment(3L);
    }
}