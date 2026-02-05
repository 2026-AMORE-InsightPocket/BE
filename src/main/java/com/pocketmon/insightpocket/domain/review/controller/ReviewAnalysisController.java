package com.pocketmon.insightpocket.domain.review.controller;

import com.pocketmon.insightpocket.domain.review.dto.ReviewAnalysisResponse;
import com.pocketmon.insightpocket.domain.review.service.ReviewAnalysisService;
import com.pocketmon.insightpocket.global.response.ApiResponse;
import com.pocketmon.insightpocket.global.response.SuccessCode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "리뷰 분석 조회 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/laneige/products")
public class ReviewAnalysisController {

    private final ReviewAnalysisService reviewAnalysisService;

    @Operation(
            summary = "상품 리뷰 분석 조회",
            description = """
                    특정 상품의 최신 리뷰 스냅샷을 기준으로
                    - 고객 요약(Customers Say)
                    - 평판 점수(Reputation Score)
                    - 긍·부정 감성 비율(Sentiment)
                    - 별점 분포(Rating Distribution)
                    - 키워드 기반 인사이트(Keyword Insights)
                    를 종합 분석한 결과를 반환합니다.
                    """
    )
    @GetMapping("/{id}/review-analysis")
    public ApiResponse<ReviewAnalysisResponse> getReviewAnalysis(
            @PathVariable("id") Long productId
    ) {
        return ApiResponse.onSuccess(
                reviewAnalysisService.getReviewAnalysis(productId),
                SuccessCode.OK
        );
    }
}