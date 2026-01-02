package com.pocketmon.insightpocket.domain.laneige.repository;

import com.pocketmon.insightpocket.domain.laneige.dto.LaneigeProductItem;

import java.time.LocalDateTime;
import java.util.List;

public interface LaneigeProductRepositoryCustom {

    ProductsResult findLaneigeProducts();

    record ProductsResult(
            LocalDateTime snapshotTime,
            List<LaneigeProductItem> items
    ) {}
}