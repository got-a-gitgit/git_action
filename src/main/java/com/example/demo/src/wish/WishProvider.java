package com.example.demo.src.wish;


import com.example.demo.config.BaseException;
import com.example.demo.src.wish.model.GetWishesRes;
import com.example.demo.src.wish.model.WishInfo;
import com.example.demo.utils.JwtService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;


@Service
public class WishProvider {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final WishDao wishDao;

    @Autowired
    public WishProvider(WishDao wishDao) {
        this.wishDao = wishDao;
    }


    /** 관심상품 목록 조회 **/
    public GetWishesRes getWishes(int userId, int productId, String date, int size) throws BaseException {
        try {
            List<WishInfo> wishInfo = wishDao.selectWishes(userId, productId, date, size);
            int lastProductId = 0;
            String lastUpdateDate = "";

            // 다음 페이지 여부
            boolean hasNextPage = true;
            if (wishInfo.size() != size + 1) {
                hasNextPage = false;
            }
            if (hasNextPage){
                wishInfo.remove(size);  // 마지막 데이터 삭제
                lastProductId = wishInfo.get(size-1).getProductId();       // 마지막 데이터 상품 Id
                lastUpdateDate = wishInfo.get(size-1).getUpdateDate();  // 마지막 데이터 날짜
            }

            return new GetWishesRes(wishInfo, hasNextPage, lastProductId, lastUpdateDate);

        } catch (Exception e){
            logger.error("GetWishes", e);
            throw new BaseException(FAIL_GET_WISHES);
        }
    }
}
