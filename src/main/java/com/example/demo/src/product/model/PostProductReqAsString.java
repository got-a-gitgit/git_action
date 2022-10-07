package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostProductReqAsString {
    @NotNull(message = "이미지가 입력되지 않았습니다.")
    private List<MultipartFile> images;

    @NotBlank(message = "제목이 입력되지 않았습니다.")
    private String name;

    @NotNull(message = "가격이 입력되지 않았습니다.")
    private String price;

    @NotNull(message = "카테고리가 입력되지 않았습니다.")
    private String categoryId;

    @NotNull(message = "배송비 포함 여부가 입력되지 않았습니다.")
    private String shippingFeeIncluded;

    @NotBlank(message = "지역 정보가 입력되지 않았습니다.")
    private String location;

    @NotNull(message = "개수가 입력되지 않았습니다.")
//    @Min(value=1, message = "개수는 1개 이상이어야 합니다.")
    private String amount;

    @NotNull(message = "상품의 사용여부가 입력되지 않았습니다.")
    private String used;

    @NotNull(message = "안전 결재 사용 여부가 입력되지 않았습니다.")
    private String safePayment;

    @NotNull(message = "교환 가능 여부가 입력되지 않았습니다.")
    private String exchange;

    @NotNull(message = "상품 설명이 입력되지 않았습니다.")
    private String contents;

    private List<String> tags;
}
