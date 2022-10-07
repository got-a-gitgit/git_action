package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
@AllArgsConstructor
public class PostTokenReq {
    private int flag;

    @NotBlank(message = "카카오 AccessToken을 입력하세요.")
    private String accessToken;
}
