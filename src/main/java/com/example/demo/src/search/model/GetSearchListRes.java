package com.example.demo.src.search.model;

import com.example.demo.src.product.model.RecommendedProduct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetSearchListRes {
    private boolean hasNextPage;
    private int lastProductId;
    private String lastUpdatedAt;
    private int lastProductPrice;
    private List<SearchProduct> searchProductList;
}
