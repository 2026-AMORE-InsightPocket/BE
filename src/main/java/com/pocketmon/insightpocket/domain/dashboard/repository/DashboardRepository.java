package com.pocketmon.insightpocket.domain.dashboard.repository;

import com.pocketmon.insightpocket.domain.dashboard.dto.BestsellerTop5Item;

import java.time.LocalDateTime;
import java.util.List;

public interface DashboardRepository {

    Top5Result findLaneigeBestsellerTop5(LocalDateTime monthStart, LocalDateTime monthEnd);

    record Top5Result(
            LocalDateTime snapshotTime,
            List<BestsellerTop5Item> items
    ) {}
}