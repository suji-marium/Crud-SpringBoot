package com.example.mongoSpring.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.Test;

public class EmployeeResponseTest {
    @Test
    void testLombokGettersAndSetters() {
        EmployeeResponseDTO employeeResponseDTO=new EmployeeResponseDTO();
        employeeResponseDTO.setId("10");
        employeeResponseDTO.setName("Jane");
        EmployeeResponse employeeResponse=new EmployeeResponse();
        employeeResponse.setId("1");
        employeeResponse.setAccountManager("John");
        employeeResponse.setDepartment("sales");
        employeeResponse.setEmployeeList(Collections.singletonList(employeeResponseDTO));

       assertEquals("1", employeeResponse.getId());
       assertEquals("John", employeeResponse.getAccountManager());
       assertEquals("sales", employeeResponse.getDepartment());
       assertEquals(1, employeeResponse.getEmployeeList().size());
       assertEquals(employeeResponseDTO, employeeResponse.getEmployeeList().get(0));
    }

    @Test 
    void testAllArgsConstructor(){
        EmployeeResponseDTO dto = new EmployeeResponseDTO(); // Assuming a basic constructor is available
        dto.setId("1");
        dto.setName("John Doe");

        EmployeeResponse response = new EmployeeResponse("Jane Smith", "sales", "100", Collections.singletonList(dto));

        assertEquals("Jane Smith", response.getAccountManager());
        assertEquals("sales", response.getDepartment());
        assertEquals("100", response.getId());
        assertEquals(1, response.getEmployeeList().size());
        assertEquals(dto, response.getEmployeeList().get(0));
    }

    @Test
    void testNoArgsConstructor() {
        EmployeeResponse response = new EmployeeResponse();
        response.setAccountManager("Jane Smith");
        response.setDepartment("sales");
        response.setId("100");

        assertEquals("Jane Smith", response.getAccountManager());
        assertEquals("sales", response.getDepartment());
        assertEquals("100", response.getId());
        assertNull(response.getEmployeeList()); // By default, employeeList should be null
    }
    
    @Test
    void testSetters() {
        EmployeeResponse response = new EmployeeResponse();
        response.setAccountManager("Jane Smith");
        response.setDepartment("sales");
        response.setId("100");
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId("1");
        dto.setName("John Doe");
        response.setEmployeeList(Collections.singletonList(dto));

        assertEquals("Jane Smith", response.getAccountManager());
        assertEquals("sales", response.getDepartment());
        assertEquals("100", response.getId());
        assertEquals(1, response.getEmployeeList().size());
        assertEquals(dto, response.getEmployeeList().get(0));
    }

    @Test
    void testEqualsAndHashCode() {
        EmployeeResponseDTO dto1 = new EmployeeResponseDTO();
        dto1.setId("1");
        dto1.setName("John Doe");

        EmployeeResponseDTO dto2 = new EmployeeResponseDTO();
        dto2.setId("1");
        dto2.setName("John Doe");

        EmployeeResponse response1 = new EmployeeResponse("Jane Smith", "sales", "100", Collections.singletonList(dto1));
        EmployeeResponse response2 = new EmployeeResponse("Jane Smith", "sales", "100", Collections.singletonList(dto2));

        // Test equality
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());

        // Test inequality
        response2.setId("101");
        assertNotEquals(response1, response2);
        assertNotEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testCanEqual() {
        EmployeeResponse response = new EmployeeResponse();
        assertTrue(response.canEqual(new EmployeeResponse()));
        assertFalse(response.canEqual(new Object()));
    }
}
