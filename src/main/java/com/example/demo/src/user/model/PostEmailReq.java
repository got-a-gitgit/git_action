package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
@AllArgsConstructor
public class PostEmailReq {
    private int flag;

    @NotBlank(message = "이메일을 입력하세요.")
    @Email(message = "이메일을 확인하세요.")
    private String email;
}
