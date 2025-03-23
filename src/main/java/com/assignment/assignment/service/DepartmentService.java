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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
//            if (repository.existsById(id)) {
                Department updated = repository.save(new Department(id, dto.getName()));
                DepartmentDTO updatedDto = new DepartmentDTO(updated.getId(), updated.getName());

                ResponseFormat<DepartmentDTO> response = new ResponseFormat<>(
                        LocalDateTime.now(),
                        "Department updated successfully",
                        updatedDto
                );
                return ResponseEntity.ok(response);
//            }
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

            if (employeeOpt.isEmpty()) {
                throw new UserNotFoundException("Employee not found with ID: " + employeeId);
            }

            Department department = employeeOpt.get().getDepartment();
            List<String> departmentNames = List.of(department.getName());

            ResponseFormat<List<String>> response = new ResponseFormat<>(
                    LocalDateTime.now(),
                    "Fetched " + departmentNames.size() + " departments for employee " + employeeId,
                    departmentNames
            );
        return ResponseEntity.ok(response);
        }catch (UserNotFoundException e){
            ErrorFormat err = new ErrorFormat(
                    LocalDateTime.now(),
                    "The User Id is invalid",
                    e.getMessage()
            );

            return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
        }

    }


//    public ResponseEntity<ResponseFormat<List<String>>> getDepartmentsByEmployee(String email, int limit) {
//        Optional<Employee> employeeOpt = employeeRepository.findByEmail(email);
//
//        if (employeeOpt.isEmpty()) {
//            throw new RuntimeException("Employee not found with email: " + email);
//        }
//
//        // Fetching the department of the employee
//        Employee employee = employeeOpt.get();
//        String departmentId = employee.getDepartment().getId(); // Fetch single department ID
//
//        // Since Employee has only one department, we fetch additional related departments
//        List<String> departmentNames = repository.findByEmployeeId(employee.getId()) // Custom Query
//                .stream()
//                .map(Department::getName)
//                .limit(limit)
//                .collect(Collectors.toList());
//
//        ResponseFormat<List<String>> response = new ResponseFormat<>(
//                LocalDateTime.now(),
//                "Departments fetched successfully for employee " + email,
//                departmentNames
//        );
//
//        return ResponseEntity.ok(response);
//    }

}
