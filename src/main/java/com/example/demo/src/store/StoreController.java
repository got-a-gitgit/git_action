package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.store.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/stores")
public class StoreController {

    private final StoreProvider storeProvider;
    private final StoreService storeService;
    private final JwtService jwtService;

    @Autowired
    public StoreController(StoreProvider storeProvider, StoreService storeService, JwtService jwtService) {
        this.storeProvider = storeProvider;
        this.storeService = storeService;
        this.jwtService = jwtService;
    }

    /**
     * 상점명 설정 API
     * [POST] /stores
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<String> registerStoreName(@RequestBody @Valid PostStoreNameReq store) throws BaseException {
        // jwt에서 id 추출
        int userId = jwtService.getUserId();

        String result = storeService.registerStoreName(userId, store.getName());

        return new BaseResponse<>(result);
    }

    /**
     * 상점 정보 조회 API
     * [GET] /stores/{store_id}
     * @return BaseResponse<GetStoreInfoRes>
     */
    @ResponseBody
    @GetMapping("/{store_id}")
    public BaseResponse<GetStoreInfoRes> getStoreInfo(@PathVariable("store_id") int storeId) throws BaseException{

        GetStoreInfoRes result = storeProvider.getStoreInfo(storeId);

        return new BaseResponse<>(result);

    }

    /**
     * 상점 소개 수정 API
     * [PATCH] /stores
     * @return BaseResponse<PatchStoreProfileReq>
     */
    @ResponseBody
    @PatchMapping("")
    public BaseResponse<PatchStoreProfileRes> modifyStoreProfile(@ModelAttribute @Valid PatchStoreProfileReq storeProfile) throws BaseException{
        // jwt에서 id 추출
        int userId = jwtService.getUserId();

        PatchStoreProfileRes result = storeService.modifyStoreProfile(userId, storeProfile);

        return new BaseResponse<>(result);

    }

    /**
     * 상점 거래내역(판매) 조회 API
     * [GET] /stores/sales?type={type}
     * @return BaseResponse<List<TradeInfo>>
     */
    @ResponseBody
    @GetMapping("/sales")
    public BaseResponse<List<TradeInfo>> getSales(@RequestParam(value = "type", defaultValue = "All") String type) throws BaseException {
        //jwt 인증 및 userId 추출
        int userId= jwtService.getUserId();

        //필터링 문자 조율
        if (!type.equals("All")) {
            type = "\'" + type + "\'";
        }

        List<TradeInfo> result = storeProvider.getSales(userId, type);

        return new BaseResponse<>(result);
    }

    /**
     * 상점 거래내역(구매) 조회 API
     * [GET] /stores/purchases?type={type}
     * @return BaseResponse<List<TradeInfo>>
     */
    @ResponseBody
    @GetMapping("/purchases")
    public BaseResponse<List<TradeInfo>> getPurchases(@RequestParam(value = "type", defaultValue = "All") String type) throws BaseException {
        //jwt 인증 및 userId 추출
        int userId= jwtService.getUserId();

        //필터링 문자 조율
        if (!type.equals("All")) {
            type = "\'" + type + "\'";
        }

        List<TradeInfo> result = storeProvider.getPurchases(userId, type);

        return new BaseResponse<>(result);
    }

    /**
     * 상점 팔로우 API
     * [POST] /stores/{store-id}/followed
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/{store-id}/followed")
    public BaseResponse<String> modifyFollowing(@PathVariable("store-id") int followId) throws BaseException {
        // jwt에서 id 추출
        int userId = jwtService.getUserId();

        // 스스로 팔로우 불가
        if (userId == followId){
            return new BaseResponse<>(INVALID_ACCESS);
        }

        String result = storeService.modifyFollowing(userId, followId);

        return new BaseResponse<>(result);
    }

    /**
     * 상점 팔로잉 알림 설정 API
     * [PATCH] /stores/{store-id}/notified
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{store-id}/notified")
    public BaseResponse<String> modifyFollowingNotification(@PathVariable("store-id") int followId) throws BaseException {
        // jwt에서 id 추출
        int userId = jwtService.getUserId();

        // 자기 자신에 대해서 알림 불가
        if (userId == followId){
            return new BaseResponse<>(INVALID_ACCESS);
        }

        String result = storeService.modifyFollowingNotification(userId, followId);

        return new BaseResponse<>(result);
    }

    /**
     * 상점 팔로워 목록 조회 API
     * [GET] /stores/{store-id}/followers
     * @return BaseResponse<GetFollowRes>
     */
    @ResponseBody
    @GetMapping("/{store-id}/followers")
    public BaseResponse<GetFollowRes> getFollowers(@PathVariable("store-id") int storeId,
                                                   @RequestParam(value = "id", defaultValue = "0") int lastId,
                                                   @RequestParam(value = "size", defaultValue = "100") int size) throws BaseException {

        GetFollowRes followers = storeProvider.getFollowers(storeId, lastId, size);

        return new BaseResponse<>(followers);
    }

