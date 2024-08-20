package com.example.mongoSpring.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.mongoSpring.model.EmployeeDetails;
import com.example.mongoSpring.model.EmployeeResponse;
import com.example.mongoSpring.model.EmployeeResponseDTO;
import com.example.mongoSpring.model.EmployeeResponseGet;
import com.example.mongoSpring.model.EmployeeResponseUpdate;
import com.example.mongoSpring.model.UpdateEmployeeRequest;
import com.example.mongoSpring.repository.EmployeeRepo;

public class EmployeeServiceTest {
    @Mock
    private EmployeeRepo employeeRepo;

    @InjectMocks
    private EmployeeService employeeService;
    private DateTimeFormatter formatter;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        formatter = DateTimeFormatter.ISO_DATE_TIME;
    }

    @Test
    void testAddEmployee_Success(){
        EmployeeDetails emp=new EmployeeDetails();
        emp.setId("1");
        emp.setDesignation("Associate");
        emp.setDepartment("sales");

        when(employeeRepo.findAll()).thenReturn(Collections.emptyList());
        when(employeeRepo.findAllByDepartment("sales")).thenReturn(Collections.emptyList());
        //when(employeeRepo.save(emp)).thenReturn(emp);

        ResponseEntity<EmployeeResponseUpdate> response=employeeService.addEmployee(emp);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Employee added successfully", response.getBody().getMessage());
    }

    @Test
    void testAddEmployee_ManagerAlreadyExists(){
        EmployeeDetails emp=new EmployeeDetails();
        emp.setId("30");
        emp.setDesignation("Account Manager");
        emp.setDepartment("BA");

        EmployeeDetails existingManager=new EmployeeDetails();
        existingManager.setDepartment("BA");
        existingManager.setDesignation("Account Manager");

        when(employeeRepo.findAll()).thenReturn(Collections.singletonList(existingManager));
        ResponseEntity<EmployeeResponseUpdate> response=employeeService.addEmployee(emp);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Department BA already has a manager.", response.getBody().getMessage());
    }

    @Test
    void testAddEmployee_NoManagerInDepartment(){
        EmployeeDetails emp=new EmployeeDetails();
        emp.setId("34");
        emp.setDesignation("associate");
        emp.setDepartment("BA");

        when(employeeRepo.findAll()).thenReturn(Collections.emptyList());
        when(employeeRepo.findAllByDepartment("BA")).thenReturn(Collections.emptyList());

        ResponseEntity<EmployeeResponseUpdate> response=employeeService.addEmployee(emp);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Department BA doesn't contain a manager.", response.getBody().getMessage());
    }

    @Test
    void testDeleteEmployee_Success(){
        String empId="1";
        EmployeeDetails emp=new EmployeeDetails();
        emp.setId(empId);
        emp.setDesignation("associate");

        when(employeeRepo.existsById(empId)).thenReturn(true);
        when(employeeRepo.findById(empId)).thenReturn(Optional.of(emp));
        when(employeeRepo.findAllByManagerId(empId)).thenReturn(Collections.emptyList());

        ResponseEntity<EmployeeResponseUpdate> response=employeeService.deleteEmployee(empId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully deleted "+emp.getName()+ " from the employee list of the organization",response.getBody().getMessage());
    }

    @Test
    void testDeleteEmployee_ManagerHasSubordinates(){
        String empId="12";
        EmployeeDetails emp=new EmployeeDetails();
        emp.setId(empId);
        emp.setDesignation("Account Manager");

        EmployeeDetails subordinate=new EmployeeDetails();
        subordinate.setManagerId(empId);

        when(employeeRepo.existsById(empId)).thenReturn(true);
        when(employeeRepo.findById(empId)).thenReturn(Optional.of(emp));
        when(employeeRepo.findAllByManagerId(empId)).thenReturn(Collections.singletonList(subordinate));

        ResponseEntity<EmployeeResponseUpdate> response=employeeService.deleteEmployee(empId);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Cannot delete employee because they manage other employees", response.getBody().getMessage());
    }

    @Test
    void testDeleteEmployee_EmployeeExistsAndNotAccountManager() {
        String employeeId = "1";
        EmployeeDetails employee = new EmployeeDetails();
        employee.setId(employeeId);
        employee.setDesignation("associate");
        employee.setName("John Doe");

        when(employeeRepo.existsById(employeeId)).thenReturn(true);
        when(employeeRepo.findById(employeeId)).thenReturn(Optional.of(employee));

        when(employeeRepo.findAllByManagerId(employeeId)).thenReturn(Collections.emptyList());

        ResponseEntity<EmployeeResponseUpdate> response = employeeService.deleteEmployee(employeeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully deleted John Doe from the employee list of the organization", response.getBody().getMessage());
        verify(employeeRepo).deleteById(employeeId);
    }
    
    @Test
    void testDeleteAccountManager_WithNoSubordinates() {
        String managerId = "1";
        EmployeeDetails manager = new EmployeeDetails();
        manager.setId(managerId);
        manager.setName("Manager");
        manager.setDesignation("Account Manager");
        
        when(employeeRepo.existsById(managerId)).thenReturn(true);
        when(employeeRepo.findById(managerId)).thenReturn(Optional.of(manager));
        when(employeeRepo.findAllByManagerId(managerId)).thenReturn(Collections.emptyList());

        ResponseEntity<EmployeeResponseUpdate> response = employeeService.deleteEmployee(managerId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully deleted Manager from the employee list of the organization", response.getBody().getMessage());
        verify(employeeRepo, times(1)).deleteById(managerId);
    }
    @Test
    void testDeleteEmployee_EmployeeNotFound() {
        String nonExistentId = "999";
        when(employeeRepo.existsById(nonExistentId)).thenReturn(false);
        ResponseEntity<EmployeeResponseUpdate> response = employeeService.deleteEmployee(nonExistentId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Employee not found", response.getBody().getMessage());
        verify(employeeRepo, times(0)).findById(nonExistentId);
        verify(employeeRepo, times(0)).deleteById(nonExistentId);
    }

    @Test
    void testDeleteEmployee_EmployeeExistsButNotFound() {
        String existingId = "1";
        when(employeeRepo.existsById(existingId)).thenReturn(true);
        when(employeeRepo.findById(existingId)).thenReturn(Optional.empty());
        ResponseEntity<EmployeeResponseUpdate> response = employeeService.deleteEmployee(existingId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Employee not found", response.getBody().getMessage());
        verify(employeeRepo, times(1)).findById(existingId);
        verify(employeeRepo, times(0)).deleteById(existingId);
    }

    @Test
    void testUpdateEmployeeManager_Success(){
        String empId="1";
        String newManagerId="2";
        EmployeeDetails emp=new EmployeeDetails();
        emp.setId(empId);
        emp.setManagerId("3");

        EmployeeDetails newManager=new EmployeeDetails();
        newManager.setId(newManagerId);
        newManager.setName("New Manager");

        when(employeeRepo.findById(empId)).thenReturn(Optional.of(emp));
        when(employeeRepo.findById(newManagerId)).thenReturn(Optional.of(newManager));
        //when(employeeRepo.save(emp)).thenReturn(emp);

        UpdateEmployeeRequest request=new UpdateEmployeeRequest();
        request.setEmployeeId(empId);
        request.setManagerId(newManagerId);

        ResponseEntity<EmployeeResponseUpdate> response=employeeService.updateEmployeeManager(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("successfully changed"));
    }

    @Test 
    void testUpdateEmployeeManager_ManagerNotFound(){
        String empId="1";
        String newManagerId="2";
        EmployeeDetails employeeDetails=new EmployeeDetails();
        employeeDetails.setId(empId);
        employeeDetails.setManagerId("3");

        when(employeeRepo.findById(empId)).thenReturn(Optional.of(employeeDetails));
        when(employeeRepo.findById(newManagerId)).thenReturn(Optional.empty());

        UpdateEmployeeRequest request=new UpdateEmployeeRequest();
        request.setEmployeeId(empId);
        request.setManagerId(newManagerId);

        ResponseEntity<EmployeeResponseUpdate> response=employeeService.updateEmployeeManager(request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Manager not found", response.getBody().getMessage());
    }

    @Test
    void testUpdateEmployeeManager_ManagerIdProvidedAsEmployeeId() {

        String employeeId = "1";
        String managerId = "2";
        
        EmployeeDetails manager = new EmployeeDetails();
        manager.setId(managerId);
        manager.setManagerId(employeeId);  // This will simulate the error condition
        
        EmployeeDetails employee = new EmployeeDetails();
        employee.setId(employeeId);
        
        UpdateEmployeeRequest request = new UpdateEmployeeRequest();
        request.setEmployeeId(employeeId);
        request.setManagerId(managerId);
        
        when(employeeRepo.findAll()).thenReturn(Collections.singletonList(manager));
        when(employeeRepo.findById(employeeId)).thenReturn(Optional.of(employee));
        when(employeeRepo.findById(managerId)).thenReturn(Optional.of(manager));

        ResponseEntity<EmployeeResponseUpdate> response = employeeService.updateEmployeeManager(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Manager id cannot be provided as employee id", response.getBody().getMessage());
    }

    @Test
    void testUpdateEmployeeManager_EmployeeNotFound() {
        UpdateEmployeeRequest updateRequest = new UpdateEmployeeRequest();
        updateRequest.setEmployeeId("1");
        updateRequest.setManagerId("2");
        when(employeeRepo.findById(updateRequest.getEmployeeId())).thenReturn(Optional.empty());
        ResponseEntity<EmployeeResponseUpdate> response = employeeService.updateEmployeeManager(updateRequest);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Employee not found", response.getBody().getMessage());
        verify(employeeRepo, times(1)).findById(updateRequest.getEmployeeId());
        verify(employeeRepo, times(0)).save(any());
    }

    @Test
    void testGetFilteredEmployees_NoResults(){
        when(employeeRepo.findAll()).thenReturn(Collections.emptyList());
        ResponseEntity<EmployeeResponseGet> response=employeeService.getFilteredEmployees(null, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("No employees found", response.getBody().getMessage());
        assertTrue(response.getBody().getDetails().isEmpty());

    }

    @Test
    void testGetFilteredEmployees_WithResults(){
        String managerId="1";
        EmployeeDetails manager=new EmployeeDetails();
        manager.setId(managerId);
        manager.setName("Manager");
        manager.setDepartment("sales");
        manager.setDesignation("Account Manager");

        EmployeeDetails employee=new EmployeeDetails();
        employee.setId("2");
        employee.setName("Employee");
        employee.setDesignation("associate");
        employee.setDepartment("sales");
        employee.setManagerId(managerId);
        employee.setDateOfJoining(LocalDateTime.now().minusYears(5).toString());

        when(employeeRepo.findAll()).thenReturn(List.of(employee,manager));
        when(employeeRepo.findById(managerId)).thenReturn(Optional.of(manager));

        ResponseEntity<EmployeeResponseGet> response=employeeService.getFilteredEmployees(1,managerId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully fetched", response.getBody().getMessage());
        assertEquals(1, response.getBody().getDetails().size());

        EmployeeResponse employeeResponse = response.getBody().getDetails().get(0);
        assertEquals("Manager", employeeResponse.getAccountManager());
        assertEquals("sales", employeeResponse.getDepartment());
        assertEquals("1", employeeResponse.getId());
        assertEquals(1, employeeResponse.getEmployeeList().size());

        EmployeeResponseDTO employeeResponseDTO = employeeResponse.getEmployeeList().get(0);
        assertEquals("2", employeeResponseDTO.getId());
        assertEquals("Employee", employeeResponseDTO.getName());
        assertEquals("associate", employeeResponseDTO.getDesignation());
        assertEquals("sales", employeeResponseDTO.getDepartment());
    }

    @Test
    void testGetFilteredEmployees_FilterByExperience() {
        String managerId = "1";
        String department = "sales";
        
        EmployeeDetails manager = new EmployeeDetails();
        manager.setId(managerId);
        manager.setName("Manager");
        manager.setDepartment(department);
        manager.setDesignation("Account Manager");

        EmployeeDetails employee1 = new EmployeeDetails();
        employee1.setId("2");
        employee1.setName("Employee1");
        employee1.setDesignation("associate");
        employee1.setDepartment(department);
        employee1.setDateOfJoining(LocalDateTime.now().minusYears(3).format(formatter)); // 3 years experience
        employee1.setManagerId(managerId);

        EmployeeDetails employee2 = new EmployeeDetails();
        employee2.setId("3");
        employee2.setName("Employee2");
        employee2.setDesignation("associate");
        employee2.setDepartment(department);
        employee2.setDateOfJoining(LocalDateTime.now().minusYears(1).format(formatter)); // 1 year experience
        employee2.setManagerId(managerId);
 
        when(employeeRepo.findAll()).thenReturn(List.of(manager, employee1, employee2));
        when(employeeRepo.findById(managerId)).thenReturn(Optional.of(manager));

        // Act
        ResponseEntity<EmployeeResponseGet> response = employeeService.getFilteredEmployees(2, managerId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully fetched", response.getBody().getMessage());
        assertNotNull(response.getBody().getDetails());
        assertEquals(1, response.getBody().getDetails().size()); // Only the manager with valid employees should be included

        EmployeeResponse employeeResponse = response.getBody().getDetails().get(0);
        List<EmployeeResponseDTO> employeeResponseDTOs = employeeResponse.getEmployeeList();
        assertEquals(1, employeeResponseDTOs.size());
        assertEquals("Employee1", employeeResponseDTOs.get(0).getName()); // Only Employee1 should be included
    }
}
