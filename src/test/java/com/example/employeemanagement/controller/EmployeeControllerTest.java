package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.request.EmployeeRequestDto;
import com.example.employeemanagement.dto.response.EmployeeResponseDto;
import com.example.employeemanagement.dto.response.PagedResponse;
import com.example.employeemanagement.entity.EmployeeStatus;
import com.example.employeemanagement.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MockMvc integration tests for {@link EmployeeController}.
 *
 * {@code @WebMvcTest} loads only the web layer (controllers, filters) – no database.
 * Service beans are replaced by Mockito mocks.
 * {@code @WithMockUser} bypasses real authentication for testing controller logic.
 */
@WebMvcTest(EmployeeController.class)
@DisplayName("EmployeeController MockMvc Tests")
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    // Additional beans required by security config
    @MockBean private com.example.employeemanagement.security.JwtTokenProvider      jwtTokenProvider;
    @MockBean private com.example.employeemanagement.security.JwtAuthenticationFilter jwtFilter;
    @MockBean private com.example.employeemanagement.security.UserDetailsServiceImpl  userDetailsService;

    private EmployeeRequestDto  requestDto;
    private EmployeeResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = EmployeeRequestDto.builder()
                .firstName("Jane").lastName("Smith")
                .email("jane.smith@test.com")
                .phone("9876543210")
                .salary(new BigDecimal("75000.00"))
                .joiningDate(LocalDate.of(2023, 3, 1))
                .departmentId(1L).roleId(1L)
                .build();

        responseDto = EmployeeResponseDto.builder()
                .id(1L)
                .firstName("Jane").lastName("Smith")
                .fullName("Jane Smith")
                .email("jane.smith@test.com")
                .salary(new BigDecimal("75000.00"))
                .status(EmployeeStatus.ACTIVE)
                .departmentId(1L).departmentName("Engineering")
                .roleId(1L).roleName("Software Engineer")
                .build();
    }

    // ── POST /api/v1/employees ─────────────────────────────────────────────
    @Test
    @DisplayName("POST /employees – Admin can create employee (201)")
    @WithMockUser(roles = "ADMIN")
    void createEmployee_AsAdmin_Returns201() throws Exception {
        when(employeeService.createEmployee(any(EmployeeRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/employees")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("jane.smith@test.com"))
                .andExpect(jsonPath("$.data.fullName").value("Jane Smith"))
                .andExpect(jsonPath("$.message").value("Employee created successfully"));
    }

    @Test
    @DisplayName("POST /employees – User role gets 403 Forbidden")
    @WithMockUser(roles = "USER")
    void createEmployee_AsUser_Returns403() throws Exception {
        mockMvc.perform(post("/api/v1/employees")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /employees – Invalid request body returns 400")
    @WithMockUser(roles = "ADMIN")
    void createEmployee_InvalidBody_Returns400() throws Exception {
        EmployeeRequestDto invalid = EmployeeRequestDto.builder()
                .firstName("")          // blank – violates @NotBlank
                .email("not-an-email")  // invalid email
                .build();

        mockMvc.perform(post("/api/v1/employees")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400));
    }

    // ── GET /api/v1/employees ──────────────────────────────────────────────
    @Test
    @DisplayName("GET /employees – Returns paginated list (200)")
    @WithMockUser(roles = "USER")
    void getAllEmployees_Returns200() throws Exception {
        PagedResponse<EmployeeResponseDto> paged = PagedResponse.<EmployeeResponseDto>builder()
                .content(List.of(responseDto))
                .page(0).size(10).totalElements(1).totalPages(1)
                .first(true).last(true).empty(false)
                .build();

        when(employeeService.getAllEmployees(0, 10, "id", "asc")).thenReturn(paged);

        mockMvc.perform(get("/api/v1/employees")
                        .param("page",    "0")
                        .param("size",    "10")
                        .param("sortBy",  "id")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].email").value("jane.smith@test.com"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    // ── GET /api/v1/employees/{id} ─────────────────────────────────────────
    @Test
    @DisplayName("GET /employees/{id} – Returns employee (200)")
    @WithMockUser(roles = "USER")
    void getEmployeeById_Found_Returns200() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.email").value("jane.smith@test.com"));
    }

    // ── PUT /api/v1/employees/{id} ─────────────────────────────────────────
    @Test
    @DisplayName("PUT /employees/{id} – Admin updates employee (200)")
    @WithMockUser(roles = "ADMIN")
    void updateEmployee_AsAdmin_Returns200() throws Exception {
        when(employeeService.updateEmployee(eq(1L), any(EmployeeRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/employees/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ── DELETE /api/v1/employees/{id} ─────────────────────────────────────
    @Test
    @DisplayName("DELETE /employees/{id} – Admin soft-deletes (200)")
    @WithMockUser(roles = "ADMIN")
    void deleteEmployee_AsAdmin_Returns200() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/api/v1/employees/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Employee deleted successfully"));
    }

    // ── GET /api/v1/employees/search ──────────────────────────────────────
    @Test
    @DisplayName("GET /employees/search?name=Jane – Returns results (200)")
    @WithMockUser(roles = "USER")
    void searchByName_Returns200() throws Exception {
        PagedResponse<EmployeeResponseDto> paged = PagedResponse.<EmployeeResponseDto>builder()
                .content(List.of(responseDto)).page(0).size(10)
                .totalElements(1).totalPages(1).first(true).last(true).empty(false)
                .build();
        when(employeeService.searchByName("Jane", 0, 10)).thenReturn(paged);

        mockMvc.perform(get("/api/v1/employees/search")
                        .param("name", "Jane")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].firstName").value("Jane"));
    }
}
