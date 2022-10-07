package com.example.demo.src.wish;


import com.example.demo.src.wish.model.GetWishesRes;
import com.example.demo.utils.S3Service;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



import static com.example.demo.config.BaseResponseStatus.*;


@RestController
@AllArgsConstructor
@RequestMapping("/wishes")
public class WishController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final WishProvider wishProvider;
    @Autowired
    private final WishService wishService;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final S3Service s3Service;


    /**
     * 관심 상품 목록 조회 API
     * [GET] /wishes?id={id}&date={date}&size={size}
     * @return BaseResponse<GetWishesRes>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetWishesRes> getWishes(@RequestParam(value = "id", defaultValue = "0") int productId,
                                                @RequestParam(value="date") String date,
                                                @RequestParam(value = "size", defaultValue = "100") int size) throws BaseException {

        //jwt 인증 및 userId 추출
        int userId= jwtService.getUserId();

        GetWishesRes result = wishProvider.getWishes(userId, productId, date, size);

        return new BaseResponse<>(result);
    }

    /**
     * 관심 상품 등록 API
     * [POST] /wishes/:product-id
     * @return BaseResponse<>
     */
    @ResponseBody
    @PostMapping("/{product-id}")
    public BaseResponse createWish(@PathVariable(value = "product-id", required = false)int productId) throws BaseException {

        //jwt 인증 및 userId 추출
        int userId= jwtService.getUserId();


        //관심 상품 등록
        try{
            wishService.createWish(userId, productId);
            return new BaseResponse<>(INSERT_SUCCESS);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 관심 상품 삭제 API
     * [DELETE] /wishes/:product-id
     * @return BaseResponse<>
     */
    @ResponseBody
    @DeleteMapping("/{product-id}")
    public BaseResponse deleteWish(@PathVariable(value = "product-id", required = false)int productId) throws BaseException {

        //jwt 인증 및 userId 추출
        int userId= jwtService.getUserId();


        //관심 상품 삭제
        try{
            wishService.deleteWish(userId, productId);
            return new BaseResponse<>(DELETE_SUCCESS);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
