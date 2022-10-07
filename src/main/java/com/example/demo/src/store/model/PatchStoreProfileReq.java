package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
public class PatchStoreProfileReq {

    private MultipartFile newImageFile;
    private String originImageUrl;

    @Size(min = 2, max = 10, message = "상점명은 최소 2자, 최대 10자까지 입력 가능합니다.")
    @Pattern(regexp="^[가-힣a-zA-Z0-9]*$", message = "상점명은 띄어쓰기 없이 한글, 영문, 숫자만 가능합니다.")
    @NotBlank(message = "상점명을 입력하세요.")
    private String storeName;

    @Size(max = 1000, message = "최대 글자 수를 초과했습니다.")
    private String description;

}
