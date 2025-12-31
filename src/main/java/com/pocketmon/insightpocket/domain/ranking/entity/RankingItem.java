package com.pocketmon.insightpocket.domain.ranking.entity;

import com.pocketmon.insightpocket.global.common.CreatedEntity;
import com.pocketmon.insightpocket.global.common.YesNoBooleanConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(
        name = "ranking_items",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_ranking_items_snapshot_rank",
                columnNames = {"snapshot_id", "rank"}
        )
)
@Getter
@NoArgsConstructor(access = PROTECTED)
public class RankingItem extends CreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "snapshot_id", nullable = false)
    private RankingSnapshot snapshot;

    @Column(name = "rank", nullable = false)
    private Integer rank;

    @Column(name = "product_name", length = 1000, nullable = false)
    private String productName;

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Convert(converter = YesNoBooleanConverter.class)
    @Column(name = "is_laneige", nullable = false, columnDefinition = "CHAR(1)")
    private Boolean isLaneige;

    public static RankingItem create(RankingSnapshot snapshot, Integer rank, String productName,
                                     BigDecimal price, Boolean isLaneige) {
        RankingItem i = new RankingItem();
        i.snapshot = snapshot;
        i.rank = rank;
        i.productName = productName;
        i.price = price;
        i.isLaneige = isLaneige; // @Convert(YesNoBooleanConverter) 쓰면 알아서 Y/N 저장됨
        return i;
    }

    public void update(String productName, BigDecimal price, Boolean isLaneige) {
        this.productName = productName;
        this.price = price;
        this.isLaneige = isLaneige;
    }
}