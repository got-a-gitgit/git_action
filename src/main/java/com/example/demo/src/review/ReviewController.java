package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.review.model.GetReviewsRes;
import com.example.demo.src.review.model.PostReviewReq;
import com.example.demo.src.review.model.PutReviewReq;
import com.example.demo.utils.JwtService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@AllArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ReviewProvider reviewProvider;
    @Autowired
    private final ReviewService reviewService;
    @Autowired
    private final JwtService jwtService;


    /**
     * 거래후기 등록 API
     * [POST] /reviews
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<String> registerReview(@RequestBody @Valid PostReviewReq reviewInfo) throws BaseException {
        // jwt 인증
        int userId= jwtService.getUserId();

        // 거래 후기 작성 권한 확인
        int targetId;
        if (userId == reviewInfo.getSellerId()) {
            targetId = reviewInfo.getBuyerId();
        } else if (userId == reviewInfo.getBuyerId()){
            targetId = reviewInfo.getSellerId();
        } else {
            throw new BaseException(INVALID_REVIEWER);
        }

        reviewService.registerReview(userId, targetId, reviewInfo);

        return new BaseResponse<>(INSERT_SUCCESS);
    }

    /**
     * 거래후기 삭제 API
     * [DELETE] /reviews/{review-id}
     * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping("/{review-id}")
    public BaseResponse<String> removeReview(@PathVariable("review-id")int reviewId) throws BaseException {
        // jwt 인증
        int userId= jwtService.getUserId();

        reviewService.removeReview(userId, reviewId);

        return new BaseResponse<>(DELETE_SUCCESS);
    }

    /**
     * 거래후기 수정 API
     * [PUT] /reviews/{review-id}
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PutMapping("/{review-id}")
    public BaseResponse<String> modifyReview(@PathVariable("review-id")int reviewId,
                                             @RequestBody @Valid PutReviewReq reviewInfo) throws BaseException {
        // jwt 인증
        int userId= jwtService.getUserId();

        reviewService.modifyReview(userId, reviewId, reviewInfo);

        return new BaseResponse<>(UPDATE_SUCCESS);
    }

    /**
     * 거래후기 목록 조회 API
     * [GET] /reviews/{store-id}?id={id}&date={date}&size={size}
     * @return BaseResponse<GetReviewsRes>
     */
    @ResponseBody
    @GetMapping("/{store-id}")
    public BaseResponse<GetReviewsRes> registerReview(@PathVariable("store-id") int storeId,
                                                      @RequestParam(value = "id", defaultValue = "0") int reviewId,
                                                      @RequestParam(value="date") String date,
                                                      @RequestParam(value = "size", defaultValue = "100") int size) throws BaseException {
        GetReviewsRes result = reviewProvider.getReviews(storeId, reviewId, date, size);
        return new BaseResponse<>(result);
    }

}
