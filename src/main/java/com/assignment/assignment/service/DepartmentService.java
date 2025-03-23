package com.assignment.assignment.service;

import com.assignment.assignment.Dto.DepartmentDTO;
import com.assignment.assignment.Dto.ResponseFormat;
import com.assignment.assignment.exception.CannotBeNullException;
import com.assignment.assignment.exception.DepartmentNotFoundException;
import com.assignment.assignment.exception.ErrorFormat;
import com.assignment.assignment.exception.UserNotFoundException;
import com.assignment.assignment.model.Department;
import com.assignment.assignment.model.Employee;
import com.assignment.assignment.repository.DepartmentRepository;
import com.assignment.assignment.repository.EmployeeRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import static java.util.Arrays.stream;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository repository;
    @Autowired
    private EmployeeRepository employeeRepository;

    public ResponseEntity<?> addDepartment(DepartmentDTO dto) {
        try {
            if (dto.getName() == null || dto.getName().trim().isEmpty()) {
                throw new CannotBeNullException("Department name cannot be empty or null");
            }
            Department department = new Department(null, dto.getName());
            Department saved = repository.save(department);
            DepartmentDTO responseDto = new DepartmentDTO(saved.getId(), saved.getName());

            ResponseFormat<DepartmentDTO> response = new ResponseFormat<>(
                    LocalDateTime.now(),
                    "Department created successfully",
                    responseDto
            );
            return ResponseEntity.ok(response);
        }catch (CannotBeNullException e) {
            ErrorFormat err = new ErrorFormat(
                    LocalDateTime.now(),
                    "Invalid Department Data",
                    e.getMessage()
            );

            return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> getDepartmentById(String id) {
        Optional<Department> departmentOptional = repository.findById(id);

        try {
            if (departmentOptional.isEmpty()) {
                throw new DepartmentNotFoundException("Invalid Department ID: " + id);
            }
        }catch (DepartmentNotFoundException e){
            ErrorFormat err = new ErrorFormat(
                    LocalDateTime.now(),
                    "The department Id is invalid",
                    e.getMessage()
            );

            return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
        }

        Department department = departmentOptional.get();
        DepartmentDTO departmentDTO = new DepartmentDTO(department.getId(), department.getName());

        ResponseFormat<DepartmentDTO> response = new ResponseFormat<>(
                LocalDateTime.now(),
                "Department fetched successfully",
                departmentDTO
        );

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> updateDepartment(String id, DepartmentDTO dto) {
        try {
            if (!repository.existsById(id)) {
                throw new DepartmentNotFoundException("Invalid Department ID: " + id);
            }
                Department updated = repository.save(new Department(id, dto.getName()));
                DepartmentDTO updatedDto = new DepartmentDTO(updated.getId(), updated.getName());

                ResponseFormat<DepartmentDTO> response = new ResponseFormat<>(
                        LocalDateTime.now(),
                        "Department updated successfully",
                        updatedDto
                );
                return ResponseEntity.ok(response);
        }catch (DepartmentNotFoundException e){
            ErrorFormat err = new ErrorFormat(
                    LocalDateTime.now(),
                    "The department Id is invalid",
                    e.getMessage()
            );

            return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> deleteDepartment(String id) {
        try {
            if (!repository.existsById(id)) {
                throw new DepartmentNotFoundException("Invalid Department ID: " + id);
            }
//        if (repository.existsById(id)) {
            repository.deleteById(id);

            ResponseFormat<String> response = new ResponseFormat<>(
                    LocalDateTime.now(),
                    "Department deleted successfully",
                    "Department with ID: " + id + " has been deleted"
            );
            return ResponseEntity.ok(response);
//        }
    }catch (DepartmentNotFoundException e){
            ErrorFormat err = new ErrorFormat(
                    LocalDateTime.now(),
                    "The department Id is invalid",
                    e.getMessage()
            );

            return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> getLimitedDepartmentsByEmployee(String employeeId, int count) {
        try {
            Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
            try {

                if (employeeOpt.isEmpty()) {
                    throw new UserNotFoundException("Employee not found with ID: " + employeeId);
                }
            }catch (UserNotFoundException e){
                ErrorFormat err = new ErrorFormat(
                        LocalDateTime.now(),
                        "Employee not found ",
                        e.getMessage()
                );

                return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
            }

            List<Department> departments = employeeOpt.get().getDepartments();
            try {
                if (departments.isEmpty()) {
                    throw new DepartmentNotFoundException("No departments found for employee ID: " + employeeId);
                }
            }catch (DepartmentNotFoundException e){
                ErrorFormat err = new ErrorFormat(
                        LocalDateTime.now(),
                        "No department found related to this employee",
                        e.getMessage()
                );
                return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
            }
            List<String> departmentNames = departments.stream()
                    .limit(Math.min(count, departments.size()))
                    .map(Department::getName)
                    .collect(Collectors.toList());

            ResponseFormat<List<String>> response = new ResponseFormat<>(
                    LocalDateTime.now(),
                    "Fetched " + departmentNames.size() + " departments for employee " + employeeId,
                    departmentNames
            );

            return ResponseEntity.ok(response);
        } catch (UserNotFoundException | DepartmentNotFoundException e) {
            ErrorFormat err = new ErrorFormat(
                    LocalDateTime.now(),
                    "Error fetching departments",
                    e.getMessage()
            );
            return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
        }

    }
    public ResponseEntity<?> getEmployeesByDepartment(String departmentId, int limit) {
        Optional<Department> departmentOpt = repository.findById(departmentId);
        try {

            if (departmentOpt.isEmpty()) {
                throw new DepartmentNotFoundException("Nodepartment found with this iD" );
            }
        }catch (DepartmentNotFoundException e){
            ErrorFormat err = new ErrorFormat(
                    LocalDateTime.now(),
                    "No Department found with this Id",
                    e.getMessage()
            );

            return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
        }
        List<Employee> employees = employeeRepository.findByDepartmentsContaining(departmentOpt.get());
        List<Map<String, String>> employeeNames = new ArrayList<>();
        int count = 0;
        for (Employee emp : employees) {
            if (count >= limit) break;
            Map<String, String> empData = new HashMap<>();
            empData.put("id", emp.getId());
            empData.put("name", emp.getName());
            employeeNames.add(empData);
            count++;
        }

        return ResponseEntity.ok(new ResponseFormat<>(LocalDateTime.now(), "Employees retrieved successfully", employeeNames));
    }



}
