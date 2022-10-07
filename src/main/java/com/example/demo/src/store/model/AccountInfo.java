package com.example.demo.src.store.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class AccountInfo {
    private int accountId;
    private String defaultFlag;
    private int bankId;
    private String logoImageUrl;
    private String bank;
    private String accountNumber;
    private String name;
}
