package com.pocketmon.insightpocket.domain.dashboard.controller;

import com.pocketmon.insightpocket.domain.dashboard.dto.BestsellerTop1Response;
import com.pocketmon.insightpocket.domain.dashboard.dto.BestsellerTop5Response;
import com.pocketmon.insightpocket.domain.dashboard.dto.RisingProductResponse;
import com.pocketmon.insightpocket.domain.dashboard.service.BestsellerTop1Service;
import com.pocketmon.insightpocket.domain.dashboard.service.BestsellerTop5Service;
import com.pocketmon.insightpocket.domain.dashboard.service.RisingProductService;
import com.pocketmon.insightpocket.global.response.ApiResponse;
import com.pocketmon.insightpocket.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "요약 대시보드 조회 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final RisingProductService risingProductService;
    private final BestsellerTop5Service bestsellerTop5Service;
    private final BestsellerTop1Service bestsellerTop1Service;

    @Operation(
            summary = "랭킹 급상승 상품 조회",
            description = """
                        최근 두 스냅샷을 기준으로 랭킹 상승폭이 가장 큰 상품을 조회합니다.
                        - 랭킹이 일정 이상 상승한 상품이 없을 경우 item은 null로 반환됩니다.
                        - 대시보드의 '급상승한 제품' 카드에 사용됩니다.
                        """
    )
    @GetMapping(
            "/rising")
    public ApiResponse<RisingProductResponse> getRisingProducts() {
        return ApiResponse.onSuccess(
                risingProductService.getRisingProducts(),
                SuccessCode.OK
        );
    }

    @Operation(
            summary = "지난 달 베스트셀러 TOP 5 조회",
            description = """
                        지정한 월(yyyy-MM)의 마지막 스냅샷을 기준으로 Laneige 브랜드의 베스트셀러 TOP 5 상품을 조회합니다.
                        
                        - 전월 랭킹과 비교한 순위 변동(rank_change)을 함께 제공합니다.
                        - 데이터가 없는 경우 items는 빈 배열로 반환됩니다.
                        """
    )
    @GetMapping("/bestsellers/top5")
    public ApiResponse<BestsellerTop5Response> top5(
            @RequestParam String month
    ) {
        return ApiResponse.onSuccess(
                bestsellerTop5Service.getLaneigeTop5(month),
                SuccessCode.OK
        );
    }

    @Operation(
            summary = "지난 달 매출 1위 상품 조회",
            description = """
                        월별 베스트셀러 TOP 5 중 1위 상품을 조회합니다.
                        
                        - 내부적으로 TOP5 조회 로직을 재사용합니다.
                        - 해당 월에 데이터가 없을 경우 winner는 null로 반환됩니다.
                        - 대시보드의 '지난 달 매출 1위' 카드에 사용됩니다.
                        """
    )
    @GetMapping("/bestsellers/top1")
    public ApiResponse<BestsellerTop1Response> top1(
            @RequestParam String month
    ) {
        return ApiResponse.onSuccess(
                bestsellerTop1Service.getLastMonthWinner(month),
                SuccessCode.OK
        );
    }
}