package com.example.mongoSpring.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDTO {
    private String id; 
    private String name;
    private String designation;
    private String department;
    private String email;
    private String mobile;
    private String location;
    private String dateOfJoining;
    private Date createdTime;
    private Date updatedTime;
}
