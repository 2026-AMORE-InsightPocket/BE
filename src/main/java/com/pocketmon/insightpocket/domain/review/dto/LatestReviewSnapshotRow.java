package com.pocketmon.insightpocket.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class LatestReviewSnapshotRow {
    private Long snapshotId;
    private LocalDateTime snapshotTime;
    private Long reviewCount;
    private Double rating;
    private String customersSayHighlight;
    private Long rating5Pct;
    private Long rating4Pct;
    private Long rating3Pct;
    private Long rating2Pct;
    private Long rating1Pct;
    private String customersSayCurrent;
    private LocalDateTime customersSayUpdatedAt;
}