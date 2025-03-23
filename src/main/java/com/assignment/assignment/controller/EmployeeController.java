package com.assignment.assignment.controller;

import com.assignment.assignment.Dto.DepartmentDTO;
import com.assignment.assignment.Dto.EmployeeDTO;
import com.assignment.assignment.Dto.ResponseFormat;
import com.assignment.assignment.model.Department;
import com.assignment.assignment.model.Employee;
import com.assignment.assignment.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
    @Autowired
    private EmployeeService service;

    @PostMapping("/create")
    public ResponseEntity<?> addEmployee(@RequestBody EmployeeDTO newEmployee) {
        ResponseEntity<?> createdEmployee = service.addEmployee(newEmployee);
        return ResponseEntity.ok(createdEmployee).getBody();
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<?> getEmployeesByDepartment(@PathVariable String departmentId) {
        return ResponseEntity.ok(service.getEmployeesByDepartment(departmentId).getBody());
    }
    @GetMapping("get/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable("id") String id){
        return ResponseEntity.ok(service.getEmployeeById(id)).getBody();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable String id) {
        if(service.deleteEmployee(id))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.notFound().build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateEmployee(
            @PathVariable String id,
            @RequestBody EmployeeDTO updatedEmployee
    ) {
        return ResponseEntity.ok(service.updateEmployee(id, updatedEmployee).getBody());
    }

    @GetMapping("/department/{departmentId}/{count}")
    public ResponseEntity<?> getLimitedEmployeesByDepartment(@PathVariable String departmentId,@PathVariable int count) {
        return service.getLimitedEmployeesByDepartment(departmentId, count);
    }

}
