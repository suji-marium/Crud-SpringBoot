package com.example.mongoSpring.repository;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

import com.example.mongoSpring.model.EmployeeDetails;

public interface EmployeeRepo extends MongoRepository<EmployeeDetails, String> {
    Optional<EmployeeDetails> findById(String id);

    void deleteById(String id);
    
    @Query("{managerId:'?0'}")
    public List<EmployeeDetails> employeeUnderManager(String managerId);
}
