package com.example.demo.src.event;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.event.model.GetEventListRes;
import com.example.demo.src.event.model.GetNoticeRes;
import com.example.demo.src.product.model.GetRecommendedProductListRes;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.S3Service;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/events")
public class EventController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final EventProvider eventProvider;
    @Autowired
    private final EventService eventService;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final S3Service s3Service;

    /**
     * 홈 화면 이벤트 이미지 조회 API
     * [GET] /events
     * @return BaseResponse<List<GetEventListRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetEventListRes>> getEventList() throws BaseException {
        //jwt 인증
        int userId= jwtService.getUserId();

        try{
            //추천 상품 조회
            List<GetEventListRes> getEventListRes = eventProvider.getEventList();
            return new BaseResponse<>(getEventListRes);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * My탭 공지 목록 조회 API
     * [GET] /events/notices
     * @return BaseResponse<List<GetNoticeRes>>
     */
    @ResponseBody
    @GetMapping("/notices")
    public BaseResponse<List<GetNoticeRes>> getNotices() throws BaseException {
        List<GetNoticeRes> getEventListRes = eventProvider.getNotices();
        return new BaseResponse<>(getEventListRes);
    }

}
