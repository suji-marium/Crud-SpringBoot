package com.example.mongoSpring.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmployeeResponseUpdateTest {

    @Test
    void testLombokGettersAndSetters() {
        // Create an instance of EmployeeResponseUpdate using the all-args constructor
        EmployeeResponseUpdate responseUpdate = new EmployeeResponseUpdate("Operation successful");

        // Test getter
        assertEquals("Operation successful", responseUpdate.getMessage());

        // Test setter
        responseUpdate.setMessage("Operation failed");
        assertEquals("Operation failed", responseUpdate.getMessage());
    }

    @Test
    void testAllArgsConstructor() {
        
        EmployeeResponseUpdate responseUpdate = new EmployeeResponseUpdate("Operation successful");

        assertEquals("Operation successful", responseUpdate.getMessage());
    }

    @Test
    void testNoArgsConstructor() {
        // Create an instance of EmployeeResponseUpdate using the no-args constructor
        EmployeeResponseUpdate responseUpdate = new EmployeeResponseUpdate();
        responseUpdate.setMessage("Operation successful");

        // Test field value
        assertEquals("Operation successful", responseUpdate.getMessage());
    }

    @Test
    void testSetters() {
        EmployeeResponseUpdate responseUpdate = new EmployeeResponseUpdate();
        responseUpdate.setMessage("Operation successful");

        assertEquals("Operation successful", responseUpdate.getMessage());
    }

    @Test
    void testToString() {
        EmployeeResponseUpdate responseUpdate = new EmployeeResponseUpdate("Operation successful");

        String expectedString = "EmployeeResponseUpdate(message=Operation successful)";
        assertEquals(expectedString, responseUpdate.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        // Create instances using the all-args constructor
        EmployeeResponseUpdate responseUpdate1 = new EmployeeResponseUpdate("Operation successful");
        EmployeeResponseUpdate responseUpdate2 = new EmployeeResponseUpdate("Operation successful");

        assertEquals(responseUpdate1, responseUpdate2);
        assertEquals(responseUpdate1.hashCode(), responseUpdate2.hashCode());

        responseUpdate2.setMessage("Operation failed");
        assertNotEquals(responseUpdate1, responseUpdate2);
    }

    @Test
    void testCanEqual() {
        EmployeeResponseUpdate responseUpdate1 = new EmployeeResponseUpdate("Operation successful");

        assertTrue(responseUpdate1.canEqual(new EmployeeResponseUpdate()));

        assertFalse(responseUpdate1.canEqual(new Object()));
    }
}
