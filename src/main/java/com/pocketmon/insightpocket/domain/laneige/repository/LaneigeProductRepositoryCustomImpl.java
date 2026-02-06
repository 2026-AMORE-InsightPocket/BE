package com.pocketmon.insightpocket.domain.laneige.repository;

import com.pocketmon.insightpocket.domain.laneige.dto.LaneigeProductItem;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class LaneigeProductRepositoryImpl implements LaneigeProductRepositoryCustom {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public ProductsResult findLaneigeProducts() {

        String sql = """
        WITH latest AS (
            SELECT snapshot_id, snapshot_time
            FROM laneige_snapshot_runs
            ORDER BY snapshot_time DESC
            FETCH FIRST 1 ROW ONLY
        )
        SELECT
            l.snapshot_time,
            p.product_id,
            p.image_url,
            p.product_name,
            p.style,
            ps.price,
            ps.rank_1,
            ps.rank_1_category,
            ps.rank_2,
            ps.rank_2_category
        FROM laneige_products p
        CROSS JOIN latest l
        LEFT JOIN laneige_product_snapshots ps
               ON ps.snapshot_id = l.snapshot_id
              AND ps.product_id  = p.product_id
        ORDER BY p.product_id
        """;

        List<Row> rows = jdbc.query(sql, Map.of(), (rs, i) -> new Row(
                rs.getTimestamp("snapshot_time"),
                rs.getLong("product_id"),
                rs.getString("image_url"),
                rs.getString("product_name"),
                rs.getString("style"),
                (BigDecimal) rs.getObject("price"),
                toLong(rs.getObject("rank_1")),
                rs.getString("rank_1_category"),
                toLong(rs.getObject("rank_2")),
                rs.getString("rank_2_category")
        ));

        if (rows.isEmpty()) {
            return new ProductsResult(null, List.of());
        }

        LocalDateTime snapshotTime =
                rows.get(0).snapshotTime() == null
                        ? null
                        : rows.get(0).snapshotTime().toLocalDateTime();

        List<LaneigeProductItem> items = rows.stream()
                .map(r -> new LaneigeProductItem(
                        r.productId(),
                        r.imageUrl(),
                        r.productName(),
                        r.style(),
                        r.price(),
                        r.rank1(),
                        r.rank1Category(),
                        r.rank2(),
                        r.rank2Category()
                ))
                .toList();

        return new ProductsResult(snapshotTime, items);
    }

    private record Row(
            Timestamp snapshotTime,
            long productId,
            String imageUrl,
            String productName,
            String style,
            BigDecimal price,
            Long rank1,
            String rank1Category,
            Long rank2,
            String rank2Category
    ) {}

    private Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Long l) return l;
        if (v instanceof Integer i) return i.longValue();
        if (v instanceof BigDecimal bd) return bd.longValue();
        if (v instanceof Number n) return n.longValue();
        return Long.valueOf(v.toString());
    }
}