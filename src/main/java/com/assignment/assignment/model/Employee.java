package com.assignment.assignment.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Document(collection = "employees")
public class Employee {
    @Id
    private String id;
    private String name;
    private String designation;
    private String email;
    @DBRef
    private Department department;
    private Double salary;
}
