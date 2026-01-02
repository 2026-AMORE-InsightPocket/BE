package com.pocketmon.insightpocket.domain.review.repository;

import com.pocketmon.insightpocket.domain.laneige.entity.LaneigeSnapshotRun;
import com.pocketmon.insightpocket.domain.review.repository.projection.LatestReviewSnapshotRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewSnapshotRepository extends JpaRepository<LaneigeSnapshotRun, Long> {

    @Query(value = """
        SELECT
            r.snapshot_id     AS snapshotId,
            r.snapshot_time   AS snapshotTime,

            p.rating          AS rating,
            p.review_count    AS reviewCount,

            p.rating_5_pct    AS rating5Pct,
            p.rating_4_pct    AS rating4Pct,
            p.rating_3_pct    AS rating3Pct,
            p.rating_2_pct    AS rating2Pct,
            p.rating_1_pct    AS rating1Pct,

            pr.customers_say_current    AS customersSayCurrent,
            pr.customers_say_updated_at AS customersSayUpdatedAt,

            p.customers_say             AS customersSayHighlight
        FROM laneige_snapshot_runs r
        JOIN laneige_product_snapshots p
          ON r.snapshot_id = p.snapshot_id
         AND p.product_id = :productId
        JOIN laneige_products pr
          ON pr.product_id = p.product_id
        ORDER BY r.snapshot_time DESC
        FETCH FIRST 1 ROWS ONLY
    """, nativeQuery = true)
    Optional<LatestReviewSnapshotRow> findLatestSnapshot(@Param("productId") Long productId);
}