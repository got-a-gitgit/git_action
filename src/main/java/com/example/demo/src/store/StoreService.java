package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.src.store.model.PatchStoreProfileReq;
import com.example.demo.src.store.model.PatchStoreProfileRes;
import com.example.demo.src.store.model.PostAccountReq;
import com.example.demo.utils.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.example.demo.config.BaseResponseStatus.*;


@Service
public class StoreService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final StoreDao storeDao;
    private final StoreProvider storeProvider;
    private final S3Service s3Service;


    @Autowired
    public StoreService(StoreDao storeDao, StoreProvider storeProvider, S3Service s3Service) {
        this.storeDao = storeDao;
        this.storeProvider = storeProvider;
        this.s3Service = s3Service;

    }


    /** 상점명 설정 **/
    public String registerStoreName(int userId, String name) throws BaseException {
        // 상점명 중복 확인
        int duplicated = storeProvider.checkDuplicatesStoreName(userId, name);
        if (duplicated == 1){
            throw new BaseException(DUPLICATE_STORE_NAME);
        }

        try {
            int result = storeDao.insertStoreName(userId, name);
            if (result == 0) {  // 등록 실패
                throw new BaseException(INSERT_FAIL);
            }
            return name;

        } catch (BaseException e){
            throw new BaseException(e.getStatus());
        } catch (Exception e){
            logger.error("RegisterStoreName Error", e);
            throw new BaseException(INSERT_FAIL);
        }
    }

    /** 상점 소개 편집 **/
    @Transactional(rollbackFor = Exception.class)
    public PatchStoreProfileRes modifyStoreProfile(int userId, PatchStoreProfileReq storeProfile) throws BaseException {
        // 프로필 이미지 정보
        String originImageUrl = storeProfile.getOriginImageUrl();    // 기존의 이미지
        MultipartFile newImageFile = storeProfile.getNewImageFile(); // 업로드한 이미지
        String profileImageUrl;

        // 프로필 이미지 수정
        if (newImageFile != null) { // 새로운 프로필 이미지를 등록하는 경우
            profileImageUrl = s3Service.updateImage(originImageUrl, newImageFile);
        } else {    // 기존의 이미지를 삭제하는 경우
            if (originImageUrl != null){
                s3Service.deleteImage(originImageUrl);
            }
            profileImageUrl = null;
        }

        // DB에 저장할 이미지 Url 설정
        storeProfile.setOriginImageUrl(profileImageUrl);

        try {
            // 상점 소개 수정
            int result = storeDao.updateStoreProfile(userId, storeProfile);
            if (result == 0) {  // 수정 실패
                throw new BaseException(UPDATE_FAIL);
            }
            return storeProvider.getStoreProfile(userId);

        } catch (BaseException e){
            throw new BaseException(e.getStatus());
        } catch (Exception e){
            logger.error("ModifyStoreProfile Error", e);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 상점 팔로우/언팔로우 **/
    public String modifyFollowing(int userId, int followId) throws BaseException {
        // 유효한 (팔로우)유저인지 확인
        int isUser = storeProvider.checkUserId(followId);
        if (isUser == 0) {
            throw new BaseException(INVALID_ACCESS);
        }

        // 팔로우 관계 처리
        try {
            return storeDao.insertFollowing(userId, followId);
        } catch (Exception e){
            logger.error("Follow Error", e);
            throw new BaseException(FAIL_FOLLOW_STORE);
        }
    }

    /** 상점 팔로잉 알림 설정 **/
    public String modifyFollowingNotification(int userId, int followId) throws BaseException {
        // 유효한 (팔로우)유저인지 확인
        int isUser = storeProvider.checkUserId(followId);
        int isRelationship = storeProvider.checkFollowing(userId, followId);
        if (isUser == 0  || isRelationship == 0) {
            throw new BaseException(INVALID_ACCESS);
        }

        // 팔로잉 알림 처리
        try {
            String result = storeDao.updateFollowingNotification(userId, followId);

            return result;
        } catch (Exception e){
            logger.error("FollowingNotify Error", e);
            throw new BaseException(FAIL_NOTIFY_STORE);
        }
    }

    /** 계좌 등록 **/
    @Transactional(rollbackFor = Exception.class)
    public int registerAccount(int userId, PostAccountReq accountInfo) throws BaseException {

        // 계좌 중복 확인
        int isDuplicated = storeProvider.getDuplicatedAccount(accountInfo.getBankId(), accountInfo.getAccountNumber());
        if (isDuplicated == 1){
            throw new BaseException(DUPLICATE_ACCOUNT);
        }

        // 최대 계좌 수를 등록했는지 확인
        int isMax = storeProvider.getMaxAccount(userId);
        if (isMax == 2){    // 최대 계좌 수 2개가 입력됨
            throw new BaseException(EXCEEDED_ACCOUNT);
        } else if (isMax == 1 ){  // 이미 계좌가 1개 등록된 상태에서 기본계좌를 변경하는 경우
            if (accountInfo.getDefaultFlag().equals("Y")) {
                int mainAccountId = storeProvider.getMainAccount(userId);
                storeDao.updateAccountDefaultValue(mainAccountId, "N");
            }
        } else {    // 첫 계좌
            accountInfo.setDefaultFlag("Y");
        }

        try {
            return storeDao.insertAccount(userId, accountInfo);
        } catch (Exception e){
            logger.error("RegisterAccount Error", e);
            throw new BaseException(INSERT_FAIL);
        }

    }

    /** 계좌 수정 **/
    @Transactional(rollbackFor = Exception.class)
    public void modifyAccount(int userId, int accountId, PostAccountReq accountInfo) throws BaseException {
        // 계좌 유효성 확인
        int isOk = storeProvider.getAvailableAccount(userId, accountId);
        if (isOk == 0){
            throw new BaseException(INVALID_ACCESS);
        }

        String remainAccountNumber = storeProvider.getAccountNumber(accountId);
        if (!accountInfo.getAccountNumber().equals(remainAccountNumber)){
            // 계좌 중복 확인
            int isDuplicated = storeProvider.getDuplicatedAccount(accountInfo.getBankId(), accountInfo.getAccountNumber());
            if (isDuplicated == 1){
                throw new BaseException(DUPLICATE_ACCOUNT);
            }
        }

        // 최대 계좌 수를 등록했는지 확인
        int isMax = storeProvider.getMaxAccount(userId);
        if (isMax == 2) {    // 최대 계좌 수 2개가 입력됨
            int mainAccountId = storeProvider.getMainAccount(userId);
            // 수정 계좌의 기본 계좌 설정 해제 시
            if (mainAccountId == accountId && accountInfo.getDefaultFlag().equals("N")){
                int newMainAccountId = storeProvider.getNotMainAccount(userId);
                storeDao.updateAccountDefaultValue(newMainAccountId, "Y");
            }
            // 수정 계좌를 기본 계좌로 변경 시
            if (mainAccountId != accountId && accountInfo.getDefaultFlag().equals("Y")){
                storeDao.updateAccountDefaultValue(mainAccountId, "N");
            }
        }

        try {
            storeDao.updateAccount(accountId, accountInfo);
        } catch (Exception e){
            logger.error("ModifyAccount Error", e);
            throw new BaseException(UPDATE_FAIL);
        }
    }

    /** 계좌 삭제 **/
    public void removeAccount(int userId, int accountId) throws BaseException{
        // 계좌 유효성 확인
        int isOk = storeProvider.getAvailableAccount(userId, accountId);
        if (isOk == 0){
            throw new BaseException(INVALID_ACCESS);
        }

        try {
            storeDao.deleteAccount(accountId);
        } catch (Exception e){
            logger.error("RemoveAccount Error", e);
            throw new BaseException(FAIL_DELETE_ACCOUNT);
        }
    }

}
