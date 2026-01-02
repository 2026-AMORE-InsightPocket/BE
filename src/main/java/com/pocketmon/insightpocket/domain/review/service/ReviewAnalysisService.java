package com.pocketmon.insightpocket.domain.review.service;

import com.pocketmon.insightpocket.domain.review.dto.ReviewAnalysisResponse;
import com.pocketmon.insightpocket.domain.review.repository.ReviewAnalysisRepository;
import com.pocketmon.insightpocket.domain.review.repository.ReviewSnapshotRepository;
import com.pocketmon.insightpocket.domain.review.repository.projection.KeywordInsightRow;
import com.pocketmon.insightpocket.domain.review.repository.projection.LatestReviewSnapshotRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewAnalysisService {

    private final ReviewSnapshotRepository reviewSnapshotRepository;
    private final ReviewAnalysisRepository reviewAnalysisRepository;

    public ReviewAnalysisResponse getReviewAnalysis(Long productId) {
        LatestReviewSnapshotRow snap = reviewSnapshotRepository.findLatestSnapshot(productId)
                .orElseThrow(() -> new IllegalArgumentException("No snapshot for productId=" + productId));

        List<KeywordInsightRow> aspects = reviewAnalysisRepository.findKeywordInsights(snap.getSnapshotId());

        // 1) sentiment 계산 (aspect 긍/부 합)
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

        // 2) reputation score 계산
        // - aspect 기반 긍정비율이 있으면 그걸 사용
        // - 없으면 rating(0~5)을 0~100으로 환산
        int score;
        if (denom > 0) {
            score = clamp(positivePct, 0, 100);
        } else if (snap.getRating() != null) {
            score = clamp((int) Math.round(snap.getRating() * 20.0), 0, 100);
        } else {
            score = 0;
        }

        // 3) rating distribution
        List<ReviewAnalysisResponse.RatingDistItem> ratingDist = new ArrayList<>();
        ratingDist.add(new ReviewAnalysisResponse.RatingDistItem(5, null, toIntOrNull(snap.getRating5Pct())));
        ratingDist.add(new ReviewAnalysisResponse.RatingDistItem(4, null, toIntOrNull(snap.getRating4Pct())));
        ratingDist.add(new ReviewAnalysisResponse.RatingDistItem(3, null, toIntOrNull(snap.getRating3Pct())));
        ratingDist.add(new ReviewAnalysisResponse.RatingDistItem(2, null, toIntOrNull(snap.getRating2Pct())));
        ratingDist.add(new ReviewAnalysisResponse.RatingDistItem(1, null, toIntOrNull(snap.getRating1Pct())));

        // 4) keyword insights + 각 키워드 score 계산
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