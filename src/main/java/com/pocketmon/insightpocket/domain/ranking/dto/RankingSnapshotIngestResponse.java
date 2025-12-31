package com.pocketmon.insightpocket.domain.ranking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RankingSnapshotIngestResponse {
    private Long snapshotId;
    private int inserted;
    private int updated;
}