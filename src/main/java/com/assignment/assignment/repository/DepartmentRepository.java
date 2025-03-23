package com.assignment.assignment.repository;

import com.assignment.assignment.Dto.DepartmentDTO;
import com.assignment.assignment.model.Department;
import com.assignment.assignment.model.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface DepartmentRepository extends MongoRepository<Department, String> {
//    List<Employee> findByDepartments_Id(String departmentId);
//List<Department> findByEmployeeId(String id);

}
