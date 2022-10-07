package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.src.review.model.PostReviewReq;
import com.example.demo.src.review.model.PutReviewReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ReviewService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ReviewProvider reviewProvider;
    private final ReviewDao reviewDao;
    private final JwtService jwtService;

    @Autowired
    public ReviewService(ReviewProvider reviewProvider, ReviewDao reviewDao, JwtService jwtService) {
        this.reviewProvider = reviewProvider;
        this.reviewDao = reviewDao;
        this.jwtService = jwtService;
    }

    /** 거래 후기 등록 **/
    public void registerReview(int userId, int targetId, PostReviewReq reviewInfo) throws BaseException {
        // 거래 후기를 작성했는지 확인
        int isEnable =  reviewProvider.checkEnableReview(userId, reviewInfo.getTradeId());
        if (isEnable == 1){
            throw new BaseException(REGISTERED_REVIEW);
        }

        try{
            // 거래 후기 등록
            int result = reviewDao.insertReview(userId, targetId, reviewInfo);
            if (result == 0){
                throw new BaseException(FAIL_REGISTER_REVIEW);
            }
        } catch (Exception e){
            logger.error("RegisterReview Error", e);
            throw new BaseException(FAIL_REGISTER_REVIEW);
        }
    }

    /** 거래 후기 삭제 **/
    public void removeReview(int userId, int reviewId) throws BaseException {
        // 거래 후기를 작성했는지 확인
        int isEnable =  reviewProvider.checkDeleteReview(userId, reviewId);
        if (isEnable == 0){
            throw new BaseException(INVALID_ACCESS);
        }

        try{
            // 거래 후기 삭제
            reviewDao.deleteReview(userId, reviewId);
        } catch (Exception e){
            logger.error("RemoveReview Error", e);
            throw new BaseException(FAIL_REMOVE_REVIEW);
        }
    }

    /** 거래 후기 수정 **/
    public void modifyReview(int userId, int reviewId, PutReviewReq reviewInfo) throws BaseException {
        // 거래 후기를 작성했는지 확인
        int isEnable =  reviewProvider.checkDeleteReview(userId, reviewId);
        if (isEnable == 0){
            throw new BaseException(INVALID_ACCESS);
        }

        try{
            // 거래 후기 수정
            reviewDao.updateReview(userId, reviewId, reviewInfo);
        } catch (Exception e){
            logger.error("ModifyReview Error", e);
            throw new BaseException(FAIL_MODIFY_REVIEW);
        }
    }


}
