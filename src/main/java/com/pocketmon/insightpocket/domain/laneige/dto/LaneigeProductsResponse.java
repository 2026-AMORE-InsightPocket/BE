package com.pocketmon.insightpocket.domain.laneige.dto;

import java.util.List;

public record LaneigeProductListResponse(
        String snapshot_time,
        List<LaneigeProductListItem> items
) {}