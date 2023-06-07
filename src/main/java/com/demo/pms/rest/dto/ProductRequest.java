package com.demo.pms.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductRequest {
    @NotNull
    @Size(min = 10, max = 10)
    private String code;

    @NotNull
    private String name;

    @PositiveOrZero
    private double priceEUR;

    private String description;

    private boolean isAvailable;
}
