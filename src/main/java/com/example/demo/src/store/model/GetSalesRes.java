package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetSalesRes {
    private List<TradeInfo> tradeInfoList;
    private boolean hasNextPage;
    private int lastTradeId;
    private String lastTradeDate;
}
