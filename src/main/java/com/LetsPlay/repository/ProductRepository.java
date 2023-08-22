package com.LetsPlay.repository;

import com.LetsPlay.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findProductByName(String name);
    List<Product> findProductsByPrice(Double price);
    List<Product> findProductsByUserId(String userId);
}
