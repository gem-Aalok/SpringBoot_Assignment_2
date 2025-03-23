package com.assignment.assignment.service;

import com.assignment.assignment.Dto.EmployeeDTO;
import com.assignment.assignment.Dto.ResponseFormat;
import com.assignment.assignment.exception.DepartmentNotFoundException;
import com.assignment.assignment.exception.DuplicateEntryException;
import com.assignment.assignment.exception.ErrorFormat;
import com.assignment.assignment.exception.UserNotFoundException;
import com.assignment.assignment.model.Department;
import com.assignment.assignment.model.Employee;
import com.assignment.assignment.repository.DepartmentRepository;
import com.assignment.assignment.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.testng.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    public ResponseEntity<?> addEmployee(EmployeeDTO dto) {

        Optional<Department> departmentOpt = departmentRepository.findById(dto.getDepartmentId());
        try {
        if (departmentOpt.isEmpty()) {
            throw new DepartmentNotFoundException("Invalid Department ID: " + dto.getDepartmentId());
        }
        } catch (DepartmentNotFoundException e){
            ErrorFormat err = new ErrorFormat(
              LocalDateTime.now(),
              "The department Id is invalid",
              e.getMessage()
            );

            return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
        }

        try {
        if (employeeRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new DuplicateEntryException("Employee with this email already exists: " + dto.getEmail());
        }
        } catch (DuplicateEntryException e) {
            ErrorFormat err = new ErrorFormat(
                    LocalDateTime.now(),
                    "The employee already exist in the same department",
                    e.getMessage()
            );
            return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
        }
        Employee employee = new Employee(
                null,
                dto.getName(),
                dto.getDesignation(),
                dto.getEmail(),
                departmentOpt.get(),
                dto.getSalary()
        );
        Employee savedEmployee = employeeRepository.save(employee);
        EmployeeDTO responseDto = new EmployeeDTO(
                savedEmployee.getId(),
                savedEmployee.getName(),
                savedEmployee.getDesignation(),
                savedEmployee.getEmail(),
                savedEmployee.getDepartment().getId(),
                savedEmployee.getSalary()
        );

        ResponseFormat<EmployeeDTO> response = new ResponseFormat<>(
                LocalDateTime.now(),
                "Employee created successfully",
                responseDto
        );

        return ResponseEntity.ok(response);
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
        List<Employee> allEmployees = employeeRepository.findByDepartment_Id(departmentId);
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
        Department department = existingEmployee.getDepartment();
        if (dto.getDepartmentId() != null) {
            Optional<Department> departmentOpt = departmentRepository.findById(dto.getDepartmentId());
            try {
                if (departmentOpt.isEmpty()) {
                    throw new RuntimeException("Invalid Department ID: " + dto.getDepartmentId());
                }
            }catch (DepartmentNotFoundException e){
                ErrorFormat err = new ErrorFormat(
                        LocalDateTime.now(),
                        "The department Id is invalid",
                        e.getMessage()
                );

                return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
            }
            department = departmentOpt.get();
        }
        existingEmployee.setName(dto.getName());
        existingEmployee.setDesignation(dto.getDesignation());
        existingEmployee.setEmail(dto.getEmail());
        existingEmployee.setSalary(dto.getSalary());
        existingEmployee.setDepartment(department);

        Employee updatedEmployee = employeeRepository.save(existingEmployee);

        EmployeeDTO updatedEmployeeDTO = new EmployeeDTO(
                updatedEmployee.getId(),
                updatedEmployee.getName(),
                updatedEmployee.getDesignation(),
                updatedEmployee.getEmail(),
                updatedEmployee.getDepartment().getId(),
                updatedEmployee.getSalary()
        );

        ResponseFormat<EmployeeDTO> response = new ResponseFormat<>(
                LocalDateTime.now(),
                "Employee updated successfully",
                updatedEmployeeDTO
        );

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> getLimitedEmployeesByDepartment(String departmentId, int count) {
        List<Employee> employees = employeeRepository.findByDepartment_Id(departmentId);
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
