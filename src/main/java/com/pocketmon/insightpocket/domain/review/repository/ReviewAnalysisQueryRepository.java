package com.pocketmon.insightpocket.domain.review.repository;

import com.pocketmon.insightpocket.domain.review.dto.KeywordInsightRow;
import com.pocketmon.insightpocket.domain.review.dto.LatestReviewSnapshotRow;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewAnalysisQueryRepository {

    private final JdbcTemplate jdbcTemplate;

    public LatestReviewSnapshotRow findLatestSnapshot(Long productId) {
        String sql = """
        
                SELECT *
        FROM (
            SELECT
                p.product_snapshot_id        AS productSnapshotId,
                p.snapshot_id                AS snapshotId,
                r.snapshot_time              AS snapshotTime,
                p.review_count               AS reviewCount,
                p.rating                     AS rating,
                p.customers_say              AS customersSayHighlight,
                p.rating_5_pct               AS rating5Pct,
                p.rating_4_pct               AS rating4Pct,
                p.rating_3_pct               AS rating3Pct,
                p.rating_2_pct               AS rating2Pct,
                p.rating_1_pct               AS rating1Pct,
                pr.customers_say_current     AS customersSayCurrent,
                pr.customers_say_updated_at  AS customersSayUpdatedAt
            FROM laneige_product_snapshots p
            JOIN laneige_snapshot_runs r ON r.snapshot_id = p.snapshot_id
            JOIN laneige_products pr ON pr.product_id = p.product_id
            WHERE p.product_id = ?
            ORDER BY r.snapshot_time DESC
        )
        WHERE customersSayHighlight IS NOT NULL
        FETCH FIRST 1 ROWS ONLY
        """;

        List<LatestReviewSnapshotRow> list =
                jdbcTemplate.query(sql, new Object[]{productId},
                        (rs, i) -> new LatestReviewSnapshotRow(
                                rs.getLong("productSnapshotId"),
                                rs.getLong("snapshotId"),
                                rs.getTimestamp("snapshotTime").toLocalDateTime(),
                                rs.getLong("reviewCount"),
                                rs.getObject("rating") == null ? null : rs.getDouble("rating"),
                                rs.getString("customersSayHighlight"),
                                rs.getObject("rating5Pct") == null ? null : rs.getLong("rating5Pct"),
                                rs.getObject("rating4Pct") == null ? null : rs.getLong("rating4Pct"),
                                rs.getObject("rating3Pct") == null ? null : rs.getLong("rating3Pct"),
                                rs.getObject("rating2Pct") == null ? null : rs.getLong("rating2Pct"),
                                rs.getObject("rating1Pct") == null ? null : rs.getLong("rating1Pct"),
                                rs.getString("customersSayCurrent"),
                                rs.getTimestamp("customersSayUpdatedAt") == null
                                        ? null
                                        : rs.getTimestamp("customersSayUpdatedAt").toLocalDateTime()
                        ));

        return list.isEmpty() ? null : list.get(0);
    }

    public List<KeywordInsightRow> findKeywordInsights(Long snapshotId) {
        String sql = """
            SELECT
                a.aspect_name        AS aspect_name,
                a.mention_total      AS mention_total,
                a.mention_positive   AS mention_positive,
                a.mention_negative   AS mention_negative,
                a.summary            AS summary
            FROM laneige_aspect_details a
            WHERE a.product_snapshot_id = ?
            ORDER BY a.mention_total DESC
        """;

        return jdbcTemplate.query(sql, new Object[]{snapshotId},
                (rs, i) -> new KeywordInsightRow(
                        rs.getString("aspect_name"),
                        rs.getLong("mention_total"),
                        rs.getLong("mention_positive"),
                        rs.getLong("mention_negative"),
                        rs.getString("summary")
                ));
    }
}