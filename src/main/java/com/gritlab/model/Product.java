package com.gritlab.model;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "products")
@Entity
@Data
@AllArgsConstructor
public class Product {

    @Id
    private String id;

    @Field("name")
    @NotNull(message = "Name is required")
    @Pattern(regexp = "^(?!\\s*$).+", message = "Name cannot be empty or contain only spaces")
    @Size(max = 50, min = 1, message = "Name cannot exceed 50 characters")
    private String name;

    @Field("description")
    @NotNull(message = "Description is required")
    @Pattern(regexp = "^(?!\\s*$).+", message = "Description cannot be empty or contain only spaces")
    @Size(max = 1000, min = 1, message = "Description cannot exceed 1000 characters")
    private String description;

    @Field("price")
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0.0")
    @DecimalMax(value = "999999999.99", message = "Price must be less than 999999999.99")
    private Double price;

    @Field("userId")
    private String userId;
}
