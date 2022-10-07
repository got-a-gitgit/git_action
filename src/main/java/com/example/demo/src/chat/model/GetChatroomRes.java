package com.example.demo.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetChatroomRes {
    private String storeName;
    private int storeId;
    private int productId;
    private String productName;
    private int price;
    private String userProfile;
    private List<Message> messageList;

    public GetChatroomRes(String storeName, int storeId, int productId, String productName, int price, String userProfile) {
        this.storeName = storeName;
        this.storeId = storeId;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.userProfile = userProfile;
    }
}
