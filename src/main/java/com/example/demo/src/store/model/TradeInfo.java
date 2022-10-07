package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TradeInfo {
    private int tradeId;
    private String productImageUrl;
    private String title;
    private int price;
    private String storeName;
    private String tradeDate;
    private String status;
}
