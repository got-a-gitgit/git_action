package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetFollowRes {
    private List<followInfo> followInfoList;
    private boolean hasNextPage;
    private int lastFollowId;
}
