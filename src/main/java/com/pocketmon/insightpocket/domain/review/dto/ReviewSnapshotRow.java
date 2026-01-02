package com.pocketmon.insightpocket.domain.review.dto;

import java.time.LocalDateTime;

public interface ReviewSnapshotRow {
    Long getSnapshotId();
    LocalDateTime getSnapshotTime();
}