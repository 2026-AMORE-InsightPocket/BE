package com.pocketmon.insightpocket.domain.ranking.repository;

import com.pocketmon.insightpocket.domain.ranking.entity.RankingItem;
import com.pocketmon.insightpocket.domain.ranking.entity.RankingSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RankingItemRepository extends JpaRepository<RankingItem, Long> {
    Optional<RankingItem> findBySnapshotAndRank(RankingSnapshot snapshot, Integer rank);
}