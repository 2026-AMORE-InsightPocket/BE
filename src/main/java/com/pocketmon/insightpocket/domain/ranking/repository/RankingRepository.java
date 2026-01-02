package com.pocketmon.insightpocket.domain.ranking.repository;

import com.pocketmon.insightpocket.domain.ranking.dto.RankingCurrentItem;

import java.time.LocalDateTime;
import java.util.List;

public interface RankingRepository {

    CurrentResult findCurrentRanking(long categoryId);

    record CurrentResult(
            LocalDateTime snapshotTime,
            List<RankingCurrentItem> items
    ) {}
}