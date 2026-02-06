package com.pocketmon.insightpocket.domain.laneige.controller;

import com.pocketmon.insightpocket.domain.laneige.dto.LaneigeProductIngestRequest;
import com.pocketmon.insightpocket.domain.laneige.dto.LaneigeProductIngestResponse;
import com.pocketmon.insightpocket.domain.laneige.service.LaneigeProductIngestService;
import com.pocketmon.insightpocket.global.response.ApiResponse;
import com.pocketmon.insightpocket.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "라네즈 크롤링 데이터 적재 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/laneige")
public class LaneigeProductIngestController {

    private final LaneigeProductIngestService laneigeProductIngestService;

    @Operation(
            summary = "라네즈 상품 배치 적재",
            description = """
                    크롤러가 수집한 라네즈 상품/스냅샷 데이터를 배치로 DB에 저장합니다.
                    - 요청은 배열(List) 형태로 전달합니다.
                    - 검증 실패 시 400(VALIDATION_FAILED) 응답을 반환합니다.
                    """
    )
    @PostMapping("/products/ingest")
    public ApiResponse<LaneigeProductIngestResponse> ingestProducts(
            @RequestBody @Valid List<@Valid LaneigeProductIngestRequest> requests
    ) {
        LaneigeProductIngestResponse res = laneigeProductIngestService.ingestBatch(requests);
        return ApiResponse.onSuccess(res, SuccessCode.OK);
    }
}