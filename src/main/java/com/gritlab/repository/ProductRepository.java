package com.gritlab.repository;

import com.gritlab.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;
import java.util.List;

@RepositoryRestResource(collectionResourceRel = "products", path = "products")
public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findById(String id);
    Optional<List<Product>> findByUserId(String userId);
}
