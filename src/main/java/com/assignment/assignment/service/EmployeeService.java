package com.assignment.assignment.service;

import com.assignment.assignment.Dto.EmployeeDTO;
import com.assignment.assignment.Dto.ResponseFormat;
import com.assignment.assignment.exception.DepartmentNotFoundException;
import com.assignment.assignment.exception.ErrorFormat;
import com.assignment.assignment.exception.UserNotFoundException;
import com.assignment.assignment.model.Department;
import com.assignment.assignment.model.Employee;
import com.assignment.assignment.repository.DepartmentRepository;
import com.assignment.assignment.repository.EmployeeRepository;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
//import org.testng.Assert;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

public ResponseEntity<?> addEmployee(@RequestBody EmployeeDTO dto) {
    if (dto.getDepartmentIds() == null || dto.getDepartmentIds().isEmpty()) {
        return ResponseEntity.badRequest().body(new ErrorFormat(
                LocalDateTime.now(),
                "Department IDs cannot be empty",
                "Please provide at least one department ID."
        ));
    }
    List<Department> departments = departmentRepository.findAllById(dto.getDepartmentIds());
    if (departments.size() != dto.getDepartmentIds().size()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorFormat(
                LocalDateTime.now(),
                "One or more Department IDs are invalid",
                "Check if all provided department IDs exist in the database."
        ));
    }
    if (employeeRepository.findByEmail(dto.getEmail()).isPresent()) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorFormat(
                LocalDateTime.now(),
                "Duplicate Employee",
                "An employee with this email already exists."
        ));
    }
    Employee employee = new Employee(
            null, dto.getName(), dto.getDesignation(), dto.getEmail(),
            departments, dto.getSalary()
    );
    Employee savedEmployee = employeeRepository.save(employee);
    EmployeeDTO responseDto = new EmployeeDTO(
            savedEmployee.getId(), savedEmployee.getName(),
            savedEmployee.getDesignation(), savedEmployee.getEmail(),
            dto.getDepartmentIds(), savedEmployee.getSalary()
    );

    return ResponseEntity.ok(new ResponseFormat<>(
            LocalDateTime.now(), "Employee created successfully", responseDto));
}

    public ResponseEntity<?> getEmployeeById(String id){
        Optional<Employee> emp = employeeRepository.findById(id);
        try {
            if (emp.isEmpty()) {
                throw  new UserNotFoundException("Invalid user id" + id);
            }
        }
        catch (UserNotFoundException e){
            ErrorFormat err = new ErrorFormat(
                    LocalDateTime.now(),
                    "User Not found Id is invalid",
                    e.getMessage()
            );

            return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
        }


              ResponseFormat<Optional<Employee>> response = new ResponseFormat<Optional<Employee>>(
                    LocalDateTime.now(),
                    "employee fetched successfully",
                    emp
                    );

              return ResponseEntity.ok(response);

    }


    public ResponseEntity<?> getEmployeesByDepartment(String departmentId) {
        List<Employee> allEmployees = employeeRepository.findByDepartments_Id(departmentId);
        try{
            if(allEmployees.isEmpty()){
                throw new DepartmentNotFoundException("invalid department ID or No Employees in this department" + departmentId);
            }
        }
        catch (DepartmentNotFoundException e){
            ErrorFormat err = new ErrorFormat(
                    LocalDateTime.now(),
                    "invalid department ID or No Employees in this department",
                    e.getMessage()
            );

            return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
        }
        ResponseFormat<List<Employee>> empList = new ResponseFormat<>(
                LocalDateTime.now(),
                "All employees fetched successfully",
                allEmployees
        );

        return ResponseEntity.ok(empList);
    }

    public boolean deleteEmployee(String id) {

        boolean emp = employeeRepository.existsById(id);
        try {
            if(emp==false){
                throw new UserNotFoundException("User Id is invalid"+id);
            }
        }
        catch (UserNotFoundException e){
            ErrorFormat err = new ErrorFormat(
                    LocalDateTime.now(),
                    "The user id is invalid",
                    e.getMessage()
            );

            return new ResponseEntity<>(err, HttpStatus.NOT_FOUND).hasBody();
        }

        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            return true;
        }

        return false;
    }


    public ResponseEntity<?> updateEmployee(String id, EmployeeDTO dto) {

        Optional<Employee> existingEmployeeOpt = employeeRepository.findById(id);
            try {
        if (existingEmployeeOpt.isEmpty()) {
            throw new UserNotFoundException("Employee not found with ID: " + id);
        }

            }catch (UserNotFoundException e){
                ErrorFormat err = new ErrorFormat(
                        LocalDateTime.now(),
                        "The User Id is invalid",
                        e.getMessage()
                );

                return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
            }

        Employee existingEmployee = existingEmployeeOpt.get();
            List<Department> departments = existingEmployee.getDepartments();
        if (dto.getDepartmentIds() != null  && !dto.getDepartmentIds().isEmpty()) {
            List<Department> fetchedDepartments = departmentRepository.findAllById(dto.getDepartmentIds());
            try {
                if (fetchedDepartments.isEmpty()) {
                    throw new RuntimeException("Invalid Department ID: " + dto.getDepartmentIds());
                }
            }catch (DepartmentNotFoundException e){
                ErrorFormat err = new ErrorFormat(
                        LocalDateTime.now(),
                        "The department Id is invalid",
                        e.getMessage()
                );

                return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
            }
            departments = fetchedDepartments;

        }

        if (!existingEmployee.getEmail().equals(dto.getEmail()) && employeeRepository.findByEmail(dto.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ErrorFormat(
                    LocalDateTime.now(),
                    "Duplicate Email",
                    "Another employee with this email already exists."
            ));
        }
        existingEmployee.setName(dto.getName());
        existingEmployee.setDesignation(dto.getDesignation());
        existingEmployee.setEmail(dto.getEmail());
        existingEmployee.setSalary(dto.getSalary());
        existingEmployee.setDepartments(departments);

        Employee updatedEmployee = employeeRepository.save(existingEmployee);

        // Prepare response DTO
        EmployeeDTO updatedEmployeeDTO = new EmployeeDTO(
                updatedEmployee.getId(), updatedEmployee.getName(),
                updatedEmployee.getDesignation(), updatedEmployee.getEmail(),
                updatedEmployee.getDepartments().stream().map(Department::getId).toList(), // List of department IDs
                updatedEmployee.getSalary()
        );

        return ResponseEntity.ok(new ResponseFormat<>(LocalDateTime.now(), "Employee updated successfully", updatedEmployeeDTO));

    }

    public ResponseEntity<?> getLimitedEmployeesByDepartment(String departmentId, int count) {
        List<Employee> employees = employeeRepository.findByDepartments_Id(departmentId);
        try {
            if (employees.isEmpty()) {
                throw new DepartmentNotFoundException("Employee not found with ID: " + departmentId);
            }

        }catch (DepartmentNotFoundException e){
            ErrorFormat err = new ErrorFormat(
                    LocalDateTime.now(),
                    "The Department Id is invalid or no employees in the dpeartment",
                    e.getMessage()
            );

            return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
        }
        List<String> employeeNames = new ArrayList<>();
        for (int i = 0; i < Math.min(count, employees.size()); i++) {
            employeeNames.add(employees.get(i).getName());
        }

        ResponseFormat<List<String>> response = new ResponseFormat<>(
                LocalDateTime.now(),
                "Fetched " + employeeNames.size() + " employees from department " + departmentId,
                employeeNames
        );

        return ResponseEntity.ok(response);
    }
}
