package com.example.demo.src.search.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchProduct {
    private int productId;
    private int userId;
    private String image;
    private String wish;
    private int price;
    private String name;
    private String updatedAt;
    private String safePayment;
    private String status;
}
