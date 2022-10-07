package com.example.demo.src.search;

import com.example.demo.config.BaseException;
import com.example.demo.src.search.model.SearchProduct;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class SearchProvider {
    private final SearchDao searchDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public SearchProvider(SearchDao searchDao, JwtService jwtService) {
        this.searchDao = searchDao;
        this.jwtService = jwtService;
    }

    /** 카테고리별 상품 목록 조회 **/
    public List<SearchProduct>  getSearchByCategory(int userId, int categoryId, String safePayment, Integer priceFrom, Integer priceTo, String soldOutIncluded,
                                                    String shippingFeeIncluded, String usded, String exchange, String sort, String keyword) throws BaseException {
        try {
            return searchDao.getSearchByCategory(userId, categoryId, safePayment, priceFrom, priceTo, soldOutIncluded, shippingFeeIncluded, usded, exchange, sort, keyword);
        } catch (Exception exception) {
            logger.error("getSearchByCategory 에러", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
