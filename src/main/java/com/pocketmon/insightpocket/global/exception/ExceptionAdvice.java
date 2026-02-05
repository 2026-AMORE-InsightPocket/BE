package com.pocketmon.insightpocket.global.exception;

import com.pocketmon.insightpocket.global.response.ApiResponse;
import com.pocketmon.insightpocket.global.response.BaseCode;
import com.pocketmon.insightpocket.global.response.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class ExceptionAdvice {

    private final List<DataIntegrityMapper> dataIntegrityMappers;

    public ExceptionAdvice(Optional<List<DataIntegrityMapper>> mappers) {
        this.dataIntegrityMappers = mappers.orElseGet(List::of);
    }

    // @RequestParam, @PathVariable 등 Bean Validation 실패
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(v -> v.getMessage())
                .findFirst()
                .orElse("잘못된 요청입니다.");

        return ResponseEntity
                .status(ErrorCode.VALIDATION_FAILED.getHttpStatus())
                .body(ApiResponse.onFailure(ErrorCode.VALIDATION_FAILED, message));
    }

    // @Valid @RequestBody DTO 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        Map<String, String> errors = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(fe -> {
            String msg = Optional.ofNullable(fe.getDefaultMessage()).orElse("");
            errors.merge(fe.getField(), msg, (a, b) -> a + ", " + b);
        });

        return ResponseEntity
                .status(ErrorCode.VALIDATION_FAILED.getHttpStatus())
                .body(ApiResponse.onFailure(ErrorCode.VALIDATION_FAILED, errors));
    }

    // @RequestBody JSON 파싱 실패
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        return ResponseEntity
                .status(ErrorCode.JSON_PARSE_ERROR.getHttpStatus())
                .body(ApiResponse.onFailure(ErrorCode.JSON_PARSE_ERROR, null));
    }

    // 도메인 CustomException
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(CustomException e) {
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(ApiResponse.onFailure(e.getErrorCode(), e.getMessage()));
    }

    // 데이터 무결성 제약 조건 핸들러
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        // 기본은 메시지
        String key = Optional.ofNullable(e.getMostSpecificCause()).map(Throwable::getMessage).orElse("");

        // 원인 체인을 끝까지 훑어서 constraint name 우선 추출
        Throwable t = e;
        while (t != null) {
            if (t instanceof org.hibernate.exception.ConstraintViolationException cve) {
                String constraintName = cve.getConstraintName();
                if (constraintName != null && !constraintName.isBlank()) {
                    key = constraintName;
                    break;
                }
            }
            t = t.getCause();
        }

        for (DataIntegrityMapper mapper : dataIntegrityMappers) {
            if (mapper.supports(key)) {
                BaseCode code = mapper.errorCode();
                return ResponseEntity
                        .status(code.getHttpStatus())
                        .body(ApiResponse.onFailure(code, null));
            }
        }

        // 매핑 못한 경우 (공통 fallback)
        return ResponseEntity
                .status(ErrorCode.DATA_INTEGRITY_VIOLATION.getHttpStatus())
                .body(ApiResponse.onFailure(ErrorCode.DATA_INTEGRITY_VIOLATION, null));
    }

    // 모든 미처리 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleUnknownException(Exception e) {
        log.error("Unhandled exception", e);
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(ApiResponse.onFailure(ErrorCode.INTERNAL_SERVER_ERROR, null));
    }
}