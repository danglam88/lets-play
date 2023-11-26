package com.gritlab.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gritlab.model.Product;
import com.gritlab.model.ProductRequest;
import com.gritlab.service.UserService;
import com.gritlab.service.ProductService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<Void> createProduct(@Valid @RequestBody ProductRequest productRequest,
            BindingResult result,
            UriComponentsBuilder ucb, Authentication authentication) throws MethodArgumentNotValidException {

        if (result.hasErrors()) {
            throw new MethodArgumentNotValidException((MethodParameter) null, result);
        }

        userService.authorizeUser(authentication, productRequest.getUserId());
        Product createdProduct = productService.createProduct(productRequest);
        URI locationOfNewProduct = ucb
                .path("/products/{productId}")
                .buildAndExpand(createdProduct.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewProduct).build();
    }

    @PermitAll
    @GetMapping
    public ResponseEntity<JsonNode> getAllProducts() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String productsNoUserId =
                objectMapper.writeValueAsString(productService.convertToDtos(productService.getAllProducts()));
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.readTree(productsNoUserId));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping("/{productId}")
    public ResponseEntity<JsonNode> getProductById(@PathVariable String productId, Authentication authentication)
            throws JsonProcessingException {

        Product product = productService.getProductById(productId);
        userService.authorizeUser(authentication, null);
        ObjectMapper objectMapper = new ObjectMapper();
        String productNoUserId =
                objectMapper.writeValueAsString(productService.convertToDto(product));
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.readTree(productNoUserId));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/{productId}")
    public ResponseEntity<Void> updateProduct(@PathVariable String productId,
                                              @Valid @RequestBody ProductRequest productRequest,
            BindingResult result,
            UriComponentsBuilder ucb, Authentication authentication) throws MethodArgumentNotValidException {

        if (result.hasErrors()) {
            throw new MethodArgumentNotValidException((MethodParameter) null, result);
        }

        Product product = productService.getProductById(productId);
        userService.authorizeUser(authentication, product.getUserId());
        Product updatedProduct = productService.updateProduct(product, productRequest);
        URI locationOfUpdatedProduct = ucb
                .path("/products/{productId}")
                .buildAndExpand(updatedProduct.getId())
                .toUri();
        return ResponseEntity.ok().location(locationOfUpdatedProduct).build();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String productId, Authentication authentication) {
        Product product = productService.getProductById(productId);
        userService.authorizeUser(authentication, product.getUserId());
        productService.deleteProduct(product);
        return ResponseEntity.ok().build();
    }
}
