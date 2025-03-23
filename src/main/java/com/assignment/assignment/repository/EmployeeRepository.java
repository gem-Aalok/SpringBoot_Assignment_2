package com.assignment.assignment.repository;

import com.assignment.assignment.model.Department;
import com.assignment.assignment.model.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends MongoRepository<Employee, String> {
//    List<Employee> findByDepartment_Id(String departmentId);
    List<Employee> findByDepartments_Id(String departmentId);
    Optional<Employee> findById(String id);
    Optional<Employee> findByEmail(String email);
//    Optional<Employee> findByEmail(String email);
List<Employee> findByDepartmentsContaining(Department department);

}
