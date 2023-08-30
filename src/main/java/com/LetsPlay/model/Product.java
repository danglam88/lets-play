package com.LetsPlay.model;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Document(collection = "products")
@Builder
@Data
@AllArgsConstructor
public class Product {

    @Id
    private String id;

    @Field("name")
    @NotNull
    @Size(min = 1, max = 50)
    private String name;

    @Field("description")
    @NotNull
    @Size(min = 1, max = 50)
    private String description;

    @Field("price")
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @DecimalMax(value = "1000000000.0")
    private Double price;

    @Field("userId")
    @NotNull
    @Size(min = 8, max = 8)
    private String userId;
}
