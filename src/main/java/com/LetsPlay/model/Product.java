package com.LetsPlay.model;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
@Builder
@Data
@AllArgsConstructor
public class Product {

    @Id
    private String id;

    private String name;

    private String description;

    private Double price;

    private String userId;
}
