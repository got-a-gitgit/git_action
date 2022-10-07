package com.example.demo.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetChatroomListRes {
    private int roomId;
    private String profileImageUrl;
    private String storeName;
    private String lastUpdatedAt;
    private String lastSentMessage;
    private String productImageUrl;
}
