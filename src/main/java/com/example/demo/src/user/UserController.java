package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.PostEmailReq;
import com.example.demo.src.user.model.PostLoginReq;
import com.example.demo.src.user.model.PostLoginRes;
import com.example.demo.src.user.model.PostTokenReq;
import com.example.demo.utils.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.Random;

import static com.example.demo.config.BaseResponseStatus.INVALID_EMAIL_AUTH;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final MailService mailService;

    @Autowired
    public UserController(UserService userService, MailService mailService) {
        this.userService = userService;
        this.mailService = mailService;
    }

    /**
     * 이메일로 인증코드 전송 API
     * [POST] /users/confirmed
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/confirmed")
    public BaseResponse<String> sendCertEmail(@RequestBody @Valid PostEmailReq userEmail) throws BaseException {
        String email = userEmail.getEmail();

        //인증번호(난수) 생성
        Random random = new Random();
        String code = String.valueOf(random.nextInt(888888) + 111111);

        mailService.sendCodeMail(email, code);

        return new BaseResponse<>(code);
    }

    /**
     * 이메일 인증으로 로그인 API
     * [POST] /users/login
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/login")
    public BaseResponse<PostLoginRes> loginByEmail(@RequestBody @Valid PostLoginReq loginInfo) throws BaseException {
        String email = loginInfo.getEmail();
        boolean isAuth = loginInfo.getAuth();

        PostLoginRes loginRes;

        if (isAuth){   // 인증코드가 검증된 경우
            loginRes = userService.loginByEmail(email);
        } else {
          throw new BaseException(INVALID_EMAIL_AUTH);   // 인증 검증 에러
        }

        return new BaseResponse<>(loginRes);
    }


    /**
     * 카카오로 로그인 API
     * [POST] /users/kakao
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/kakao")
    public BaseResponse<PostLoginRes> loginByKakao(@RequestBody @Valid PostTokenReq kakaoToken) throws BaseException {
        String accessToken = kakaoToken.getAccessToken();

        PostLoginRes loginRes = userService.loginByKakao(accessToken);

        return new BaseResponse<>(loginRes);
    }
}
