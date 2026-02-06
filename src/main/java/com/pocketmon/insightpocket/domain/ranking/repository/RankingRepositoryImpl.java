package com.pocketmon.insightpocket.domain.ranking.repository;

import com.pocketmon.insightpocket.domain.ranking.dto.RankingCurrentItem;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class RankingRepositoryImpl implements RankingRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public CurrentResult findCurrentRanking(long categoryId) {

        String sql = """
        WITH
        cur_snapshot AS (
          SELECT id AS snapshot_id, snapshot_time
          FROM ranking_snapshots
          WHERE category_id = :category_id
          ORDER BY snapshot_time DESC
          FETCH FIRST 1 ROW ONLY
        ),
        prev_snapshot AS (
          SELECT id AS snapshot_id
          FROM ranking_snapshots
          WHERE category_id = :category_id
            AND snapshot_time < (SELECT snapshot_time FROM cur_snapshot)
          ORDER BY snapshot_time DESC
          FETCH FIRST 1 ROW ONLY
        ),
        cur_items AS (
          SELECT
            c.snapshot_time,
            i.rank,
            i.product_name,
            CASE WHEN i.is_laneige = 'Y' THEN 1 ELSE 0 END AS is_laneige
          FROM ranking_items i
          JOIN cur_snapshot c ON c.snapshot_id = i.snapshot_id
        ),
        prev_items AS (
          SELECT
            i.product_name,
            i.rank AS prev_rank
          FROM ranking_items i
          JOIN prev_snapshot p ON p.snapshot_id = i.snapshot_id
        )
        SELECT
          c.snapshot_time,
          c.rank,
          c.product_name,
          c.is_laneige,
          p.prev_rank,
          CASE
            WHEN p.prev_rank IS NULL THEN NULL
            ELSE (p.prev_rank - c.rank)
          END AS rank_change
        FROM cur_items c
        LEFT JOIN prev_items p
          ON p.product_name = c.product_name
        ORDER BY c.rank
        """;

        var params = Map.of("category_id", categoryId);

        List<Row> rows = jdbc.query(sql, params, (rs, i) -> new Row(
                rs.getTimestamp("snapshot_time"),
                rs.getInt("rank"),
                rs.getString("product_name"),
                rs.getBoolean("is_laneige"),
                toInteger(rs.getObject("prev_rank")),
                toInteger(rs.getObject("rank_change"))
        ));

        if (rows.isEmpty()) {
            return new CurrentResult(null, List.of());
        }

        LocalDateTime snapshotTime =
                rows.get(0).snapshotTime() == null
                        ? null
                        : rows.get(0).snapshotTime().toLocalDateTime();

        List<RankingCurrentItem> items = rows.stream()
                .map(r -> new RankingCurrentItem(
                        r.rank(),
                        r.productName(),
                        r.isLaneige(),
                        r.prevRank(),
                        r.rankChange()
                ))
                .toList();

        return new CurrentResult(snapshotTime, items);
    }

    private record Row(
            Timestamp snapshotTime,
            int rank,
            String productName,
            boolean isLaneige,
            Integer prevRank,
            Integer rankChange
    ) {}

    private Integer toInteger(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.intValue();
        return Integer.valueOf(v.toString());
    }
}