package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class UserProvider {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;


    @Autowired
    public UserProvider(UserDao userDao) {
        this.userDao = userDao;
    }

    /** 이메일 존재 여부 확인 **/
    public UserInfo checkUserEmail(String email) throws BaseException {
        try {
            return userDao.checkUserEmail(email);
        } catch (Exception e){
            logger.error("CheckEmail Error(Dao)", e);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 카카오에서 사용자 정보 조회 **/
    public String getKakaoUserInfo(String accessToken) throws BaseException{
        String reqURL = "https://kapi.kakao.com/v2/user/me";

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "bearer " + accessToken);

            LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();

            HttpEntity<MultiValueMap<String, String>> restRequest = new HttpEntity<>(params, headers);

            return restTemplate.postForObject(reqURL, restRequest, String.class);

        } catch (Exception e){
            logger.error("Kakao API Fail", e);
            throw new BaseException(INVALID_EMAIL_AUTH);
        }
    }
}
