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
    Map<String, String> departmentToManagerId = new HashMap<>();

    @PostMapping("/addEmployee")
    public ResponseEntity<String> addEmployee(@Valid @RequestBody List<EmployeeDetails> employees) {
        employeeRepo.deleteAll();

        //To check whether the id is unique or not
        for (EmployeeDetails employee : employees) {
            if (employeeRepo.existsById(employee.getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("ID " + employee.getId() + " already exists.");
            }
           
        }

        //To check whether there is only 1 manager for a department
        for (EmployeeDetails employee : employees) {
            if (employee.getDesignation().equalsIgnoreCase("Account Manager")) {
                if (departmentToManagerId.containsKey(employee.getDepartment())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Department " + employee.getDepartment() + " already has a manager.");
                }
                departmentToManagerId.put(employee.getDepartment(), employee.getId());
            }
        }
        //System.out.println(departmentToManagerId);

    
        Date today = new Date();
        for (EmployeeDetails employee : employees) {
            employee.setCreatedTime(today);
            employeeRepo.save(employee);
        }

        return new ResponseEntity<>("Employee(s) added successfully", HttpStatus.CREATED);
}


    @DeleteMapping("/delete")
    public ResponseEntity<EmployeeResponseUpdate> deleteEmployee(
        @RequestParam(value = "employeeId") String id) {

        if (employeeRepo.existsById(id)) {
            Optional<EmployeeDetails> employeeOpt = employeeRepo.findById(id);

            if (employeeOpt.isPresent()) {
                //System.out.println(employeeRepo.employeeUnderManager(id));
                EmployeeDetails employee = employeeOpt.get();
                if (employee.getDesignation().matches("Account Manager")) {
                    if (employeeRepo.employeeUnderManager(id).isEmpty()) {
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
        //Manager id cannot be provided as employee id when to change the manager
        if(employeeDetails.getManagerId().equals(request.getEmployeeId())){
            EmployeeResponseUpdate response = new EmployeeResponseUpdate("Manager id cannot be provided as employee id");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    Optional<EmployeeDetails> employeeOpt = employeeRepo.findById(request.getEmployeeId());
    // check if employee is present
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
        System.out.println(employees);

        //Creating a set with managerId
        Set<String> allManagerIds=new HashSet<>();
        for (EmployeeDetails employeeDetails : employees) {
            if ("Account Manager".equals(employeeDetails.getDesignation())) {
                allManagerIds.add(employeeDetails.getId());
            }
        }
        
        Map<String, List<EmployeeDetails>> employeesByManager = employees.stream()
            .collect(Collectors.groupingBy(EmployeeDetails::getManagerId));
        
        
        for (String mngId : allManagerIds) {
                employeesByManager.putIfAbsent(mngId, new ArrayList<>());
        }
    
        //System.out.println(employeesByManager);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        // Create the filtered responses
        List<EmployeeResponse> filteredResponses = employeesByManager.entrySet().stream()
            .map(entry -> {
                String currentManagerId = entry.getKey();
                List<EmployeeDetails> employeeList = entry.getValue();

                // Get manager details
                Optional<EmployeeDetails> managerOpt = employeeRepo.findById(currentManagerId);
                String managerName = managerOpt.map(EmployeeDetails::getName).orElse("Unknown");
                String managerDept = managerOpt.map(EmployeeDetails::getDepartment).orElse("Unknown");

                // Filter employees
                List<EmployeeDetails> filteredEmployeeList = employeeList.stream()
                    .filter(employee -> {
                        LocalDateTime joiningDate = LocalDateTime.parse(employee.getDateOfJoining(), formatter);
                        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
                        int yearsOfExperienceCalculated = (int) ChronoUnit.YEARS.between(joiningDate, now);

                        return (managerId == null || managerId.equalsIgnoreCase(currentManagerId)) &&
                            (yearOfExperience == null || yearsOfExperienceCalculated >= yearOfExperience);
                    })
                    .collect(Collectors.toList());

                // Include the manager in the response if conditions are met
                if ((managerId == null || managerId.equalsIgnoreCase(currentManagerId)) && Integer.parseInt(currentManagerId)>0 &&
                    (yearOfExperience == null || employeeList.stream()
                        .anyMatch(employee -> {
                            LocalDateTime joiningDate = LocalDateTime.parse(employee.getDateOfJoining(), formatter);
                            LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
                            int yearsOfExperienceCalculated = (int) ChronoUnit.YEARS.between(joiningDate, now);
                            return yearsOfExperienceCalculated >= yearOfExperience;
                        }))) {
                    return new EmployeeResponse(
                        managerName,
                        managerDept,
                        currentManagerId,
                        filteredEmployeeList
                    );
                } else {
                    return null;
                }
            })
            .filter(Objects::nonNull) // Remove null responses
            .collect(Collectors.toList());

        System.out.println(filteredResponses);

    
        String responseMessage = filteredResponses.isEmpty() ? "No employees found" : "Successfully fetched";

    
        EmployeeResponseGet response = new EmployeeResponseGet(responseMessage, filteredResponses);
        return ResponseEntity.ok(response);
    }

}