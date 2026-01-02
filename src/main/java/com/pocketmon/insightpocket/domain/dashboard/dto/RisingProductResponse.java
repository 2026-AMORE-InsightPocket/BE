package com.pocketmon.insightpocket.domain.dashboard.dto;

public record RisingProductResponse(
        String snapshot_time,
        RisingProductItem item
) {}