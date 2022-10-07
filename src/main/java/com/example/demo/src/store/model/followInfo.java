package com.example.demo.src.store.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class followInfo {
    private int userId;
    private String profileImageUrl;
    private String storeName;
    private String alarmFlag;
    private int follower;
    private int product;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ProductInfo> productInfoList;

    public followInfo(int userId, String profileImageUrl, String storeName, String alarmFlag, int follower, int product) {
        this.userId = userId;
        this.profileImageUrl = profileImageUrl;
        this.storeName = storeName;
        this.alarmFlag = alarmFlag;
        this.follower = follower;
        this.product = product;
        this.productInfoList = null;
    }
}
