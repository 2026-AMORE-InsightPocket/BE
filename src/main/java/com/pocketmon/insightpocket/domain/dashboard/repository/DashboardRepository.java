package com.pocketmon.insightpocket.domain.dashboard.repository;

import com.pocketmon.insightpocket.domain.dashboard.dto.BestsellerTop5Item;
import com.pocketmon.insightpocket.domain.dashboard.dto.RisingProductItem;

import java.time.LocalDateTime;
import java.util.List;

public interface DashboardRepository {

    RisingResult findRisingProducts();

    Top5Result findLaneigeBestsellerTop5(
            LocalDateTime monthStart,
            LocalDateTime monthEnd
    );

    record RisingResult(
            LocalDateTime snapshotTime,
            List<RisingProductItem> items
    ) {}

    record Top5Result(
            LocalDateTime snapshotTime,
            List<BestsellerTop5Item> items
    ) {}
}