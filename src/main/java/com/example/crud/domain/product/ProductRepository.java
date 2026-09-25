package com.example.crud.domain.product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findAllByActiveTrue();
    Optional<Product> findByIdAndActiveTrue(String id);
    List<Product> findAllByActiveTrueAndCategoryIgnoreCase(String category);
    List<Product> findTop5ByActiveTrueOrderByPriceDescIdAsc();
}
