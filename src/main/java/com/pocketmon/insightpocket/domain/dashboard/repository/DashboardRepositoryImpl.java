package com.pocketmon.insightpocket.domain.dashboard.repository;

import com.pocketmon.insightpocket.domain.dashboard.dto.BestsellerTop5Item;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

@Repository
@RequiredArgsConstructor
public class DashboardRepositoryImpl implements DashboardRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Top5Result findLaneigeBestsellerTop5(LocalDateTime monthStart, LocalDateTime monthEnd) {

        // prevMonth 범위는 monthStart 기준으로 계산 (서비스에서 해도 되고 여기서 해도 됨)
        LocalDateTime prevMonthStart = monthStart.minusMonths(1);
        LocalDateTime prevMonthEnd = monthStart;

        String sql = """
WITH
-- 1) 이번달 마지막 스냅샷 1개
cur_last AS (
  SELECT snapshot_id, snapshot_time
  FROM (
    SELECT r.snapshot_id, r.snapshot_time
    FROM laneige_snapshot_runs r
    WHERE r.snapshot_time >= :cur_start
      AND r.snapshot_time <  :cur_end
    ORDER BY r.snapshot_time DESC
  )
  WHERE ROWNUM = 1
),
-- 2) 전월 마지막 스냅샷 1개
prev_last AS (
  SELECT snapshot_id, snapshot_time
  FROM (
    SELECT r.snapshot_id, r.snapshot_time
    FROM laneige_snapshot_runs r
    WHERE r.snapshot_time >= :prev_start
      AND r.snapshot_time <  :prev_end
    ORDER BY r.snapshot_time DESC
  )
  WHERE ROWNUM = 1
),
-- 3) 이번달 마지막 스냅샷에서 판매량 기준 랭킹
cur_ranked AS (
  SELECT
    cl.snapshot_time AS snapshot_time,
    DENSE_RANK() OVER (ORDER BY NVL(ps.last_month_sales, 0) DESC, ps.product_id ASC) AS cur_rank,
    ps.product_id,
    p.product_name,
    NVL(ps.last_month_sales, 0) AS last_month_sales
  FROM cur_last cl
  JOIN laneige_product_snapshots ps
       ON ps.snapshot_id = cl.snapshot_id
  JOIN laneige_products p
       ON p.product_id = ps.product_id
),
-- 4) 전월 마지막 스냅샷에서 판매량 기준 랭킹
prev_ranked AS (
  SELECT
    DENSE_RANK() OVER (ORDER BY NVL(ps.last_month_sales, 0) DESC, ps.product_id ASC) AS prev_rank,
    ps.product_id
  FROM laneige_product_snapshots ps
  JOIN prev_last pl ON pl.snapshot_id = ps.snapshot_id
)
SELECT
  c.snapshot_time,
  c.cur_rank AS rank,
  c.product_id,
  c.product_name,
  c.last_month_sales,
  pr.prev_rank AS prev_month_rank,
  CASE
    WHEN pr.prev_rank IS NULL THEN NULL
    ELSE (pr.prev_rank - c.cur_rank)
  END AS rank_change
FROM cur_ranked c
LEFT JOIN prev_ranked pr ON pr.product_id = c.product_id
WHERE c.cur_rank <= 5
ORDER BY c.cur_rank
""";

        Map<String, Object> params = Map.of(
                "cur_start", monthStart,
                "cur_end", monthEnd,
                "prev_start", prevMonthStart,
                "prev_end", prevMonthEnd
        );

        List<Row> fetched = jdbc.query(sql, params, (rs, i) -> new Row(
                rs.getTimestamp("snapshot_time"),
                rs.getInt("rank"),
                rs.getLong("product_id"),
                rs.getString("product_name"),
                rs.getLong("last_month_sales"),
                toInteger(rs.getObject("prev_month_rank")),
                toInteger(rs.getObject("rank_change"))
        ));

        if (fetched.isEmpty()) {
            return new Top5Result(null, List.of());
        }

        Timestamp ts = fetched.get(0).snapshotTime();
        LocalDateTime snapshotTime =
                (ts == null) ? null : ts.toLocalDateTime();

        List<BestsellerTop5Item> items = fetched.stream()
                .map(r -> new BestsellerTop5Item(
                        r.rank(),
                        r.productId(),
                        r.productName(),
                        r.lastMonthSales(),
                        r.prevMonthRank(),
                        r.rankChange()
                ))
                .toList();

        return new Top5Result(snapshotTime, items);
    }

    // 내부 매핑용 record
    private record Row(
            Timestamp snapshotTime,
            int rank,
            long productId,
            String productName,
            long lastMonthSales,
            Integer prevMonthRank,
            Integer rankChange
    ) {}

    private Integer toInteger(Object v) {
        if (v == null) return null;
        if (v instanceof Integer i) return i;
        if (v instanceof Long l) return Math.toIntExact(l);
        if (v instanceof BigDecimal bd) return bd.intValue(); // Oracle NUMBER 대응
        if (v instanceof Number n) return n.intValue();
        return Integer.valueOf(v.toString());
    }
}