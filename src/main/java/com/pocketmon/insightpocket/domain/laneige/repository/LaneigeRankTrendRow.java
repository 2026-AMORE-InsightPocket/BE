package com.pocketmon.insightpocket.domain.laneige.repository;

import java.time.LocalDateTime;

public interface LaneigeRankTrendRow {
    LocalDateTime getSnapshotTime();

    Long getRank1();
    String getRank1Category();

    Long getRank2();
    String getRank2Category();
}