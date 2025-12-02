package tn.esprit.studentmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;


import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.studentmanagement.controllers.EnrollmentController;
import tn.esprit.studentmanagement.entities.Enrollment;
import tn.esprit.studentmanagement.entities.Status;
import tn.esprit.studentmanagement.services.IEnrollment;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EnrollmentController.class)
@DisplayName("EnrollmentController - Tests REST (Spring Boot 3.4+)")
class EnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // NOUVELLE ANNOTATION OFFICIELLE (depuis Spring Boot 3.4)
    @MockitoBean
    private IEnrollment enrollmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private Enrollment createEnrollment(Long id, Double grade, Status status) {
        Enrollment e = new Enrollment();
        e.setIdEnrollment(id);
        e.setEnrollmentDate(LocalDate.of(2025, 9, 15));
        e.setGrade(grade);
        e.setStatus(status);
        return e;
    }

    @Test
    @DisplayName("GET /Enrollment/getAllEnrollment → 200 + liste")
    void shouldReturnAllEnrollments() throws Exception {
        List<Enrollment> list = Arrays.asList(
                createEnrollment(1L, 14.5, Status.DROPPED),
                createEnrollment(2L, 18.0, Status.DROPPED)
        );
        when(enrollmentService.getAllEnrollments()).thenReturn(list);

        mockMvc.perform(get("/Enrollment/getAllEnrollment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].grade").value(14.5))
                .andExpect(jsonPath("$[1].status").value("VALIDEE"));

        verify(enrollmentService).getAllEnrollments();
    }

    @Test
    @DisplayName("GET /Enrollment/getEnrollment/{id} → 200 quand existe")
    void shouldReturnEnrollmentWhenFound() throws Exception {
        Enrollment e = createEnrollment(10L, 16.75, Status.DROPPED);
        when(enrollmentService.getEnrollmentById(10L)).thenReturn(e);

        mockMvc.perform(get("/Enrollment/getEnrollment/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEnrollment").value(10))
                .andExpect(jsonPath("$.grade").value(16.75))
                .andExpect(jsonPath("$.status").value("VALIDEE"));
    }

    @Test
    @DisplayName("GET /Enrollment/getEnrollment/{id} → 404 quand non trouvé")
    void shouldReturn404WhenNotFound() throws Exception {
        when(enrollmentService.getEnrollmentById(999L))
                .thenThrow(new EntityNotFoundException("Enrolment with id 999 not found"));

        mockMvc.perform(get("/Enrollment/getEnrollment/999"))
                .andExpect(status().isNotFound());

        verify(enrollmentService).getEnrollmentById(999L);
    }

    @Test
    @DisplayName("POST /Enrollment/createEnrollment → 200 (ou 201 si tu ajoutes @ResponseStatus)")
    void shouldCreateEnrollment() throws Exception {
        Enrollment toSave = createEnrollment(null, 12.0, Status.DROPPED);
        Enrollment saved = createEnrollment(50L, 12.0, Status.DROPPED);

        when(enrollmentService.saveEnrollment(any(Enrollment.class))).thenReturn(saved);

        mockMvc.perform(post("/Enrollment/createEnrollment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toSave)))
                .andExpect(status().isOk())  // car tu n'as pas @ResponseStatus(CREATED)
                .andExpect(jsonPath("$.idEnrollment").value(50))
                .andExpect(jsonPath("$.grade").value(12.0));

        verify(enrollmentService).saveEnrollment(any(Enrollment.class));
    }

    @Test
    @DisplayName("PUT /Enrollment/updateEnrollment → 200")
    void shouldUpdateEnrollment() throws Exception {
        Enrollment updated = createEnrollment(3L, 19.5, Status.DROPPED);

        when(enrollmentService.saveEnrollment(any(Enrollment.class))).thenReturn(updated);

        mockMvc.perform(put("/Enrollment/updateEnrollment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grade").value(19.5))
                .andExpect(jsonPath("$.status").value("VALIDEE"));
    }

    @Test
    @DisplayName("DELETE /Enrollment/deleteEnrollment/{id} → 200 (void method)")
    void shouldDeleteEnrollment() throws Exception {
        doNothing().when(enrollmentService).deleteEnrollment(anyLong());

        mockMvc.perform(delete("/Enrollment/deleteEnrollment/25"))
                .andExpect(status().isOk());

        verify(enrollmentService).deleteEnrollment(25L);
    }
}