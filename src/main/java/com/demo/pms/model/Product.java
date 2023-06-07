package com.demo.pms.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Product {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, length = 10)
    private String code;
    @Column
    private String name;
    @Column
    private double priceEUR;
    @Column
    private double priceUSD;
    @Column
    private String description;
    @Column
    private boolean isAvailable;
}
