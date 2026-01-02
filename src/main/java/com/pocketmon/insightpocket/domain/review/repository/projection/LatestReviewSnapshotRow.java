package com.pocketmon.insightpocket.domain.review.repository.projection;

import java.time.LocalDateTime;

public interface LatestReviewSnapshotRow {

    Long getSnapshotId();
    LocalDateTime getSnapshotTime();

    Long getReviewCount();
    Double getRating();

    Long getRating5Pct();
    Long getRating4Pct();
    Long getRating3Pct();
    Long getRating2Pct();
    Long getRating1Pct();

    // laneige_products
    String getCustomersSayCurrent();
    LocalDateTime getCustomersSayUpdatedAt();

    // laneige_product_snapshots (optional)
    String getCustomersSayHighlight();
}