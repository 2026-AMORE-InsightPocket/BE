package com.pocketmon.insightpocket.domain.review.repository;

import com.pocketmon.insightpocket.domain.review.repository.projection.KeywordInsightRow;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewAnalysisRepository extends Repository<Object, Long> {

    @Query(value = """
        SELECT
            a.aspect_name      AS aspectName,
            a.mention_total    AS mentionTotal,
            a.mention_positive AS positive,
            a.mention_negative AS negative,
            a.summary          AS summary
        FROM laneige_aspect_details a
        WHERE a.product_snapshot_id = :productSnapshotId
        ORDER BY a.mention_total DESC NULLS LAST
    """, nativeQuery = true)
    List<KeywordInsightRow> findKeywordInsights(@Param("productSnapshotId") Long productSnapshotId);
}