    /**
     * 상점 팔로잉 목록 조회 API
     * [GET] /stores/{store-id}/followings
     * @return BaseResponse<GetFollowRes>
     */
    @ResponseBody
    @GetMapping("/{store-id}/followings")
    public BaseResponse<GetFollowRes> getFollowings(@PathVariable("store-id") int storeId,
                                                        @RequestParam(value = "id", defaultValue = "0") int lastId,
                                                    @RequestParam(value = "size", defaultValue = "100") int size) throws BaseException {

        GetFollowRes followings = storeProvider.getFollowings(storeId, lastId, size);

        return new BaseResponse<>(followings);

    }

    /**
     * 계좌 목록 조회 API
     * [GET] /stores/accounts
     * @return BaseResponse<List<AccountInfo>>
     */
    @ResponseBody
    @GetMapping ("/accounts")
    public BaseResponse<List<AccountInfo>> getAccounts() throws BaseException {
        // jwt에서 id 추출
        int userId = jwtService.getUserId();

        List<AccountInfo> result = storeProvider.getAccounts(userId);

        return new BaseResponse<>(result);
    }

    /**
     * 계좌 추가 API
     * [POST] /stores/accounts
     * @return BaseResponse<Integer>
     */
    @ResponseBody
    @PostMapping("/accounts")
    public BaseResponse<Integer> registerAccount(@RequestBody @Valid PostAccountReq accountInfo) throws BaseException {
        // jwt에서 id 추출
        int userId = jwtService.getUserId();

        int result = storeService.registerAccount(userId, accountInfo);

        return new BaseResponse<>(result);
    }

    /**
     * 계좌 수정 API
     * [PUT] /stores/accounts/{account-id}
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PutMapping("/accounts/{account-id}")
    public BaseResponse<String> modifyAccount(@PathVariable("account-id") int accountId,
                                              @RequestBody @Valid PostAccountReq accountInfo) throws BaseException{
        // jwt에서 id 추출
        int userId = jwtService.getUserId();

        storeService.modifyAccount(userId, accountId, accountInfo);

        return new BaseResponse<>(UPDATE_SUCCESS);
    }

    /**
     * 계좌 삭제 API
     * [DELETE] /stores/accounts/{account-id}
     * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping("/accounts/{account-id}")
    public BaseResponse<String> removeAccount(@PathVariable("account-id") int accountId) throws BaseException {
        // jwt에서 id 추출
        int userId = jwtService.getUserId();

        storeService.removeAccount(userId, accountId);

        return new BaseResponse<>(DELETE_SUCCESS);
    }

    /**
     * 본인인증 정보 조회 API
     * [GET] /stores/{store-id}/authentication
     * @return BaseResponse<GetAuthenticationRes>
     */
    @ResponseBody
    @GetMapping ("/{store-id}/authentication")
    public BaseResponse<GetAuthenticationRes> getUserAuthentication(@PathVariable("store-id")int storeId) throws BaseException {

        GetAuthenticationRes result = storeProvider.getUserAuthentication(storeId);

        return new BaseResponse<>(result);
    }

}

    // 페이징 처리
//    /**
//     * 상점 거래내역(판매) 조회 API
//     * [POST] /stores/sales
//     * @return BaseResponse<GetSalesRes>
//     */
//    @ResponseBody
//    @GetMapping("/sales")
//    public BaseResponse<GetSalesRes> getSales(@RequestParam(value = "id", defaultValue = "0") int tradeId,
//                                         @RequestParam(value="date") String date,
//                                         @RequestParam(value = "size", defaultValue = "100") int size) throws BaseException {
//        //jwt 인증 및 userId 추출
//        int userId= jwtService.getUserId();
//
//        GetSalesRes result = storeProvider.getSales(userId, tradeId, date, size);
//
//        return new BaseResponse<>(result);
//    }

