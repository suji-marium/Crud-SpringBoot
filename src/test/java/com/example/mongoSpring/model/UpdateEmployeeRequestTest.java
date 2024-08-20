package com.example.mongoSpring.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UpdateEmployeeRequestTest {

    @Test
    void testLombokGettersAndSetters() {
        // Create an instance using the all-args constructor
        UpdateEmployeeRequest request = new UpdateEmployeeRequest("emp123", "mgr456");

        // Test getter methods
        assertEquals("emp123", request.getEmployeeId());
        assertEquals("mgr456", request.getManagerId());

        // Test setter methods
        request.setEmployeeId("emp789");
        request.setManagerId("mgr012");

        assertEquals("emp789", request.getEmployeeId());
        assertEquals("mgr012", request.getManagerId());
    }

    @Test
    void testAllArgsConstructor() {
        // Create an instance using the all-args constructor
        UpdateEmployeeRequest request = new UpdateEmployeeRequest("emp123", "mgr456");

        // Test fields initialized by the constructor
        assertEquals("emp123", request.getEmployeeId());
        assertEquals("mgr456", request.getManagerId());
    }

    @Test
    void testNoArgsConstructor() {
        // Create an instance using the no-args constructor
        UpdateEmployeeRequest request = new UpdateEmployeeRequest();
        request.setEmployeeId("emp123");
        request.setManagerId("mgr456");

        // Test fields set after object creation
        assertEquals("emp123", request.getEmployeeId());
        assertEquals("mgr456", request.getManagerId());
    }

    @Test
    void testSetters() {
        // Create an instance using the no-args constructor
        UpdateEmployeeRequest request = new UpdateEmployeeRequest();
        request.setEmployeeId("emp123");
        request.setManagerId("mgr456");

        // Test setter methods
        assertEquals("emp123", request.getEmployeeId());
        assertEquals("mgr456", request.getManagerId());
    }

    @Test
    void testToString() {
        // Create an instance using the all-args constructor
        UpdateEmployeeRequest request = new UpdateEmployeeRequest("emp123", "mgr456");

        // Test toString
        String expectedString = "UpdateEmployeeRequest(employeeId=emp123, managerId=mgr456)";
        assertEquals(expectedString, request.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        // Create instances using the all-args constructor
        UpdateEmployeeRequest request1 = new UpdateEmployeeRequest("emp123", "mgr456");
        UpdateEmployeeRequest request2 = new UpdateEmployeeRequest("emp123", "mgr456");

        // Test equality
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());

        // Test inequality
        request2.setEmployeeId("emp789");
        assertNotEquals(request1, request2);
        assertNotEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testCanEqual() {
        // Create an instance using the all-args constructor
        UpdateEmployeeRequest request = new UpdateEmployeeRequest("emp123", "mgr456");

        // Test canEqual method with the same class
        assertTrue(request.canEqual(new UpdateEmployeeRequest("emp123", "mgr456")));

        // Test canEqual method with a different class
        assertFalse(request.canEqual(new Object()));
    }
}
