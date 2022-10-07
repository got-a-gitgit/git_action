package com.example.demo.src.search;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.product.model.GetStoreProductListRes;
import com.example.demo.src.search.model.GetSearchListRes;
import com.example.demo.src.search.model.SearchProduct;
import com.example.demo.utils.JwtService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/searches")
public class SearchController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final SearchProvider searchProvider;
    @Autowired
    private final SearchService searchService;
    @Autowired
    private final JwtService jwtService;


    /**
     * 카테고리 검색 API
     * [GET] /searches?category-id={카테고리id}&size={사이즈}& last-updated-at={마지막 상품 등록시간} & {last-product-id=마지막상품 id}
     * @return BaseResponse<List<SearchProduct>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<SearchProduct>> getSearchResult(@RequestParam(value = "category-id", required = true) int categoryId,
                                                          @RequestParam(value = "safe-payment", required = false) String safePayment,
                                                          @RequestParam(value = "price-from", required = false) Integer priceFrom,
                                                          @RequestParam(value = "price-to", required = false) Integer priceTo,
                                                          @RequestParam(value = "sold-out-included", required = false, defaultValue = "Y") String soldOutIncluded,
                                                          @RequestParam(value = "shipping-fee-included", required = false) String shippingFeeIncluded,
                                                          @RequestParam(value = "used", required = false) String usded,
                                                          @RequestParam(value = "exchange", required = false) String exchange,
                                                          @RequestParam(value = "sort", required = false, defaultValue ="latest") String sort,
                                                          @RequestParam(value = "keyword", required = false) String keyword
                                                    ) throws BaseException {
        //jwt 인증
        int userId = jwtService.getUserId();

        try {
            //카테고리별 검색
            List<SearchProduct>  searchList = searchProvider.getSearchByCategory(userId, categoryId,
                    safePayment, priceFrom, priceTo, soldOutIncluded,
                    shippingFeeIncluded, usded, exchange, sort, keyword);
            return new BaseResponse<>(searchList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
