package com.example.demo.src.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetReviewsRes {
    private int reviewCount;
    private List<ReviewInfo> reviewInfoList;
    private boolean hasNextPage;
    private int lastReviewId;
    private String lastRegisteredDate;
}
