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

    // 크롤링 데이터 DB 적재
    INGEST_BAD_REQUEST(HttpStatus.BAD_REQUEST, "INGEST-400", "크롤링 적재 요청 데이터가 올바르지 않습니다."),
    INGEST_EMPTY_PAYLOAD(HttpStatus.BAD_REQUEST, "INGEST-400-1", "적재 요청 목록이 비어있습니다."),
    INGEST_INVALID_SNAPSHOT_TIME(HttpStatus.BAD_REQUEST, "INGEST-400-2", "snapshot_time 형식이 올바르지 않습니다. (yyyy-MM-dd HH:mm:ss)"),
    INGEST_SNAPSHOT_TIME_MISMATCH(HttpStatus.BAD_REQUEST, "INGEST-400-3", "요청 내 snapshot_time 값이 서로 일치하지 않습니다."),
    INGEST_ITEMS_EMPTY(HttpStatus.BAD_REQUEST, "INGEST-400-4", "적재 대상 items가 비어있습니다."),
    INGEST_DUPLICATE_SNAPSHOT(HttpStatus.CONFLICT, "INGEST-409", "이미 동일한 스냅샷 데이터가 존재합니다."),
    INGEST_DATA_INCONSISTENT(HttpStatus.CONFLICT, "INGEST-409-1", "크롤링 데이터 간 정합성이 맞지 않습니다."),
    INGEST_TARGET_NOT_FOUND(HttpStatus.NOT_FOUND, "INGEST-404", "적재 대상 리소스를 찾을 수 없습니다."),
    INGEST_PARTIAL_FAILURE(HttpStatus.MULTI_STATUS, "INGEST-207", "일부 데이터 적재에 실패했습니다."),
    INGEST_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "INGEST-500", "크롤링 데이터 적재 중 오류가 발생했습니다."),

    // 인증
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH-401", "인증에 실패했습니다."),
    INGEST_API_KEY_NOT_CONFIGURED(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH-500", "서버 인증 설정이 누락되었습니다."),

    // 랭킹
    RANKING_NOT_FOUND(HttpStatus.NOT_FOUND, "RANKING-404", "랭킹이 존재하지 않습니다."),

    // 라네즈 제품
    LANEIGE_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "LANEIGE-404", "존재하지 않는 상품입니다."),
    LANEIGE_INVALID_RANGE(HttpStatus.BAD_REQUEST, "LANEIGE-400", "유효하지 않은 조회 범위입니다."),

    // 리뷰 분석
    REVIEW_SNAPSHOT_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW-404", "해당 상품의 리뷰 스냅샷이 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}