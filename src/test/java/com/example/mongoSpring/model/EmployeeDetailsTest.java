package com.example.mongoSpring.model;

import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class EmployeeDetailsTest {
    @Test
    void testLombokGettersAndSetters() {
        EmployeeDetails employee = new EmployeeDetails();
        employee.setId("1");
        employee.setName("John Doe");
        employee.setDesignation("Account Manager");
        employee.setDepartment("sales");
        employee.setEmail("john.doe@example.com");
        employee.setMobile("1234567890");
        employee.setLocation("New York");
        employee.setManagerId("2");
        employee.setDateOfJoining("2024-08-14");
        employee.setCreatedTime(new Date());
        employee.setUpdatedTime(new Date());

        assertEquals("1", employee.getId());
        assertEquals("John Doe", employee.getName());
        assertEquals("Account Manager", employee.getDesignation());
        assertEquals("sales", employee.getDepartment());
        assertEquals("john.doe@example.com", employee.getEmail());
        assertEquals("1234567890", employee.getMobile());
        assertEquals("New York", employee.getLocation());
        assertEquals("2", employee.getManagerId());
        assertEquals("2024-08-14", employee.getDateOfJoining());
        assertNotNull(employee.getCreatedTime());
        assertNotNull(employee.getUpdatedTime());
    }

    @Test
    public void testEqualsAndHashCode() {
        EmployeeDetails emp1 = new EmployeeDetails("1", "John Doe", "Account Manager", "sales", "john.doe@example.com", "1234567890", "Location1", "manager1", "2022-01-01", new Date(), new Date());
        EmployeeDetails emp2 = new EmployeeDetails("1", "John Doe", "Account Manager", "sales", "john.doe@example.com", "1234567890", "Location1", "manager1", "2022-01-01", new Date(), new Date());
        EmployeeDetails emp3 = new EmployeeDetails("2", "Jane Doe", "associate", "delivery", "jane.doe@example.com", "0987654321", "Location2", "manager2", "2023-02-01", new Date(), new Date());

        // Test equality
        assertTrue(emp1.equals(emp2));
        assertFalse(emp1.equals(emp3));

        // Test hash codes
        assertEquals(emp1.hashCode(), emp2.hashCode());
        assertNotEquals(emp1.hashCode(), emp3.hashCode());
    }

    @Test
    public void testCanEqual() {
        EmployeeDetails emp = new EmployeeDetails();
        assertTrue(emp.canEqual(new EmployeeDetails()));
        assertFalse(emp.canEqual(new Object()));
    }
}
