package com.example.crud.domain.product;
import jakarta.validation.constraints.*;
public record RequestProduct(
    String id,
    @NotBlank @Size(max = 120) String name,
    @NotNull @Positive Integer price,
    @NotBlank @Size(max = 80) String category,
    @NotBlank @Pattern(regexp = "Mogi das Cruzes|Recife|Porto Alegre",
        message = "deve ser Mogi das Cruzes, Recife ou Porto Alegre") String distributionCenter
) {}
