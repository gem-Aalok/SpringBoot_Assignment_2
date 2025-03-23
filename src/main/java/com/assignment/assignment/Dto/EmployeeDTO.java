package com.assignment.assignment.Dto;

import com.assignment.assignment.model.Department;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EmployeeDTO {
    private String id;
    private String name;
    private String designation;
    private String email;
    @DBRef
    private String departmentId;
    private Double salary;

}
