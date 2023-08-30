package com.LetsPlay.controller;

import com.LetsPlay.service.ProductService;
import com.LetsPlay.model.Product;
import com.LetsPlay.response.Response;
import jakarta.annotation.security.PermitAll;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {

    @Autowired
    private ProductService productService;

    @PermitAll
    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        if (products.size() > 0) {
            return ResponseEntity.status(HttpStatus.OK).body(products);
        }
        Response errorResponse = new Response("No products exist in the system yet");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @PermitAll
    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable String productId) {
        Optional<Product> product = productService.getProductById(productId);
        if (product.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(product.get());
        }
        Response errorResponse = new Response("Product with id " + productId + " not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @Secured({ "ROLE_ADMIN", "ROLE_USER" })
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        try {
            Product createdProduct = productService.createProduct(product);
            if (createdProduct == null) {
                Response errorResponse = new Response("Creation of new product failed:" +
                        " no any field can be empty (or contain only spaces)," +
                        " every field must have at most 50 characters," +
                        " 'price' must be positive and not exceed 1000000000," +
                        " 'userId' must be a valid id of an existing user");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Secured({ "ROLE_ADMIN", "ROLE_USER" })
    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable String productId, @RequestBody Product product) {
        if (!productService.findProductById(productId)) {
            Response errorResponse = new Response("Product with id " + productId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        try {
            Product updatedProduct = productService.updateProduct(productId, product);
            if (updatedProduct == null) {
                Response errorResponse = new Response("Update of product with id "
                        + productId + " failed:" +
                        " no any field can be empty (or contain only spaces)," +
                        " every field must have at most 50 characters," +
                        " 'price' must be positive and not exceed 1000000000," +
                        " 'userId' must be a valid id of an existing user");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            return ResponseEntity.status(HttpStatus.OK).body(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Secured({ "ROLE_ADMIN", "ROLE_USER" })
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable String productId) {
        if (!productService.findProductById(productId)) {
            Response errorResponse = new Response("Product with id " + productId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        try {
            Response okResponse = new Response(productService.deleteProduct(productId));
            return ResponseEntity.status(HttpStatus.OK).body(okResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
