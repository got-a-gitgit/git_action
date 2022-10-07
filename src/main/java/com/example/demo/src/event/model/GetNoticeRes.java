package com.example.demo.src.event.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetNoticeRes {
    private int eventId;
    private String type;
    private String contents;
    private String url;
}
