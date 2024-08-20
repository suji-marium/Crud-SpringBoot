package com.example.mongoSpring.model;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

class EmployeeResponseGetTest {

    @Test
    void testLombokGettersAndSetters() {
        // Create instances of EmployeeResponse
        EmployeeResponse response1 = new EmployeeResponse("Manager1", "sales", "1", Collections.emptyList());
        EmployeeResponse response2 = new EmployeeResponse("Manager2", "engineering", "2", Collections.emptyList());
        
        // Create an instance of EmployeeResponseGet
        EmployeeResponseGet responseGet = new EmployeeResponseGet();
        responseGet.setMessage("Success");
        responseGet.setDetails(Arrays.asList(response1, response2));
        
        // Test getters
        assertEquals("Success", responseGet.getMessage());
        assertEquals(2, responseGet.getDetails().size());
        assertEquals(response1, responseGet.getDetails().get(0));
        assertEquals(response2, responseGet.getDetails().get(1));
    }

    @Test
    void testAllArgsConstructor() {
        // Create instances of EmployeeResponse
        EmployeeResponse response1 = new EmployeeResponse("Manager1", "sales", "1", Collections.emptyList());
        EmployeeResponse response2 = new EmployeeResponse("Manager2", "engineering", "2", Collections.emptyList());
        
        // Create an instance of EmployeeResponseGet using all-args constructor
        EmployeeResponseGet responseGet = new EmployeeResponseGet("Success", Arrays.asList(response1, response2));
        
        // Test fields
        assertEquals("Success", responseGet.getMessage());
        assertEquals(2, responseGet.getDetails().size());
        assertEquals(response1, responseGet.getDetails().get(0));
        assertEquals(response2, responseGet.getDetails().get(1));
    }

    @Test
    void testNoArgsConstructor() {
        // Create an instance using no-args constructor
        EmployeeResponseGet responseGet = new EmployeeResponseGet();
        responseGet.setMessage("Success");
        responseGet.setDetails(Collections.singletonList(new EmployeeResponse("Manager1", "sales", "1", Collections.emptyList())));
        
        // Test fields
        assertEquals("Success", responseGet.getMessage());
        assertEquals(1, responseGet.getDetails().size());
        assertEquals("Manager1", responseGet.getDetails().get(0).getAccountManager());
    }

    @Test
    void testSetters() {
        // Create an instance using no-args constructor
        EmployeeResponseGet responseGet = new EmployeeResponseGet();
        responseGet.setMessage("Success");
        EmployeeResponse response = new EmployeeResponse("Manager1", "sales", "1", Collections.emptyList());
        responseGet.setDetails(Collections.singletonList(response));
        
        // Test setters
        assertEquals("Success", responseGet.getMessage());
        assertEquals(1, responseGet.getDetails().size());
        assertEquals(response, responseGet.getDetails().get(0));
    }

    @Test
    public void testEquals() {
        EmployeeResponseGet obj1 = new EmployeeResponseGet("Success", Collections.emptyList());
        EmployeeResponseGet obj2 = new EmployeeResponseGet("Success", Collections.emptyList());
        EmployeeResponseGet obj3 = new EmployeeResponseGet("Failure", Collections.emptyList());

        assertThat(obj1).isEqualTo(obj2);  // Same message and details
        assertThat(obj1).isNotEqualTo(obj3);  // Different message
        assertThat(obj1).isNotEqualTo(null);  // Not equal to null
        assertThat(obj1).isNotEqualTo(new Object());  // Not equal to object of different class
    }

    @Test
    public void testHashCode() {
        EmployeeResponseGet obj1 = new EmployeeResponseGet("Success", Collections.emptyList());
        EmployeeResponseGet obj2 = new EmployeeResponseGet("Success", Collections.emptyList());

        assertThat(obj1.hashCode()).isEqualTo(obj2.hashCode());  // Same content should have same hashCode
    }

    @Test
    public void testToString() {
        EmployeeResponseGet obj = new EmployeeResponseGet("Success", Collections.emptyList());

        assertThat(obj.toString()).isEqualTo("EmployeeResponseGet(message=Success, details=[])");
    }

    @Test
    public void testCanEqual() {
        EmployeeResponseGet obj = new EmployeeResponseGet();

        assertThat(obj.canEqual(new EmployeeResponseGet())).isTrue();  // Same class
        assertThat(obj.canEqual(new Object())).isFalse();  // Different class
    }
}
