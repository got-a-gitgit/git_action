package com.example.demo.src.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ReviewInfo {
    private int reviewId;
    private float rating;
    private String contents;
    private String storeName;
    private String registeredDate;
    private int productId;
    private String title;
}
