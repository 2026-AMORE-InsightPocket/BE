package com.pocketmon.insightpocket.domain.dashboard.repository;

import com.pocketmon.insightpocket.domain.dashboard.dto.BestsellerTop5Item;
import com.pocketmon.insightpocket.domain.dashboard.dto.RisingProductItem;
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
    public RisingResult findRisingProducts() {

        String sql = """
SELECT
    r.snapshot_time,
    p.image_url,
    p.product_name,
    ps.rating,
    ps.review_count,
    r.diff AS rank_change,
    ROUND(r.diff * 100 / r.yesterday_rank) || '% 성장' AS growth_rate
FROM (
    SELECT
        t.product_id,
        y.yesterday_rank,
        t.today_rank,
        (y.yesterday_rank - t.today_rank) AS diff,
        l.snapshot_time
    FROM (
        SELECT
            DENSE_RANK() OVER (ORDER BY NVL(last_month_sales, 0) DESC) AS today_rank,
            product_id
        FROM laneige_product_snapshots
        WHERE snapshot_id = 2   -- 🔥 하드코딩
    ) t
    JOIN (
        SELECT
            DENSE_RANK() OVER (ORDER BY NVL(last_month_sales, 0) DESC) AS yesterday_rank,
            product_id
        FROM laneige_product_snapshots
        WHERE snapshot_id = 1   -- 🔥 하드코딩
    ) y
      ON t.product_id = y.product_id
    JOIN laneige_snapshot_runs l
      ON l.snapshot_id = 2
) r
JOIN laneige_products p
  ON p.product_id = r.product_id
JOIN laneige_product_snapshots ps
  ON ps.product_id = r.product_id
 AND ps.snapshot_id = 2
WHERE r.diff >= 2
ORDER BY r.diff DESC
FETCH FIRST 1 ROWS ONLY
""";

        List<RisingRow> rows = jdbc.query(sql, Map.of(), (rs, i) -> {

            BigDecimal reviewBd = (BigDecimal) rs.getObject("review_count");
            Long reviewCount = (reviewBd == null) ? null : reviewBd.longValue();

            Integer rankChange = toInteger(rs.getObject("rank_change"));

            return new RisingRow(
                    rs.getTimestamp("snapshot_time"),
                    rs.getString("image_url"),
                    rs.getString("product_name"),
                    (BigDecimal) rs.getObject("rating"),
                    reviewCount,
                    rankChange,
                    rs.getString("growth_rate")
            );
        });

        if (rows.isEmpty()) {
            return new RisingResult(null, List.of());
        }

        LocalDateTime snapshotTime =
                (rows.get(0).snapshotTime() == null)
                        ? null
                        : rows.get(0).snapshotTime().toLocalDateTime();

        List<RisingProductItem> items = rows.stream()
                .map(r -> new RisingProductItem(
                        r.imageUrl(),
                        r.productName(),
                        r.rating(),
                        r.reviewCount(),
                        r.rankChange(),
                        r.growthRate()
                ))
                .toList();

        return new RisingResult(snapshotTime, items);
    }

    /* 내부 Row */
    private record RisingRow(
            Timestamp snapshotTime,
            String imageUrl,
            String productName,
            BigDecimal rating,
            Long reviewCount,
            Integer rankChange,
            String growthRate
    ) {}

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
    DENSE_RANK() OVER (
      ORDER BY NVL(ps.last_month_sales, 0) DESC, ps.product_id ASC
    ) AS cur_rank,
    ps.product_id,
    p.product_name,
    p.image_url,
    NVL(ps.last_month_sales, 0) AS last_month_sales,
    ps.rating,
    ps.review_count
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
  c.image_url,
  c.product_name,
  c.last_month_sales,
  c.rating,
  c.review_count,
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

        List<Row> fetched = jdbc.query(sql, params, (rs, i) -> {

            BigDecimal rating = (BigDecimal) rs.getObject("rating");

            BigDecimal reviewCntBd = (BigDecimal) rs.getObject("review_count");
            Long reviewCount = (reviewCntBd == null) ? null : reviewCntBd.longValue();

            return new Row(
                    rs.getTimestamp("snapshot_time"),
                    rs.getInt("rank"),
                    rs.getLong("product_id"),
                    rs.getString("image_url"),
                    rs.getString("product_name"),
                    rs.getLong("last_month_sales"),
                    rating,
                    reviewCount,
                    toInteger(rs.getObject("prev_month_rank")),
                    toInteger(rs.getObject("rank_change"))
            );
        });

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
                        r.imageUrl(),
                        r.productName(),
                        r.lastMonthSales(),
                        r.rating(),
                        r.reviewCount(),
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
            String imageUrl,
            String productName,
            long lastMonthSales,
            BigDecimal rating,
            Long reviewCount,
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