package com.example.crud.domain.product;
import jakarta.persistence.*;
import lombok.*;
@Table(name = "product")
@Entity
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private Integer price;
    private Boolean active;
    private String category;
    @Column(name = "distribution_center", nullable = false)
    private String distributionCenter;
    public Product(RequestProduct data) { update(data); active = true; }
    public void update(RequestProduct data) {
        name = data.name(); price = data.price(); category = data.category();
        distributionCenter = data.distributionCenter();
    }
}
