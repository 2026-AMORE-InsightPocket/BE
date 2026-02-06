package com.pocketmon.insightpocket.domain.laneige.controller;

import com.pocketmon.insightpocket.domain.laneige.dto.LaneigeProductsResponse;
import com.pocketmon.insightpocket.domain.laneige.dto.RankTrendResponse;
import com.pocketmon.insightpocket.domain.laneige.enums.RankRange;
import com.pocketmon.insightpocket.domain.laneige.service.LaneigeProductService;
import com.pocketmon.insightpocket.domain.laneige.service.LaneigeRankTrendService;
import com.pocketmon.insightpocket.global.response.ApiResponse;
import com.pocketmon.insightpocket.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "라네즈 제품 조회 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/laneige/products")
public class LaneigeProductController {

    private final LaneigeProductService laneigeProductService;
    private final LaneigeRankTrendService rankTrendService;

    @Operation(
            summary = "라네즈 제품 목록 조회",
            description = """
                    최신 스냅샷 기준으로 라네즈 상품 목록을 조회합니다.
                    - 반환 데이터에는 상품 기본 정보 + 최신 스냅샷 정보(가격/랭킹 등)이 포함됩니다.
                    """
    )
    @GetMapping("")
    public ApiResponse<LaneigeProductsResponse> getLaneigeProducts() {
        return ApiResponse.onSuccess(
                laneigeProductService.getLaneigeProducts(),
                SuccessCode.OK
        );
    }

    @Operation(
            summary = "라네즈 제품 순위 변동 추이 조회",
            description = """
                    특정 상품의 랭킹 변화를 기간(range) 단위로 조회합니다.
                    - range 값에 따라 조회 기간/구간이 달라집니다.
                    """
    )
    @GetMapping("/{id}/rank-trends")
    public ApiResponse<RankTrendResponse> getRankTrends(
            @Parameter(description = "상품 ID", example = "1", required = true)
            @PathVariable("id") Long productId,

            @Parameter(description = "조회 기간 범위", required = true)
            @RequestParam RankRange range
    ) {
        return ApiResponse.onSuccess(
                rankTrendService.getRankTrends(productId, range),
                SuccessCode.OK
        );
    }
}