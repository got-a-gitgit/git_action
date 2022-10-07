package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchStoreProfileRes {
    private String profileImageUrl;
    private String storeName;
    private String description;
}
