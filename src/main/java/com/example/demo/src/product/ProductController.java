package com.example.demo.src.product;

import com.example.demo.utils.S3Service;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.product.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;


@RestController
@AllArgsConstructor
@RequestMapping("/products")
public class ProductController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ProductProvider productProvider;
    @Autowired
    private final ProductService productService;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final S3Service s3Service;



    /**
     * 상품 등록 API
     * [POST] /products
     * @return BaseResponse<PostProductRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostProductRes> createProduct(@ModelAttribute @Valid PostProductReqAsString postProductReqAsString) throws BaseException, BindException {

        //jwt 인증
        int userId= jwtService.getUserId();

        //String -> Integer
        //Validation
        PostProductReq postProductReq = productService.validateRequest(postProductReqAsString);
        postProductReq.setUserId(userId);

        List<MultipartFile> images= postProductReq.getImages();

        //empty image validation
        if(images.get(0).isEmpty()) throw new BaseException(EMPTY_IMAGE_ERROR);

        //S3에 이미지 업로드 및 url 반환
        List<String> imageUrls = s3Service.uploadImage(images);

        //상품 등록
        try{
            PostProductRes postProductRes = productService.createProduct(postProductReq, imageUrls);
            return new BaseResponse<>(postProductRes, INSERT_SUCCESS);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 상품 삭제 API
     * [DELETE] /products/:product-id
     * @return BaseResponse
     */
    @ResponseBody
    @DeleteMapping("/{product-id}")
    public BaseResponse deleteProduct(@PathVariable("product-id") int productId) throws BaseException {
        //jwt 인증
        int userId= jwtService.getUserId();

        //상품 삭제
        try{
            productService.deleteProduct(productId);
            return new BaseResponse<>(DELETE_SUCCESS);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 상품 상제페이지 API
     * [GET] /products/:product-id
     * @return BaseResponse<GetProductRes>
     */
    @ResponseBody
    @GetMapping("/{product-id}")
    public BaseResponse<GetProductRes> getProduct(@PathVariable("product-id") int productId) throws BaseException {
        //jwt 인증
        int userId= jwtService.getUserId();


        try{
            //상품 조회
            GetProductRes getProductRes = productProvider.getProduct(productId);
            //상품 조회수 증가
            productService.increaseProductView(productId);
            return new BaseResponse<>(getProductRes);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 상점 판매상품 조회 API
     * [GET] /products/stores?store-id=스토어id&last-product-id=마지막상품id
     * @return BaseResponse<GetProductListRes>
     */
    @ResponseBody
    @GetMapping("/stores")
    public BaseResponse<GetStoreProductListRes> getProductListByStoreId(@RequestParam(value = "store-id", required = true) int storeId,
                                                                        @RequestParam(value="size",required = false, defaultValue = "-1") Integer size,
                                                                        @RequestParam(value = "last-product-id", required = false, defaultValue = "-1") Integer lastProductId,
                                                                        @RequestParam(value="last-updated-at", required = false)String lastUpdatedAt) throws BaseException {
        //jwt 인증
        int userId= jwtService.getUserId();

        try{
            //상점 판매상품 조회
            GetStoreProductListRes getStoreProductListRes = productProvider.getStoreProductListByStoreId(userId, storeId, lastUpdatedAt, lastProductId,size);
            return new BaseResponse<>(getStoreProductListRes);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 홈 화면 추천 상품 조회 API
     * [GET] /products
     * @return BaseResponse<>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetRecommendedProductListRes> getProductList(@RequestParam(value = "last-updated-at", required = false) String lastUpdatedAt,
                                                                     @RequestParam(value = "last-product-id", required = false, defaultValue = "-1") Integer lastProductId) throws BaseException {
        //jwt 인증
        int userId= jwtService.getUserId();

        try{
            //추천 상품 조회
            GetRecommendedProductListRes getRecommendedProductListRes = productProvider.getProductList(userId, lastUpdatedAt, lastProductId);
            return new BaseResponse<>(getRecommendedProductListRes);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 카테고리 목록 조회 API
     * [GET] /products/category?category-id={}
     * @return BaseResponse<>
     */
    @ResponseBody
    @GetMapping("/category")
    public BaseResponse<GetCategoryListRes> getCategoryList(@RequestParam(value = "category-id", required = true) int categoryId) throws BaseException {
        //jwt 인증
        int userId= jwtService.getUserId();

        try{
            if (categoryId == 0){   // 대분류 카테고리 조회
                GetCategoryListRes getCategoryListRes = productProvider.getMainCategoryList();
                return new BaseResponse<>(getCategoryListRes);
            }
            // 그 외 카테고리 조회
            GetCategoryListRes getCategoryListRes = productProvider.getCategoryList(categoryId);
            return new BaseResponse<>(getCategoryListRes);

        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 상품 상태 변경 API
     * [PATCH] /products/:product-id/status/:status
     * @return BaseResponse
     */
    @ResponseBody
    @PatchMapping("/{product-id}/status/{status}")
    public BaseResponse<FetchProductStatusRes> fetchProductStatus(@PathVariable("product-id")int productId, @PathVariable("status")String status) throws BaseException {
        //jwt 인증
        int userId= jwtService.getUserId();

        try{
            //상품 상태 변경
            FetchProductStatusRes fetchProductStatusRes = productService.updateProductStatus(productId, status);
            return new BaseResponse<>(fetchProductStatusRes);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 상품을 찜한 사람 목록 조회 API
     * [Get] /products/:product-id/wishes
     * @return BaseResponse
     */
    @ResponseBody
    @GetMapping("/{product-id}/wishes")
    public BaseResponse<List<GetWisherListRes>> getWisherList(@PathVariable("product-id")int productId) throws BaseException {
        //jwt 인증
        int userId= jwtService.getUserId();

        try{
            List<GetWisherListRes> getWisherListRes = productProvider.getWisherList(productId);
            return new BaseResponse<>(getWisherListRes);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 상품 수정 API
     * [PUT] /products/:product-id
     * @return BaseResponse
     */
    @ResponseBody
    @PutMapping("/{product-id}")
    public BaseResponse<PutProductRes> updateProduct(@PathVariable("product-id")int productId, @ModelAttribute @Valid PutProductReq putProductReq) throws BaseException {
        //jwt 인증
        int userId= jwtService.getUserId();

        List<MultipartFile> images= putProductReq.getNewImages();

        //S3에 이미지 업로드 및 url 반환
        List<String> imageUrls=null;
        if (images!=null && !images.get(0).isEmpty()) {
             imageUrls= s3Service.uploadImage(images);
        }

        try{
            productService.updateProduct(productId,putProductReq, imageUrls);
            PutProductRes putProductRes = new PutProductRes(productId, userId);
            return new BaseResponse<>(putProductRes, UPDATE_SUCCESS);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
