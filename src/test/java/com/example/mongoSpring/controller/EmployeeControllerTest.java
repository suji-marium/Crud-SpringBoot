package com.example.mongoSpring.controller;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import com.example.mongoSpring.model.EmployeeDetails;
import com.example.mongoSpring.model.EmployeeResponse;
import com.example.mongoSpring.model.EmployeeResponseDTO;
import com.example.mongoSpring.model.EmployeeResponseGet;
import com.example.mongoSpring.model.EmployeeResponseUpdate;
import com.example.mongoSpring.model.UpdateEmployeeRequest;
import com.example.mongoSpring.repository.EmployeeRepo;
import com.example.mongoSpring.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private EmployeeRepo employeeRepo; // Mock the repository

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testAddEmployee() throws Exception {
        EmployeeDetails employee = new EmployeeDetails();
        employee.setId("1");
        employee.setName("John Doe");
        employee.setDesignation("Account Manager");
        employee.setDepartment("sales");

        EmployeeResponseUpdate response = new EmployeeResponseUpdate("Employee added successfully");

        when(employeeService.addEmployee(any(EmployeeDetails.class))).thenReturn(ResponseEntity.ok(response));

        mockMvc.perform(post("/addEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Employee added successfully"));
    }

    @Test
    void testUpdateEmployeeManager() throws Exception {
        // Create a request object for the update
        UpdateEmployeeRequest updateRequest = new UpdateEmployeeRequest();
        updateRequest.setEmployeeId("1");
        updateRequest.setManagerId("2");

        EmployeeResponseUpdate response = new EmployeeResponseUpdate("Manager updated successfully");

        // Mock the service method
        when(employeeService.updateEmployeeManager(any(UpdateEmployeeRequest.class)))
            .thenReturn(ResponseEntity.ok(response));

        mockMvc.perform(put("/update-manager")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Manager updated successfully"));
    }

    @Test
    void testDeleteEmployee() throws Exception {
        String employeeId = "1";
        EmployeeResponseUpdate response = new EmployeeResponseUpdate("Employee deleted successfully");

        // Mock the service method
        when(employeeService.deleteEmployee(employeeId))
            .thenReturn(ResponseEntity.ok(response));

        mockMvc.perform(delete("/delete")
                .param("employeeId", employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Employee deleted successfully"));
    }

    @Test
    void testGetFilteredEmployees() throws Exception {
        // Prepare mock response
        EmployeeResponseGet response = new EmployeeResponseGet();
        response.setMessage("Successfully fetched");

        EmployeeResponseDTO emp1 = new EmployeeResponseDTO(
            "3", "Sumi", "associate", "sales", "sumi@aspire.com",
            "1200990000", "Trivandrum", "2018-06-13T12:57:59.447+00:00",
            new Date(), new Date()
        );
        
        EmployeeResponseDTO emp2 = new EmployeeResponseDTO(
            "34", "Nibin", "Associate", "sales", "nibin123@aster.com",
            "7656789876", "Kochi", "2004-09-28T12:57:59.447+00:00",
            null, null
        );

        List<EmployeeResponseDTO> empList1 = List.of(emp1, emp2);

        EmployeeResponseDTO emp3 = new EmployeeResponseDTO(
            "4", "roshni", "associate", "delivery", "rosh@aspire.com",
            "0000009900", "Trivandrum", "2014-06-21T12:57:59.447+00:00",
            new Date(), new Date()
        );

        List<EmployeeResponseDTO> empList2 = Collections.singletonList(emp3);

        List<EmployeeResponse> details =List.of(
            new EmployeeResponse("ken", "sales", "1", empList1),
            new EmployeeResponse("rahul", "delivery", "2", empList2)
        );

        response.setDetails(details);

        when(employeeService.getFilteredEmployees(any(), any())).thenReturn(ResponseEntity.ok(response));

    
        mockMvc.perform(get("/viewEmployee")
                .param("year-of-experience", "5")  
                .param("managerId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Successfully fetched"))
                .andExpect(jsonPath("$.details[0].accountManager").value("ken"))
                .andExpect(jsonPath("$.details[0].employeeList[0].name").value("Sumi"))
                .andExpect(jsonPath("$.details[1].accountManager").value("rahul"))
                .andExpect(jsonPath("$.details[1].employeeList[0].name").value("roshni"));
    }

}
