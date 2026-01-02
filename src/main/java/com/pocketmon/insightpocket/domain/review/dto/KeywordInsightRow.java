package com.pocketmon.insightpocket.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KeywordInsightRow {
    private String aspectName;
    private Long mentionTotal;
    private Long positive;
    private Long negative;
    private String summary;
}