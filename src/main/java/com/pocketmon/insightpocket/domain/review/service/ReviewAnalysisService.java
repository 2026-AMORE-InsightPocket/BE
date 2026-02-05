package com.pocketmon.insightpocket.domain.review.service;

import com.pocketmon.insightpocket.domain.review.dto.ReviewAnalysisResponse;
import com.pocketmon.insightpocket.domain.review.repository.ReviewAnalysisQueryRepository;
import com.pocketmon.insightpocket.domain.review.dto.KeywordInsightRow;
import com.pocketmon.insightpocket.domain.review.dto.LatestReviewSnapshotRow;
import com.pocketmon.insightpocket.global.exception.CustomException;
import com.pocketmon.insightpocket.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewAnalysisService {

    private final ReviewAnalysisQueryRepository queryRepository;

    public ReviewAnalysisResponse getReviewAnalysis(Long productId) {
        // 최신 스냅샷 불러오기
        LatestReviewSnapshotRow snap =
                queryRepository.findLatestSnapshot(productId);

        if (snap == null) {
            throw new CustomException(ErrorCode.REVIEW_SNAPSHOT_NOT_FOUND);
        }

        List<KeywordInsightRow> aspects =
                queryRepository.findKeywordInsights(snap.getProductSnapshotId());

        // 긍·부정 감성 비율 계산
        long posSum = 0L;
        long negSum = 0L;
        for (KeywordInsightRow a : aspects) {
            posSum += nvl(a.getPositive());
            negSum += nvl(a.getNegative());
        }

        int positivePct = 0;
        int negativePct = 0;
        long denom = posSum + negSum;
        if (denom > 0) {
            positivePct = (int) Math.round(posSum * 100.0 / denom);
            negativePct = 100 - positivePct;
        }

        // 평판 점수 계산
        int score;
        if (denom > 0) {
            score = clamp(positivePct, 0, 100);
        } else if (snap.getRating() != null) {
            score = clamp((int) Math.round(snap.getRating() * 20.0), 0, 100);
        } else {
            score = 0;
        }

        // 별점 분포
        List<ReviewAnalysisResponse.RatingDistItem> ratingDist = List.of(
                new ReviewAnalysisResponse.RatingDistItem(5, toIntOrNull(snap.getRating5Pct())),
                new ReviewAnalysisResponse.RatingDistItem(4, toIntOrNull(snap.getRating4Pct())),
                new ReviewAnalysisResponse.RatingDistItem(3, toIntOrNull(snap.getRating3Pct())),
                new ReviewAnalysisResponse.RatingDistItem(2, toIntOrNull(snap.getRating2Pct())),
                new ReviewAnalysisResponse.RatingDistItem(1, toIntOrNull(snap.getRating1Pct()))
        );

        // 키워드 기반 인사이트
        List<ReviewAnalysisResponse.KeywordInsightItem> keywordInsights = new ArrayList<>();
        for (KeywordInsightRow a : aspects) {
            long p = nvl(a.getPositive());
            long n = nvl(a.getNegative());

            int aspectScore = 0;
            long d = p + n;
            if (d > 0) aspectScore = (int) Math.round(p * 100.0 / d);

            keywordInsights.add(new ReviewAnalysisResponse.KeywordInsightItem(
                    a.getAspectName(),
                    a.getMentionTotal(),
                    aspectScore,
                    a.getSummary(),
                    a.getPositive(),
                    a.getNegative()
            ));
        }

        return new ReviewAnalysisResponse(
                productId,
                snap.getSnapshotTime(),
                new ReviewAnalysisResponse.CustomersSay(
                        snap.getReviewCount(),
                        snap.getCustomersSayCurrent(),
                        snap.getCustomersSayHighlight(),
                        snap.getCustomersSayUpdatedAt()
                ),
                new ReviewAnalysisResponse.Reputation(
                        score,
                        snap.getRating(),
                        snap.getReviewCount()
                ),
                new ReviewAnalysisResponse.Sentiment(
                        positivePct,
                        negativePct
                ),
                ratingDist,
                keywordInsights
        );
    }

    private long nvl(Long v) {
        return v == null ? 0L : v;
    }

    private Integer toIntOrNull(Long v) {
        if (v == null) return null;
        if (v > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        return v.intValue();
    }

    private int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
}