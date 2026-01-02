package com.pocketmon.insightpocket.domain.laneige.dto;

import com.pocketmon.insightpocket.domain.laneige.dto.LaneigeProductItem;

import java.util.List;

public record LaneigeProductsResponse(
        String snapshot_time,
        List<LaneigeProductItem> items
) {}