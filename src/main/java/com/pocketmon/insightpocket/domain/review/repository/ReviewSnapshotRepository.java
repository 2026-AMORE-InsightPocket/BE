package com.pocketmon.insightpocket.domain.review.repository;

import com.pocketmon.insightpocket.domain.review.repository.projection.LatestReviewSnapshotRow;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewSnapshotRepository extends Repository<Object, Long> {

    @Query(value = """
        SELECT
            s.snapshot_id               AS snapshotId,
            r.snapshot_time             AS snapshotTime,

            s.review_count              AS reviewCount,
            s.rating                    AS rating,

            s.rating_5_pct              AS rating5Pct,
            s.rating_4_pct              AS rating4Pct,
            s.rating_3_pct              AS rating3Pct,
            s.rating_2_pct              AS rating2Pct,
            s.rating_1_pct              AS rating1Pct,

            p.customers_say_current     AS customersSayCurrent,
            p.customers_say_updated_at  AS customersSayUpdatedAt,

            s.customers_say             AS customersSayHighlight
        FROM laneige_snapshot_runs r
        JOIN laneige_product_snapshots s
          ON r.snapshot_id = s.snapshot_id
        JOIN laneige_products p
          ON p.product_id = s.product_id
        WHERE s.product_id = :productId
        ORDER BY r.snapshot_time DESC
        FETCH FIRST 1 ROWS ONLY
    """, nativeQuery = true)
    Optional<LatestReviewSnapshotRow> findLatestSnapshot(@Param("productId") Long productId);
}