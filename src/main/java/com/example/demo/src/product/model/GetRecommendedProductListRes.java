package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetRecommendedProductListRes {
    private boolean hasNextPage;
    private int lastProductId;
    private String lastUpdatedAt;
    private List<RecommendedProduct> productList;
}
