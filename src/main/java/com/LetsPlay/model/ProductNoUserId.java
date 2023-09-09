package com.LetsPlay.model;

import lombok.Data;

@Data
public class ProductNoUserId {
    private String id;
    private String name;
    private String description;
    private Double price;
    private String owner;
}
