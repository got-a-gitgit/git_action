package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class PostAccountReq {
    @NotNull(message = "이름을 입력하세요.")
    private String name;
    @NotNull(message = "은행을 선택해주세요.")
    private Integer bankId;
    @NotNull(message = "계좌를 입력하세요.")
    private String accountNumber;
    @NotNull(message = "기본계좌 여부를 입력하세요.")
    private String defaultFlag;
}
