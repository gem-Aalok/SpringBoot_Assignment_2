package com.assignment.assignment.model;

import jakarta.persistence.OneToMany;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Document(collection = "employees2")
public class Employee {
    @Id
    private String id;
    private String name;
    private String designation;
    private String email;
    @OneToMany
    private List<Department> departments;
    private Double salary;
}
