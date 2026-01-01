package com.pocketmon.insightpocket.domain.dashboard.dto;

import java.util.List;

public record BestsellerTop5Response(
        String month,
        String snapshot_time,
        List<BestsellerTop5Item> items
) {}