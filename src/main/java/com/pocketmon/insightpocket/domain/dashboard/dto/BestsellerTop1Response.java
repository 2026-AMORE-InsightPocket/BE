package com.pocketmon.insightpocket.domain.dashboard.dto;

public record BestsellerTop1Response(
        String month,
        String snapshot_time,
        BestsellerTop5Item item
) {}