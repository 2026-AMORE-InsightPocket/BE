package com.pocketmon.insightpocket.global.exception;

import com.pocketmon.insightpocket.global.response.BaseCode;

// 데이터 무결성 제약 조건 터졌을 때 상속받아 사용
public interface DataIntegrityMapper {
    boolean supports(String key);
    BaseCode errorCode();
}