package com.gritlab.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductRequest {

    @NotNull(message = "Name is required")
    @Pattern(regexp = "^(?!\\s*$).+", message = "Name cannot be empty or contain only spaces")
    @Size(max = 50, min = 1, message = "Name cannot exceed 50 characters")
    private String name;

    @NotNull(message = "Description is required")
    @Pattern(regexp = "^(?!\\s*$).+", message = "Description cannot be empty or contain only spaces")
    @Size(max = 1000, min = 1, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0.0")
    @DecimalMax(value = "999999999.99", message = "Price must be at most 999999999.99")
    private Double price;

    @NotNull(message = "userId is required")
    @Pattern(regexp = "^(?!\\s*$).+", message = "userId cannot be empty or contain only spaces")
    @Size(max = 8, min = 8, message = "userId must contain 8 characters")
    private String userId;
}