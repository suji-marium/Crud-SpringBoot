package com.example.mongoSpring.controller;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.mongoSpring.model.EmployeeDetails;
import com.example.mongoSpring.model.UpdateEmployeeRequest;
import com.example.mongoSpring.repository.EmployeeRepo;
import com.example.mongoSpring.service.EmployeeService;
import com.example.mongoSpring.model.EmployeeResponseGet;
import com.example.mongoSpring.model.EmployeeResponseUpdate;

@RestController
@Validated
public class EmployeeController {


    @Autowired    
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService, EmployeeRepo employeeRepo) {
        this.employeeService = employeeService;
    }

    // endpoint for post operation
    @PostMapping("/addEmployee")
    public ResponseEntity<EmployeeResponseUpdate> addEmployee(@Valid @RequestBody EmployeeDetails employee) {
        return employeeService.addEmployee(employee);
    }

    // endpoint for delete operation
    @DeleteMapping("/delete")
    public ResponseEntity<EmployeeResponseUpdate> deleteEmployee(
        @RequestParam(value = "employeeId") String id) {
            return employeeService.deleteEmployee(id);
    }

    // endpoint for put operation
    @PutMapping("/update-manager")
    public ResponseEntity<EmployeeResponseUpdate> updateEmployeeManager(@RequestBody UpdateEmployeeRequest request) {
        return employeeService.updateEmployeeManager(request);
    }

    // endpoint for get operation
    @GetMapping("/viewEmployee")
    public ResponseEntity<EmployeeResponseGet> getFilteredEmployees(
        @RequestParam(value = "year-of-experience", required = false) Integer yearOfExperience,
        @RequestParam(value = "managerId", required = false) String managerId) {
                return employeeService.getFilteredEmployees(yearOfExperience, managerId);
    }

}