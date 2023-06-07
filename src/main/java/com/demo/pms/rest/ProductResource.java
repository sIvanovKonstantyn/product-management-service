package com.demo.pms.rest;

import com.demo.pms.model.Product;
import com.demo.pms.rest.dto.ProductRequest;
import com.demo.pms.rest.dto.ProductResponse;
import com.demo.pms.services.ProductService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/products")
public class ProductResource {

    private final ProductService productService;
    private final ModelMapper mapper;

    @Autowired
    public ProductResource(ProductService productService, ModelMapper mapper) {
        this.productService = productService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAll().stream()
                .map(product -> mapper.map(product, ProductResponse.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return new ResponseEntity<>(
                mapper.map(productService.getOne(id), ProductResponse.class),
                HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductRequest productRequest) {
        Product createdProduct = productService.save(mapper.map(productRequest, Product.class));
        return new ResponseEntity<>(mapper.map(createdProduct, ProductResponse.class),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,
                                                         @RequestBody @Valid ProductRequest productRequest) {
        Product product = mapper.map(productRequest, Product.class);
        product.setId(id);
        Product updatedProduct = productService.update(product);
        return new ResponseEntity<>(mapper.map(updatedProduct, ProductResponse.class),
                HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
