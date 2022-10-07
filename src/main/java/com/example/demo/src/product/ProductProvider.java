package com.example.demo.src.product;


import com.example.demo.config.BaseException;
import com.example.demo.src.product.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리

@Service
public class ProductProvider {

    private final ProductDao productDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ProductProvider(ProductDao productDao, JwtService jwtService) {
        this.productDao = productDao;
        this.jwtService = jwtService;
    }


    /** 상품 상세 조회 **/
    public GetProductRes getProduct(int productId) throws BaseException {
        try {
            //상품 정보 조회
            GetProductRes getProductRes = productDao.getProduct(productId);
            //상품 이미지 조회
            List<ProductImage> images = productDao.getProductImages(productId);
            getProductRes.setImages(images);
            //상품 태그 조회
            List<String> tags = productDao.getProductTags(productId);
            getProductRes.setTags(tags);

            return getProductRes;
        } catch (BaseException baseException) {
            logger.error("getProduct 에러", baseException);
            throw baseException;
        } catch (Exception exception) {
            logger.error("getProduct 에러", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 상점 판매 상품 목록 조회 **/
    public GetStoreProductListRes getStoreProductListByStoreId(int userId, int storeId, String lastUpdatedAt, int lastProductId, Integer size) throws BaseException {
        try{
            GetStoreProductListRes getStoreProductListRes = new GetStoreProductListRes();
            List<StoreProductRes> productList;

            //무한 스크롤 여부 구분
            if (size == -1) {
                productList = productDao.getWholeProductListByStoreId(userId, storeId);
                getStoreProductListRes.setProductList(productList);
                getStoreProductListRes.setHasNextPage(false);
            } else {
                //첫 조회와 무한스크롤 구분
                if (lastProductId == -1) {
                    productList = productDao.getFirstProductListByStoreId(userId, storeId, size+1);
                } else {
                    productList = productDao.getProductListByStoreId(userId, storeId, lastUpdatedAt, lastProductId,size+1);
                }

                //다음 페이지 존재 여부 입력
                if (productList.size() == size+1) {
                    getStoreProductListRes.setHasNextPage(true);
                    getStoreProductListRes.setProductList(productList.subList(0, productList.size()));
                } else {
                    getStoreProductListRes.setHasNextPage(false);
                    getStoreProductListRes.setProductList(productList);
                }

                //마지막 아이디 입력
                productList = getStoreProductListRes.getProductList();
                int newLastProductId = productList.get(productList.size() - 1).getProductId();
                getStoreProductListRes.setLastProductId(newLastProductId);

                //마지막 게시물 수정 timestamp 입력
                String newLastUpdatedAt = productList.get(productList.size() - 1).getUpdatedAt();
                getStoreProductListRes.setLastUpdatedAt(newLastUpdatedAt);
            }
            return getStoreProductListRes;
        } catch (Exception exception) {
            logger.error("getStoreProductListByStoreId 에러", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 홈화면 추천 상품 목록 조회 **/
    public GetRecommendedProductListRes getProductList(int userId, String lastUpdatedAt, Integer lastProductId) throws BaseException {
        try{
            GetRecommendedProductListRes getRecommendedProductListRes = new GetRecommendedProductListRes();
            List<RecommendedProduct> productList;

            //첫 조회와 무한스크롤 구분
            if (lastProductId == -1) {
                productList = productDao.getFirstProductList(userId);
            } else {
                productList = productDao.getProductList(userId,lastUpdatedAt, lastProductId);
            }

            //다음 페이지 존재 여부 입력
            if (productList.size() == 21) {
                getRecommendedProductListRes.setHasNextPage(true);
                getRecommendedProductListRes.setProductList(productList.subList(0, 20));
            } else {
                getRecommendedProductListRes.setHasNextPage(false);
                getRecommendedProductListRes.setProductList(productList);
            }

            //마지막 아이디 입력
            productList = getRecommendedProductListRes.getProductList();
            int newLastProductId = productList.get(productList.size() - 1).getProductId();
            getRecommendedProductListRes.setLastProductId(newLastProductId);

            //마지막 상품 게시 시간 입력
            String newLastUpdatedAt = productList.get(productList.size() - 1).getUpdatedAt();
            getRecommendedProductListRes.setLastUpdatedAt(newLastUpdatedAt);

            return getRecommendedProductListRes;
        } catch(Exception exception) {
            logger.error("getProductList 에러", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 하위 카테고리 목록 조회**/
    public GetCategoryListRes getCategoryList(int categoryId) throws BaseException {
        try {
            //하위 카테고리 목록 조회
            List<Category> subcategoryList = productDao.getCategoryList(categoryId);

            //불러온 값이 없으면 예외 발생
            if(subcategoryList.size()==0) throw new BaseException(NO_MORE_SUBCATEGORY);

            return new GetCategoryListRes(categoryId, subcategoryList);
        } catch (BaseException baseException) {
            logger.error("getCategoryList 에러", baseException);
            throw baseException;
        } catch (Exception exception) {
            logger.error("getCategoryList 에러", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 최상위 카테고리 목록 조회**/
    public GetCategoryListRes getMainCategoryList() throws BaseException {
        try {
            // 최상위 카테고리 목록 조회
            List<Category> subcategoryList = productDao.getMainCategoryList();

            //불러온 값이 없으면 예외 발생
            if(subcategoryList.size()==0) throw new BaseException(NO_MORE_SUBCATEGORY);

            return new GetCategoryListRes(0, subcategoryList);
        } catch (BaseException baseException) {
            logger.error("getMainCategoryList 에러", baseException);
            throw baseException;
        } catch (Exception exception) {
            logger.error("getMainCategoryList 에러", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 상품 찜한 사람 목록 조회**/
    public List<GetWisherListRes> getWisherList(int productId) throws BaseException {
        try {
            return productDao.getWisherList(productId);
        } catch (Exception exception) {
            logger.error("getWisherList 에러", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
