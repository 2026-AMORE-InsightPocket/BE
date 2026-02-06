package com.pocketmon.insightpocket.domain.rag.controller;

import com.pocketmon.insightpocket.domain.rag.dto.TodayInsightResponse;
import com.pocketmon.insightpocket.domain.rag.service.TodayInsightService;
import com.pocketmon.insightpocket.global.response.ApiResponse;
import com.pocketmon.insightpocket.global.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class TodayInsightController {

    private final TodayInsightService todayInsightService;

    @GetMapping("/today/insight")
    public ApiResponse<TodayInsightResponse> getLatestInsight() {
        return ApiResponse.onSuccess(
                todayInsightService.getLatestInsight(),
                SuccessCode.OK
        );
    }
}