package com.pocketmon.insightpocket.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ReviewAnalysisResponse {

    private Long productId;
    private LocalDateTime snapshotTime;  // "이번 분석 기준 시점" (마지막 스냅샷 시간)

    private CustomersSay customersSay;   // Customers Say 카드
    private Reputation reputation;       // 평판 지수 카드(점수/별점/리뷰수)
    private Sentiment sentiment;         // 감정 분석 분포(도넛)
    private List<RatingDistItem> ratingDistribution; // 평점 분포(막대)
    private List<KeywordInsightItem> keywordInsights; // AI 키워드 분석 리스트

    @Getter @AllArgsConstructor
    public static class CustomersSay {
        private Long reviewCount;
        private String currentText;       // laneige_products.customers_say_current
        private String highlight;         // laneige_product_snapshots.customers_say (있으면) or null
        private LocalDateTime updatedAt;  // laneige_products.customers_say_updated_at
    }

    @Getter @AllArgsConstructor
    public static class Reputation {
        private Integer score; // 0~100 (계산)
        private Double rating; // 평균 별점
        private Long reviewCount;
    }

    @Getter @AllArgsConstructor
    public static class Sentiment {
        private Integer positivePct;
        private Integer negativePct;
    }

    @Getter @AllArgsConstructor
    public static class RatingDistItem {
        private Integer star;   // 5,4,3,2,1
        private Integer pct;    // snapshot의 rating_5_pct ...
    }

    @Getter @AllArgsConstructor
    public static class KeywordInsightItem {
        private String aspectName;
        private Long mentionTotal;
        private Integer score;      // 긍/부 기반 계산(0~100)
        private String summary;     // laneige_aspect_details.summary
        private Long mentionPositive;
        private Long mentionNegative;
    }
}