package com.pocketmon.insightpocket.domain.rag.dto;

import java.time.LocalDate;

public record TodayInsightResponse(
        String docId,
        LocalDate reportDate,
        String insight
) {
}