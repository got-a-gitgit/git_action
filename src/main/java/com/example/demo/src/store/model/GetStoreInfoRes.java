package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetStoreInfoRes {
    private String storeName;
    private String profileImageUrl;
    private String description;
    private float rating;
    private int trade;
    private int follower;
    private int following;
    private String createdDate;
    private String authenticationFlag;
}
