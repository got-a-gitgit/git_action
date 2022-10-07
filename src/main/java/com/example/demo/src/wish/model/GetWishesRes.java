package com.example.demo.src.wish.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetWishesRes {
    private List<WishInfo> wishInfoList;
    private boolean hasNextPage;
    private int lastProductId;
    private String lastUpdateDate;
}
