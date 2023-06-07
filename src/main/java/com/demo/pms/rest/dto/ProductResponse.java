package com.demo.pms.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String code;
    private String name;
    private double priceEUR;
    private double priceUSD;
    private String description;
    private boolean isAvailable;
}
