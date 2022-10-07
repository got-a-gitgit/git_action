package com.example.demo.src.wish.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter@AllArgsConstructor
public class WishInfo {
    // 상품 정보
    private int productId;
    private String productImageUrl;
    private String title;
    private int price;
    private String isSafePayment;
    @JsonIgnore
    private String updateDate;

    //판매자 정보
    private String profileImageUrl;
    private String storeName;
    private String registeredDate;

}
