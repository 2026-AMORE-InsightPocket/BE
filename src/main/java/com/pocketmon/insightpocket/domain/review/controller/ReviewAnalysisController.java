package com.pocketmon.insightpocket.domain.review.controller;

import com.pocketmon.insightpocket.domain.review.dto.ReviewAnalysisResponse;
import com.pocketmon.insightpocket.domain.review.service.ReviewAnalysisService;
import com.pocketmon.insightpocket.global.response.ApiResponse;
import com.pocketmon.insightpocket.global.response.SuccessCode;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/laneige/products")
public class ReviewAnalysisController {

    private final ReviewAnalysisService reviewAnalysisService;

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