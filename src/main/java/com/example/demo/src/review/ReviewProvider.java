package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.src.review.model.GetReviewsRes;
import com.example.demo.src.review.model.ReviewInfo;
import com.example.demo.src.store.StoreProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.INVALID_ACCESS;
import static com.example.demo.config.BaseResponseStatus.FAIL_GET_REVIEWS;

@Service
public class ReviewProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ReviewDao reviewDao;
    private final StoreProvider storeProvider;

    @Autowired
    public ReviewProvider(ReviewDao reviewDao, StoreProvider storeProvider) {
        this.reviewDao = reviewDao;
        this.storeProvider = storeProvider;
    }

    /** 거래 후기를 작성했는지 확인 **/
    public int checkEnableReview(int userId, int tradeId) throws BaseException {
        try {
            return reviewDao.selectReviewer(userId, tradeId);
        } catch (Exception e){
            logger.error("checkEnableReview Error", e);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 거래 후기를 삭제 가능한지 확인 **/
    public int checkDeleteReview(int userId, int reviewId) throws BaseException {
        try {
            return reviewDao.selectDeleteReview(userId, reviewId);
        } catch (Exception e){
            logger.error("checkDeleteReview Error", e);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 거래 후기 조회 **/
    public GetReviewsRes getReviews(int storeId, int reviewId, String date, int size) throws BaseException {
        // 유효한 유저인지 확인
        int isUser = storeProvider.checkUserId(storeId);
        if (isUser == 0) {
            throw new BaseException(INVALID_ACCESS);
        }

        try {
            int reviewCount = reviewDao.selectReviewCount(storeId);
            List<ReviewInfo> reviewInfo =  reviewDao.selectReviews(storeId, reviewId, date, size);
            int lastReviewId = 0;
            String lastRegisteredDate = "";

            // 다음 페이지 여부
            boolean hasNextPage = true;
            if (reviewInfo.size() != size + 1) {
                hasNextPage = false;
            }
            if (hasNextPage){
                reviewInfo.remove(size);  // 마지막 데이터 삭제
                lastReviewId = reviewInfo.get(size-1).getReviewId();            // 마지막 데이터 리뷰 Id
                lastRegisteredDate = reviewInfo.get(size-1).getRegisteredDate();  // 마지막 데이터 날짜
            }

            return new GetReviewsRes(reviewCount, reviewInfo, hasNextPage, lastReviewId, lastRegisteredDate);

        } catch (Exception e){
            logger.error("GetReviews Error", e);
            throw new BaseException(FAIL_GET_REVIEWS);
        }
    }
}
