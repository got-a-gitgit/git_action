package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@Setter
@AllArgsConstructor
public class PostLoginReq {
    @Getter
    @NotBlank(message = "이메일을 입력하세요.")
    @Email(message = "이메일을 확인하세요.")
    private String email;

    private String auth;

    public boolean getAuth(){
        return Boolean.valueOf(auth);
    }

}
