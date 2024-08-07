package com.example.mongoSpring.controller;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.example.mongoSpring.model.EmployeeResponse;
import com.example.mongoSpring.model.EmployeeResponseGet;
import com.example.mongoSpring.model.EmployeeResponseUpdate;

@RestController
@Validated
public class EmployeeController {

    @Autowired
    private EmployeeRepo employeeRepo;

    @PostMapping("/addEmployee")
    public ResponseEntity<String> addEmployee(@Valid @RequestBody List<EmployeeDetails> employees) {
            employeeRepo.deleteAll();

            for (EmployeeDetails employee : employees) {
                Date today=new Date();
                employee.setCreatedTime(today);
                employeeRepo.save(employee);
            }
            
            return new ResponseEntity<>("Employee added successfully", HttpStatus.CREATED);
        
    }

    @DeleteMapping("/delete")
    public ResponseEntity<EmployeeResponseUpdate> deleteEmployee(
        @RequestParam(value = "employeeId") String id) {

        if (employeeRepo.existsById(id)) {
            Optional<EmployeeDetails> employeeOpt = employeeRepo.findById(id);

            if (employeeOpt.isPresent()) {
                EmployeeDetails employee = employeeOpt.get();
                if (employee.getDesignation().matches("Account Manager")) {
                    if (employeeRepo.employeeUnderManager(id) == null) {
                        employeeRepo.deleteById(id);
                        EmployeeResponseUpdate response = new EmployeeResponseUpdate(
                            "Successfully deleted " + employee.getName() + " from the employee list of the organization");
                        return ResponseEntity.ok(response);
                    } else {
                        EmployeeResponseUpdate response = new EmployeeResponseUpdate(
                            "Cannot delete employee because they manage other employees");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                    }
                } else {
                    employeeRepo.deleteById(id);
                    EmployeeResponseUpdate response = new EmployeeResponseUpdate(
                        "Successfully deleted " + employee.getName() + " from the employee list of the organization");
                    return ResponseEntity.ok(response);
                }
            } else {
                EmployeeResponseUpdate response = new EmployeeResponseUpdate("Employee not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } else {
            EmployeeResponseUpdate response = new EmployeeResponseUpdate("Employee not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/update-manager")
    public ResponseEntity<EmployeeResponseUpdate> updateEmployeeManager(@RequestBody UpdateEmployeeRequest request) {
    
    List<EmployeeDetails> ListemployeeDetails=employeeRepo.findAll();

    for(EmployeeDetails employeeDetails:ListemployeeDetails){
        if(employeeDetails.getManagerId().equals(request.getEmployeeId())){
            EmployeeResponseUpdate response = new EmployeeResponseUpdate("Manager id cannot be provided as employee id");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    Optional<EmployeeDetails> employeeOpt = employeeRepo.findById(request.getEmployeeId());
    if (!employeeOpt.isPresent()) {
        EmployeeResponseUpdate response = new EmployeeResponseUpdate("Employee not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    
    EmployeeDetails employee = employeeOpt.get();
    Date today=new Date();                   
    employee.setUpdatedTime(today);
    Optional<EmployeeDetails> oldManagerOpt = employeeRepo.findById(employee.getManagerId());
    Optional<EmployeeDetails> newManagerOpt = employeeRepo.findById(request.getManagerId());

    if (!newManagerOpt.isPresent()) {
        EmployeeResponseUpdate response = new EmployeeResponseUpdate("Manager not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    employee.setManagerId(request.getManagerId());
    employeeRepo.save(employee);

    String oldManagerName = oldManagerOpt.map(EmployeeDetails::getName).orElse("Unknown");
    String newManagerName = newManagerOpt.map(EmployeeDetails::getName).orElse("Unknown");
    String employeeName = employee.getName();
    String message = String.format("%s's manager has been successfully changed from %s to %s.",
                                   employeeName, oldManagerName, newManagerName);
   
    EmployeeResponseUpdate response = new EmployeeResponseUpdate(message);
    return ResponseEntity.ok(response);
}


    @GetMapping("/viewEmployee")
    public ResponseEntity<EmployeeResponseGet> getFilteredEmployees(
        @RequestParam(value = "year-of-experience", required = false) Integer yearOfExperience,
        @RequestParam(value = "managerId", required = false) String managerId) {

    
        List<EmployeeDetails> employees = employeeRepo.findAll();

        
        Map<String, List<EmployeeDetails>> employeesByManager = employees.stream()
            .collect(Collectors.groupingBy(EmployeeDetails::getManagerId));
        
        System.out.println(employeesByManager);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        List<EmployeeResponse> filteredResponses = employeesByManager.entrySet().stream()
            .map(entry -> {
                String currentManagerId = entry.getKey();
                List<EmployeeDetails> employeeList = entry.getValue();

                Optional<EmployeeDetails> managerOpt = employeeRepo.findById(currentManagerId);
                String managerNameOpt = managerOpt.map(EmployeeDetails::getName).orElse("Unknown");
                String managerDept = managerOpt.map(EmployeeDetails::getDepartment).orElse("Unknown");

                List<EmployeeDetails> filteredEmployeeList = employeeList.stream()
                    .filter(employee -> {
                        if (employee.getDateOfJoining() != null && !employee.getDateOfJoining().isEmpty()) {
                            try {
                                LocalDateTime joiningDate = LocalDateTime.parse(employee.getDateOfJoining(), formatter);
                                LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
                                int yearsOfExperienceCalculated = (int) ChronoUnit.YEARS.between(joiningDate, now);

                                return (managerId == null || managerId.equalsIgnoreCase(currentManagerId)) &&
                                    (yearOfExperience == null || yearsOfExperienceCalculated >= yearOfExperience);
                            } catch (Exception e) {
                                return false;
                            }
                        }
                        return false;
                    })
                    .collect(Collectors.toList());

                if (!filteredEmployeeList.isEmpty() && Integer.parseInt(currentManagerId) > 0) {
                    return new EmployeeResponse(
                        managerNameOpt,  
                        managerDept,     
                        currentManagerId, 
                        filteredEmployeeList  
                    );
                } else {
                    return null; 
                }
            })
            .filter(Objects::nonNull) 
            .collect(Collectors.toList());

        System.out.println(filteredResponses);
        String responseMessage = filteredResponses.isEmpty() ? "No employees found" : "Employees retrieved successfully";

        EmployeeResponseGet response = new EmployeeResponseGet(responseMessage, filteredResponses);

        return ResponseEntity.ok(response);
    }


}