package com.pocketmon.insightpocket.domain.laneige.repository;

import com.pocketmon.insightpocket.domain.laneige.entity.LaneigeProductSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LaneigeRankTrendRepository extends JpaRepository<LaneigeProductSnapshot, Long> {

    @Query(value = """
        SELECT
            r.snapshot_time      AS snapshotTime,
            p.rank_1             AS rank1,
            p.rank_1_category    AS rank1Category,
            p.rank_2             AS rank2,
            p.rank_2_category    AS rank2Category
        FROM laneige_snapshot_runs r
        LEFT JOIN laneige_product_snapshots p
          ON r.snapshot_id = p.snapshot_id
         AND p.product_id = :productId
        WHERE r.snapshot_time >= :startTime
            AND r.snapshot_time <  :endTime
        ORDER BY r.snapshot_time
    """, nativeQuery = true)
    List<LaneigeRankTrendRow> findRankTrends(
            @Param("productId") Long productId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}