package com.example.demo.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 2XX : 요청 성공
     */
    SUCCESS_WITH_DATA(true, 200, "요청에 성공하였습니다."),
    SUCCESS_WITH_NO_DATA(true, 204, "요청에 성공하였습니다."),
    INSERT_SUCCESS(true, 201, "등록되었습니다."),
    DELETE_SUCCESS(true, 202, "삭제되었습니다."),
    UPDATE_SUCCESS(true, 203, "수정되었습니다."),


    /**
     * 3XX: 리다이렉션
     */
    // 공통



    /**
     * 4XX : 클라이언트 에러
     */
    // 공통
    INVALID_REQUEST_FIELD(false, 400, ""),
    RESPONSE_ERROR(false, 404, "존재하지 않는 리소스입니다."),

    EMPTY_JWT(false, 401, "JWT를 입력해주세요."),
    INVALID_JWT(false, 403, "유효하지 않은 JWT입니다."),
    INVALID_ACCESS(false, 405, "잘못된 요청입니다."),

    INSERT_FAIL(true, 408, "등록에 실패했습니다."),
    DELETE_FAIL(true, 409, "삭제에 실패했습니다."),
    UPDATE_FAIL(true, 410, "수정에 실패했습니다."),

    // User
    INVALID_EMAIL_AUTH(false, 402, "유효한 인증이 아닙니다."),
    FAIL_LOGIN(false, 407, "로그인에 실패했습니다."),

    // 410-50 미치
    DUPLICATE_STORE_NAME(false, 411, "사용 중인 상점명입니다."),
    FAIL_FOLLOW_STORE(false, 412, "팔로우 실패했습니다."),
    FAIL_GET_WISHES(false, 413, "관심상품 조회에 실패했습니다."),
    FAIL_GET_REVIEWS(false, 414, "거래 후기 조회에 실패했습니다."),
    FAIL_GET_STOREINFO(false, 415, "상점 정보 조회에 실패했습니다."),
    FAIL_GET_SALES(false, 416, "거래내역(판매) 조회에 실패했습니다."),
    FAIL_GET_PURCHASE(false, 417, "거래내역(구매) 조회에 실패했습니다."),
    FAIL_NOTIFY_STORE(false, 418, "알림을 설정하지 못했습니다."),
    INVALID_REVIEWER(false, 419, "거래 후기 작성 권한이 없습니다."),
    REGISTERED_REVIEW(false, 420, "이미 작성한 후기입니다."),
    FAIL_REGISTER_REVIEW(false, 421, "거래후기 등록에 실패했습니다."),
    FAIL_REMOVE_REVIEW(false, 422, "거래후기 삭제에 실패했습니다."),
    EMPTY_HEADER_TRADE_ID(false, 400, "거래 식별번호(Id)를 입력하세요."),
    FAIL_MODIFY_REVIEW(false, 423, "거래후기 수정에 실패했습니다."),
    DUPLICATE_ACCOUNT(false, 424, "이미 등록된 계좌입니다."),
    EXCEEDED_ACCOUNT(false, 425, "최대 등록 가능한 계좌는 2개입니다."),
    FAIL_GET_ACCOUNTS(false, 426, "계좌 목록 조회에 실패했습니다."),
    FAIL_DELETE_ACCOUNT(false, 427, "계좌 삭제에 실패했습니다."),
    FAIL_GET_USER_AUTHENTICATION(false, 428, "본인인증 정보 조회에 실패했습니다."),


    // 451-499 조이
    EMPTY_IMAGE_ERROR(false, 452, "이미지가 입력되지 않았습니다"),
    NON_EXISTENT_PRODUCT(false, 453, "존재하지 않는 상품입니다."),
    NO_MORE_SUBCATEGORY(false, 454, "하위 카테고리가 존재하지 않습니다."),

    INVALID_AMOUNT(false, 455, "개수는 1개 이상이어야 합니다."),

    /**
     * 5XX : Server 에러
     */
    DATABASE_ERROR(false, 500, "Database 오류"),
    SERVER_ERROR(false, 503, "서버와 연결에 실패했습니다."),
    FAIL_KAKAO_API(false, 512, "카카오 사용자 정보 조회에 실패했습니다."),
    FAIL_SEND_AUTHMAIL(false, 513, "인증메일 발송에 실패했습니다."),

    // 520-60 미치


    // 561-599 조이
    S3_UPLOAD_ERROR(false, 561, "S3 업로드에 실패했습니다."),
    S3_DELETE_ERROR(false, 562, "S3 파일 삭제에 실패했습니다."),
    S3_UPDATE_ERROR(false, 563, "S3 파일 수정에 실패했습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
