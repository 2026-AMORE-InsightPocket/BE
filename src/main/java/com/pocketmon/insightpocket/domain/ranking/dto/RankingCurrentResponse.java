package com.pocketmon.insightpocket.domain.ranking.dto;

import java.util.List;

public record RankingCurrentResponse(
        long category_id,
        String snapshot_time,
        List<RankingCurrentItem> items
) {}