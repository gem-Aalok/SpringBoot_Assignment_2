package com.assignment.assignment.repository;

import com.assignment.assignment.model.Department;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface DepartmentRepository extends MongoRepository<Department, String> {
//    List<Department> findByEmployeeId(String employeeId);
//List<Department> findByEmployeeId(String id);

}
