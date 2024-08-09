package com.example.mongoSpring.repository;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

import com.example.mongoSpring.model.EmployeeDetails;


public interface EmployeeRepo extends MongoRepository<EmployeeDetails, String> {
    Optional<EmployeeDetails> findById(String id);

    void deleteById(String id);
    
    public List<EmployeeDetails> findAllByManagerId(String managerId);

    boolean existsById(String id);

    List<EmployeeDetails> findAllByDepartment(String department);
}