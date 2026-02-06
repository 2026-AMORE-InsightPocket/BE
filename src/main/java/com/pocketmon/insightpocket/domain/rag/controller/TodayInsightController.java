package com.pocketmon.insightpocket.domain.rag.controller;

import com.pocketmon.insightpocket.domain.rag.dto.TodayInsightResponse;
import com.pocketmon.insightpocket.domain.rag.service.TodayInsightService;
import com.pocketmon.insightpocket.global.response.ApiResponse;
import com.pocketmon.insightpocket.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "오늘의 인사이트 조회 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class TodayInsightController {

    private final TodayInsightService todayInsightService;

    @Operation(
            summary = "오늘의 인사이트 조회",
            description = "오늘 생성된 최신 Daily Report 기반 인사이트를 조회합니다."
    )
    @GetMapping("/today/insight")
    public ApiResponse<TodayInsightResponse> getLatestInsight() {
        return ApiResponse.onSuccess(
                todayInsightService.getLatestInsight(),
                SuccessCode.OK
        );
    }
}