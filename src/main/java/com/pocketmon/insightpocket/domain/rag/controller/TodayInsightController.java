package com.pocketmon.insightpocket.domain.rag.controller;

import com.pocketmon.insightpocket.domain.rag.dto.TodayInsightResponse;
import com.pocketmon.insightpocket.domain.rag.service.TodayInsightService;
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
    public TodayInsightResponse getLatestInsight() {
        return todayInsightService.getLatestInsight();
    }
}