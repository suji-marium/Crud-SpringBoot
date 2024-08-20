package com.example.mongoSpring.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.mongoSpring.model.EmployeeDetails;

@SpringBootTest
public class EmployeeRepositoryTest {
    
    @Autowired
    private EmployeeRepo employeeRepo;
    @Test
    void testFindAllById_Found(){
        EmployeeDetails employeeDetails=new EmployeeDetails("34","Nibin","Associate","nibin123@aster.com","sales","7656789876","Kochi","1","2004-09-28T12:57:59.447+00:00", null, null);
        employeeRepo.save(employeeDetails);

        Optional<EmployeeDetails> found=employeeRepo.findById(employeeDetails.getId());
        assertEquals(employeeDetails.getId(), found.get().getId());
        assertEquals(employeeDetails.getName(), found.get().getName());

    }

    @Test
    void testFindAllById_NotFound(){
        Optional<EmployeeDetails> found=employeeRepo.findById("55");
        assertEquals(Optional.empty(), found);
    }
}
