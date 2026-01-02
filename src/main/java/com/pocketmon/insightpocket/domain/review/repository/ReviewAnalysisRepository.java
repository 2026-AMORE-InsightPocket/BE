package com.pocketmon.insightpocket.domain.review.repository;

import com.pocketmon.insightpocket.domain.laneige.entity.LaneigeSnapshotRun;
import com.pocketmon.insightpocket.domain.review.repository.projection.KeywordInsightRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewAnalysisRepository extends JpaRepository<LaneigeSnapshotRun, Long> {

    @Query(value = """
        SELECT
            d.aspect_name   AS aspectName,
            d.mention_total AS mentionTotal,
            d.positive      AS positive,
            d.negative      AS negative,
            d.summary       AS summary
        FROM laneige_aspect_details d
        WHERE d.snapshot_id = :snapshotId
        ORDER BY d.mention_total DESC
    """, nativeQuery = true)
    List<KeywordInsightRow> findKeywordInsights(@Param("snapshotId") Long snapshotId);
}