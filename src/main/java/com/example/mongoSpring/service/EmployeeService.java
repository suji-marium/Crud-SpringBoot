package com.example.mongoSpring.service;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.mongoSpring.repository.EmployeeRepo;
import com.example.mongoSpring.model.*;

@Service
public class EmployeeService {
    @Autowired
    EmployeeRepo employeeRepo;

    public ResponseEntity<EmployeeResponseUpdate> addEmployee(EmployeeDetails employee){
        List<EmployeeDetails> employees=employeeRepo.findAll();
        //To check whether there is only 1 manager for a department
         if (employee.getDesignation().equalsIgnoreCase("Account Manager")) {
            for (EmployeeDetails emp : employees){
                if (emp.getDepartment().matches(employee.getDepartment())) {
                    EmployeeResponseUpdate employeeResponseUpdate=new EmployeeResponseUpdate("Department " + employee.getDepartment() + " already has a manager.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(employeeResponseUpdate);
                }
            }
        }

        //To check whether the adding employee have a manager or not
        List<EmployeeDetails> employeesOfDep=employeeRepo.findAllByDepartment(employee.getDepartment());
        if(employeesOfDep.isEmpty() && employee.getDesignation().matches("associate")){
            EmployeeResponseUpdate employeeResponseUpdate=new EmployeeResponseUpdate("Department " + employee.getDepartment() + " doesn't contain a manager.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(employeeResponseUpdate);
        }

        Date today = new Date();
        employee.setId(IdGenerator.generateId());
        employee.setCreatedTime(today);
        employee.setUpdatedTime(today);
        employeeRepo.save(employee);
        
        EmployeeResponseUpdate responseUpdate=new EmployeeResponseUpdate("Employee added successfully");
        return ResponseEntity.ok(responseUpdate);
    }


    public ResponseEntity<EmployeeResponseUpdate> deleteEmployee(String id) {
        //Check whether the employee is present or not
        if (employeeRepo.existsById(id)) {
            Optional<EmployeeDetails> employeeOpt = employeeRepo.findById(id);

            if (employeeOpt.isPresent()) {
                EmployeeDetails employee = employeeOpt.get();
                //checks if there are employees under the manager or not
                if (employee.getDesignation().matches("Account Manager")) {
                    if (employeeRepo.findAllByManagerId(id).isEmpty()) {
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


    public ResponseEntity<EmployeeResponseUpdate> updateEmployeeManager(UpdateEmployeeRequest request) {
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
        

    public ResponseEntity<EmployeeResponseGet> getFilteredEmployees(Integer yearOfExperience,String managerId) {
        List<EmployeeDetails> employees = employeeRepo.findAll();
        System.out.println(employees);

        //Creating a set with all manager id's
        Set<String> allManagerIds=new HashSet<>();
        for (EmployeeDetails employeeDetails : employees) {
            if ("Account Manager".equals(employeeDetails.getDesignation())) {
                allManagerIds.add(employeeDetails.getId());
            }
        }
        
        // group by managerid
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

                Optional<EmployeeDetails> managerOpt = employeeRepo.findById(currentManagerId);
                String managerName = managerOpt.map(EmployeeDetails::getName).orElse("Unknown");
                String managerDept = managerOpt.map(EmployeeDetails::getDepartment).orElse("Unknown");

                // Filter employees based on condition
                List<EmployeeResponseDTO> filteredEmployeeList = employeeList.stream()
                    .filter(employee -> {
                        LocalDateTime joiningDate = LocalDateTime.parse(employee.getDateOfJoining(), formatter);
                        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
                        int yearsOfExperienceCalculated = (int) ChronoUnit.YEARS.between(joiningDate, now);

                        return (managerId == null || managerId.equalsIgnoreCase(currentManagerId)) &&
                            (yearOfExperience == null || yearsOfExperienceCalculated >= yearOfExperience);
                    }).map(emp->new EmployeeResponseDTO(
                        emp.getId(),
                        emp.getName(),
                        emp.getDesignation(),
                        emp.getDepartment(),
                        emp.getEmail(),
                        emp.getMobile(),
                        emp.getLocation(),
                        emp.getDateOfJoining(),
                        emp.getCreatedTime(),
                        emp.getUpdatedTime()
                    ))
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
