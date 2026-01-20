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

    @Column(name = "price", precision = 10, scale = 2)
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
    private String customersSay; // 변경된 날만 저장

    public static LaneigeProductSnapshot create(
            LaneigeSnapshotRun run,
            LaneigeProduct product,
            BigDecimal price
    ) {
        LaneigeProductSnapshot s = new LaneigeProductSnapshot();
        s.snapshotRun = run;
        s.product = product;
        s.price = price;
        return s;
    }

    public void updateMetrics(
            Long reviewCount,
            BigDecimal rating,
            Integer rating5Pct,
            Integer rating4Pct,
            Integer rating3Pct,
            Integer rating2Pct,
            Integer rating1Pct,
            Long lastMonthSales
    ) {
        if (reviewCount != null) this.reviewCount = reviewCount;
        this.rating = rating;
        this.rating5Pct = rating5Pct;
        this.rating4Pct = rating4Pct;
        this.rating3Pct = rating3Pct;
        this.rating2Pct = rating2Pct;
        this.rating1Pct = rating1Pct;
        this.lastMonthSales = lastMonthSales;
    }

    public void updateRanks(
            Long rank1,
            String rank1Category,
            Long rank2,
            String rank2Category
    ) {
        this.rank1 = rank1;
        this.rank1Category = rank1Category;
        this.rank2 = rank2;
        this.rank2Category = rank2Category;
    }

    public void updateCustomersSay(String customersSay) {
        this.customersSay = customersSay;
    }

    public void updatePrice(BigDecimal price) {
        this.price = price;
    }
}