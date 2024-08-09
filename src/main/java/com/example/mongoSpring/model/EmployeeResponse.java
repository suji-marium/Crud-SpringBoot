package com.example.mongoSpring.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {
    private String accountManager;
    private String department; 
    private String id; 
    private List<EmployeeResponseDTO> employeeList;
}
