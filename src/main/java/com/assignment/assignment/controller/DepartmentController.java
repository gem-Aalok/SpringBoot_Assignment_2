package com.assignment.assignment.controller;

import com.assignment.assignment.Dto.DepartmentDTO;
import com.assignment.assignment.Dto.ResponseFormat;
import com.assignment.assignment.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/departments")
public class DepartmentController {
    @Autowired
    private DepartmentService service;

    @PostMapping("/create")
    public ResponseEntity<?> addDepartment(@RequestBody DepartmentDTO dto) {
        return service.addDepartment(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDepartment(@PathVariable String id) {
        return service.getDepartmentById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDepartment(@PathVariable String id, @RequestBody DepartmentDTO dto) {
        return service.updateDepartment(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable String id) {
        return service.deleteDepartment(id);
    }


    //partially working rightnow
    @GetMapping("/employee/{employeeId}/{count}")
    public ResponseEntity<?> getLimitedDepartmentsByEmployee(
            @PathVariable String employeeId,
            @PathVariable int count) {
        return service.getLimitedDepartmentsByEmployee(employeeId, count);
    }

}
