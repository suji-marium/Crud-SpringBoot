package com.example.mongoSpring.model; 
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseGet {
    private String message;
    private List<EmployeeResponse> details;
}
