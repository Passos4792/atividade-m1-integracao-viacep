package com.example.crud.services;
import com.example.crud.domain.product.*;
import com.example.crud.infra.ApiException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class ProductService {
    private final ProductRepository repository;
    public ProductService(ProductRepository repository) { this.repository = repository; }
    public Product findActive(String id) {
        return repository.findByIdAndActiveTrue(id).orElseThrow(EntityNotFoundException::new);
    }
    @Transactional
    public Product create(RequestProduct data) { return repository.save(new Product(data)); }
    @Transactional
    public Product update(RequestProduct data) {
        if (data.id() == null || data.id().isBlank())
            throw new ApiException(HttpStatus.BAD_REQUEST,"Informe o id para atualizar o produto");
        Product product = findActive(data.id()); product.update(data); return product;
    }
    @Transactional
    public void deactivate(String id) { findActive(id).setActive(false); }
}
