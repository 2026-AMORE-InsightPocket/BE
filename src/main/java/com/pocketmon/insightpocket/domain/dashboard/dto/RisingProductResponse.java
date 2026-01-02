package com.pocketmon.insightpocket.domain.dashboard.dto;

import java.util.List;

public record RisingProductResponse(
        String snapshot_time,
        List<RisingProductItem> items
) {}