package com.pocketmon.insightpocket.domain.ranking.repository;

import com.pocketmon.insightpocket.domain.ranking.entity.Category;
import com.pocketmon.insightpocket.domain.ranking.entity.RankingSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RankingSnapshotRepository extends JpaRepository<RankingSnapshot, Long> {
    Optional<RankingSnapshot> findByCategoryAndSnapshotTime(Category category, LocalDateTime snapshotTime);
}