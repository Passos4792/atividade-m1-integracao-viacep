package com.example.crud.controllers;
import com.example.crud.domain.product.*;
import com.example.crud.infra.ApiException;
import com.example.crud.services.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductRepository repository;
    private final ProductService products;
    private final ViaCepService viaCep;
    public ProductController(ProductRepository repository, ProductService products, ViaCepService viaCep) {
        this.repository = repository; this.products = products; this.viaCep = viaCep;
    }
    @GetMapping
    public List<Product> list(@RequestParam(required = false) String category) {
        return category == null ? repository.findAllByActiveTrue()
            : repository.findAllByActiveTrueAndCategoryIgnoreCase(category);
    }
    @GetMapping("/{id}")
    public Product byId(@PathVariable String id) { return products.findActive(id); }
    @GetMapping("/top5")
    public List<Product> top5() { return repository.findTop5ByActiveTrueOrderByPriceDescIdAsc(); }
    // A rota da aula anterior continua aceitando os quatro meios de entrada.
    // Quando repetida, a categoria deve ser consistente entre os parâmetros.
    @GetMapping("/category/{categoryAsPath}")
    public List<Product> byCategory(@PathVariable String categoryAsPath,
            @RequestHeader(required = false) String categoryAsHeader,
            @RequestParam(required = false) String categoryAsParam,
            @RequestBody(required = false) @Valid RequestCategory categoryAsBody) {
        for (String supplied : new String[]{categoryAsHeader,categoryAsParam,
                categoryAsBody == null ? null : categoryAsBody.category()}) {
            if (supplied != null && !categoryAsPath.equalsIgnoreCase(supplied))
                throw new ApiException(HttpStatus.BAD_REQUEST,"As categorias informadas devem ser iguais");
        }
        return repository.findAllByActiveTrueAndCategoryIgnoreCase(categoryAsPath);
    }
    @PostMapping
    public ResponseEntity<Product> create(@RequestBody @Valid RequestProduct data) {
        Product product = products.create(data);
        return ResponseEntity.created(URI.create("/product/" + product.getId())).body(product);
    }
    @PutMapping
    public Product update(@RequestBody @Valid RequestProduct data) { return products.update(data); }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable String id) {
        products.deactivate(id); return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}/availability")
    public boolean availability(@PathVariable String id, @RequestParam String cep) {
        return viaCep.isAvailable(id,cep);
    }
}
