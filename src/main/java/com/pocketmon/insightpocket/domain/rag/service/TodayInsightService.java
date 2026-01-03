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

    private static final String DAILY_REPORT_CODE = "DAILY_REPORT";

    private final RagDocRepository ragDocRepository;

    public TodayInsightResponse getLatestInsight() {
        RagDoc doc = ragDocRepository
                .findLatestOneByDocTypeCode(DAILY_REPORT_CODE)
                .orElseThrow(() -> new NoSuchElementException("데일리 리포트가 없습니다."));

        String insight = extractInsightContent(doc.getBodyMd());

        return new TodayInsightResponse(
                doc.getDocId(),
                doc.getReportDate(),
                insight
        );
    }

    private String extractInsightContent(String md) {
        if (md == null) return "";

        String normalized = md.replace("\r\n", "\n");

        for (String line : normalized.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            // "**오늘의 인사이트:**" 뒤만 잘라내기
            String marker = "**오늘의 인사이트:**";
            if (trimmed.startsWith(marker)) {
                return trimmed.substring(marker.length()).trim();
            }

            // 혹시 마크다운 없이 저장된 경우 대비
            String plainMarker = "오늘의 인사이트:";
            if (trimmed.startsWith(plainMarker)) {
                return trimmed.substring(plainMarker.length()).trim();
            }

            // fallback: 그냥 첫 문장
            return trimmed;
        }

        return "";
    }
}