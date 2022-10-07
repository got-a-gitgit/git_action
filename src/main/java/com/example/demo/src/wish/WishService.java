package com.example.demo.src.wish;


import com.example.demo.config.BaseException;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Transactional(rollbackFor = Exception.class)
@Service
public class WishService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final WishDao wishDao;
    private final WishProvider wishProvider;
    private final JwtService jwtService;

    @Autowired
    public WishService(WishDao wishDao, WishProvider wishProvider, JwtService jwtService) {
        this.wishDao = wishDao;
        this.wishProvider = wishProvider;
        this.jwtService = jwtService;
    }


    public void createWish(int userId, int productId) throws BaseException {
        try{
            //관심 상품 등록
            wishDao.createWish(userId,productId);

        } catch (Exception exception) {
            logger.error("createWish", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteWish(int userId, int productId) throws BaseException {
        try{
            //관심 상품 삭제
            wishDao.deleteWish(userId,productId);

        } catch (Exception exception) {
            logger.error("deleteWish", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
