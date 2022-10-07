package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecommendedProduct {
    private int productId;
    private int userId;
    private String image;
    private String wish;
    private int price;
    private String name;
    private String location;
    private String updatedAt;
    private String safePayment;
    private int wishes;
    private String status;
}
