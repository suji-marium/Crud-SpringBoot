package com.example.mongoSpring.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;

public class EmployeeResponseDTOTest {
    @Test
    void testLombokGettersAndSetters() {
        // Create an instance using the all-args constructor
        Date now = new Date();
        EmployeeResponseDTO dto = new EmployeeResponseDTO("1", "John Doe", "Account Manager", "sales",
                "john.doe@example.com", "1234567890", "New York", "2024-08-14", now, now);

        // Test getters
        assertEquals("1", dto.getId());
        assertEquals("John Doe", dto.getName());
        assertEquals("Account Manager", dto.getDesignation());
        assertEquals("sales", dto.getDepartment());
        assertEquals("john.doe@example.com", dto.getEmail());
        assertEquals("1234567890", dto.getMobile());
        assertEquals("New York", dto.getLocation());
        assertEquals("2024-08-14", dto.getDateOfJoining());
        assertEquals(now, dto.getCreatedTime());
        assertEquals(now, dto.getUpdatedTime());

        // Test setters
        dto.setId("2");
        dto.setName("Jane Doe");
        dto.setDesignation("Associate");
        dto.setDepartment("engineering");
        dto.setEmail("jane.doe@example.com");
        dto.setMobile("0987654321");
        dto.setLocation("San Francisco");
        dto.setDateOfJoining("2024-09-01");
        Date newDate = new Date();
        dto.setCreatedTime(newDate);
        dto.setUpdatedTime(newDate);

        assertEquals("2", dto.getId());
        assertEquals("Jane Doe", dto.getName());
        assertEquals("Associate", dto.getDesignation());
        assertEquals("engineering", dto.getDepartment());
        assertEquals("jane.doe@example.com", dto.getEmail());
        assertEquals("0987654321", dto.getMobile());
        assertEquals("San Francisco", dto.getLocation());
        assertEquals("2024-09-01", dto.getDateOfJoining());
        assertEquals(newDate, dto.getCreatedTime());
        assertEquals(newDate, dto.getUpdatedTime());
    }

    @Test
    void testAllArgsConstructor() {
        Date now = new Date();
        EmployeeResponseDTO dto = new EmployeeResponseDTO("1", "John Doe", "Account Manager", "sales",
                "john.doe@example.com", "1234567890", "New York", "2024-08-14", now, now);

        assertEquals("1", dto.getId());
        assertEquals("John Doe", dto.getName());
        assertEquals("Account Manager", dto.getDesignation());
        assertEquals("sales", dto.getDepartment());
        assertEquals("john.doe@example.com", dto.getEmail());
        assertEquals("1234567890", dto.getMobile());
        assertEquals("New York", dto.getLocation());
        assertEquals("2024-08-14", dto.getDateOfJoining());
        assertEquals(now, dto.getCreatedTime());
        assertEquals(now, dto.getUpdatedTime());
    }

    @Test
    void testNoArgsConstructor() {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId("1");
        dto.setName("John Doe");
        dto.setDesignation("Account Manager");
        dto.setDepartment("sales");
        dto.setEmail("john.doe@example.com");
        dto.setMobile("1234567890");
        dto.setLocation("New York");
        dto.setDateOfJoining("2024-08-14");
        Date now = new Date();
        dto.setCreatedTime(now);
        dto.setUpdatedTime(now);

        assertEquals("1", dto.getId());
        assertEquals("John Doe", dto.getName());
        assertEquals("Account Manager", dto.getDesignation());
        assertEquals("sales", dto.getDepartment());
        assertEquals("john.doe@example.com", dto.getEmail());
        assertEquals("1234567890", dto.getMobile());
        assertEquals("New York", dto.getLocation());
        assertEquals("2024-08-14", dto.getDateOfJoining());
        assertEquals(now, dto.getCreatedTime());
        assertEquals(now, dto.getUpdatedTime());
    }

    @Test
    void testSetters() {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId("1");
        dto.setName("John Doe");
        dto.setDesignation("Account Manager");
        dto.setDepartment("sales");
        dto.setEmail("john.doe@example.com");
        dto.setMobile("1234567890");
        dto.setLocation("New York");
        dto.setDateOfJoining("2024-08-14");
        Date now = new Date();
        dto.setCreatedTime(now);
        dto.setUpdatedTime(now);

        assertEquals("1", dto.getId());
        assertEquals("John Doe", dto.getName());
        assertEquals("Account Manager", dto.getDesignation());
        assertEquals("sales", dto.getDepartment());
        assertEquals("john.doe@example.com", dto.getEmail());
        assertEquals("1234567890", dto.getMobile());
        assertEquals("New York", dto.getLocation());
        assertEquals("2024-08-14", dto.getDateOfJoining());
        assertEquals(now, dto.getCreatedTime());
        assertEquals(now, dto.getUpdatedTime());
    }

    @Test
    void testEqualsAndHashCode() {
        Date now = new Date();
        EmployeeResponseDTO dto1 = new EmployeeResponseDTO("1", "John Doe", "Account Manager", "sales",
                "john.doe@example.com", "1234567890", "New York", "2024-08-14", now, now);

        EmployeeResponseDTO dto2 = new EmployeeResponseDTO("1", "John Doe", "Account Manager", "sales",
                "john.doe@example.com", "1234567890", "New York", "2024-08-14", now, now);

        // Test equality
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        // Test inequality
        dto2.setId("2");
        assertNotEquals(dto1, dto2);
    } 

    @Test
    void testCanEqual() {
        EmployeeResponseDTO dto1 = new EmployeeResponseDTO("1", "John Doe", "Account Manager", "sales",
                "john.doe@example.com", "1234567890", "New York", "2024-08-14", new Date(), new Date());

        // Test canEqual method with the same class
        assertTrue(dto1.canEqual(new EmployeeResponseDTO()));

        // Test canEqual method with a different class
        assertFalse(dto1.canEqual(new Object()));
    }

}
