package com.pocketmon.insightpocket.domain.review.repository.projection;

import java.time.LocalDateTime;

public interface ReviewSnapshotRow {
    Long getSnapshotId();
    LocalDateTime getSnapshotTime();
}