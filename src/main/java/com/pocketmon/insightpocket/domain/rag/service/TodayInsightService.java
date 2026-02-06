package com.pocketmon.insightpocket.domain.rag.service;

import com.pocketmon.insightpocket.domain.rag.dto.TodayInsightResponse;
import com.pocketmon.insightpocket.domain.rag.entity.RagDoc;
import com.pocketmon.insightpocket.domain.rag.repository.RagDocRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TodayInsightService {

    private final DailyReportService dailyReportService;

    public TodayInsightResponse getLatestInsight() {
        RagDoc doc = dailyReportService.getLatestDailyReport();

        String insight = extractInsightContent(doc.getBodyMd());

        return new TodayInsightResponse(
                doc.getDocId(),
                doc.getReportDate(),
                insight
        );
    }

    // 오늘의 인사이트 문구
    private String extractInsightContent(String md) {
        if (md == null) return "";

        String normalized = md.replace("\r\n", "\n");

        for (String line : normalized.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            String marker = "**오늘의 인사이트:**";
            if (trimmed.startsWith(marker)) {
                return trimmed.substring(marker.length()).trim();
            }

            String plainMarker = "오늘의 인사이트:";
            if (trimmed.startsWith(plainMarker)) {
                return trimmed.substring(plainMarker.length()).trim();
            }

            return trimmed;
        }

        return "";
    }
}