package com.pocketmon.insightpocket.domain.review.repository.projection;

public interface KeywordInsightRow {

    String getAspectName();
    Long getMentionTotal();
    Long getPositive();
    Long getNegative();
    String getSummary();
}