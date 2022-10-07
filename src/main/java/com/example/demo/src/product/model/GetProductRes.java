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
public class GetProductRes {
    public GetProductRes(int productId, String name, int userId, int price, int categoryId, String categoryName, String shippingFeeIncluded, String location, int amount, String used, String safePayment, String exchange, String contents, int view, int wishes, String status, String createdAt) {
        this.productId = productId;
        this.name = name;
        this.userId = userId;
        this.price = price;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.shippingFeeIncluded = shippingFeeIncluded;
        this.location = location;
        this.amount = amount;
        this.used = used;
        this.safePayment = safePayment;
        this.exchange = exchange;
        this.contents = contents;
        this.view = view;
        this.wishes = wishes;
        this.status = status;
        this.createdAt = createdAt;
    }

    private List<ProductImage> images;
    private int productId;
    private String name;
    private int userId;
    private int price;
    private int categoryId;
    private String categoryName;
    private String shippingFeeIncluded;
    private String location;
    private int amount;
    private String used;
    private String safePayment;
    private String exchange;
    private String contents;
    private int view;
    private int wishes;
    private String status;
    private String createdAt;
    private List<String> tags;
}
