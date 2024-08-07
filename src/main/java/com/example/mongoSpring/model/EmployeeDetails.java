package com.example.mongoSpring.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class EmployeeDetails {
    @Id
    private String id; 
    private String name;

    @Pattern(regexp = "Account Manager|associate", message = "Invalid designation")
    private String designation;

    @Pattern(regexp = "sales|delivery|QA|engineering|BA", message = "Invalid department")
    private String department;

    @Email(message = "Invalid email address")
    private String email;

    @Pattern(regexp = "\\d{10}", message = "Invalid mobile number")
    private String mobile;

    private String location;
    private String managerId;
    private String dateOfJoining;
    private Date createdTime;
    private Date updatedTime;
        
}