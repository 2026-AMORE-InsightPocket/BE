package com.pocketmon.insightpocket.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseCode {

    // 공통
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-001", "서버 오류가 발생했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON-002", "요청 파라미터가 올바르지 않습니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "COMMON-003", "입력값 검증에 실패했습니다."),
    JSON_PARSE_ERROR(HttpStatus.BAD_REQUEST, "COMMON-004", "JSON 파싱에 실패했습니다."),
    DATA_INTEGRITY_VIOLATION(HttpStatus.CONFLICT, "COMMON-005", "데이터 무결성 제약조건 위반입니다."),

    // 인증
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH-001", "인증에 실패했습니다."),
    INGEST_API_KEY_NOT_CONFIGURED(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH-002", "서버 인증 설정이 누락되었습니다."),

    // 라네즈 제품
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT-001", "상품이 존재하지 않습니다."),

    // 리뷰 분석
    REVIEW_SNAPSHOT_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW-001", "해당 상품의 리뷰 스냅샷이 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}