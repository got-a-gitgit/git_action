package com.example.demo.src.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
public class PostReviewReq {
    @NotNull(message = "거래 식별번호(Id)를 입력해주세요.")
    private Integer tradeId;

    @NotNull(message = "판매자 식별번호(Id)를 입력해주세요.")
    private Integer sellerId;

    @NotNull(message = "구매자 식별번호(Id)를 입력해주세요.")
    private Integer buyerId;

    @NotNull(message = "별평가를 입력해주세요.")
    @Max(value = 5, message = "최대 5점까지 입력 가능합니다.")
    @Positive
    private Float rating;

    @Size(min = 20, message = "최소 20자 이상 입력해주세요.")
    private String contents;
}
