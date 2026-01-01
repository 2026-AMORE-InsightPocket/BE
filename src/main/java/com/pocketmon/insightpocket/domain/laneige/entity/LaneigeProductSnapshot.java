package com.pocketmon.insightpocket.domain.laneige.entity;

import com.pocketmon.insightpocket.global.common.CreatedEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(
        name = "laneige_product_snapshots",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_lps_run_product",
                columnNames = {"snapshot_id", "product_id"}
        )
)
@Getter
@NoArgsConstructor(access = PROTECTED)
public class LaneigeProductSnapshot extends CreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_snapshot_id")
    private Long productSnapshotId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "snapshot_id", nullable = false)
    private LaneigeSnapshotRun snapshotRun;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private LaneigeProduct product;

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "review_count", nullable = false)
    private Long reviewCount = 0L;

    @Column(name = "rating", precision = 3, scale = 1)
    private BigDecimal rating;

    @Column(name = "rating_5_pct")
    private Integer rating5Pct;
    @Column(name = "rating_4_pct")
    private Integer rating4Pct;
    @Column(name = "rating_3_pct")
    private Integer rating3Pct;
    @Column(name = "rating_2_pct")
    private Integer rating2Pct;
    @Column(name = "rating_1_pct")
    private Integer rating1Pct;

    @Column(name = "last_month_sales")
    private Long lastMonthSales;

    @Column(name = "rank_1")
    private Long rank1;

    @Column(name = "rank_1_category", length = 200)
    private String rank1Category;

    @Column(name = "rank_2")
    private Long rank2;

    @Column(name = "rank_2_category", length = 200)
    private String rank2Category;

    @Lob
    @Column(name = "customers_say")
    private String customersSay; // 변경된 날만 저장 (NULL 가능)
}