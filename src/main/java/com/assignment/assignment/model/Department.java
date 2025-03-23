package com.assignment.assignment.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Document(collection = "departments")
public class Department {
    @Id
    private String id;
    private String name;
}
