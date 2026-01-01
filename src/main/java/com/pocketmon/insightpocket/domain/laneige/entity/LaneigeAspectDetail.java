package com.pocketmon.insightpocket.domain.laneige.entity;

import com.pocketmon.insightpocket.global.common.CreatedEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(
        name = "laneige_aspect_details",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_aspect_snapshot_name",
                columnNames = {"product_snapshot_id", "aspect_name"}
        )
)
@Getter
@NoArgsConstructor(access = PROTECTED)
public class LaneigeAspectDetail extends CreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aspect_detail_id")
    private Long aspectDetailId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_snapshot_id", nullable = false)
    private LaneigeProductSnapshot productSnapshot;

    @Column(name = "aspect_name", length = 200)
    private String aspectName;

    @Column(name = "mention_total")
    private Long mentionTotal =0L;

    @Column(name = "mention_positive")
    private Long mentionPositive =0L;

    @Column(name = "mention_negative")
    private Long mentionNegative =0L;

    @Lob
    @Column(name = "summary")
    private String summary;
}