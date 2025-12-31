package com.pocketmon.insightpocket.domain.ranking.entity;

import com.pocketmon.insightpocket.global.common.CreatedEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(
        name = "ranking_snapshots",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_ranking_snapshots_cat_time",
                columnNames = {"category_id", "snapshot_time"}
        )
)
@Getter
@NoArgsConstructor(access = PROTECTED)
public class RankingSnapshot extends CreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "snapshot_time", nullable = false)
    private LocalDateTime snapshotTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public static RankingSnapshot create(LocalDateTime snapshotTime, Category category) {
        RankingSnapshot s = new RankingSnapshot();
        s.snapshotTime = snapshotTime;
        s.category = category;
        return s;
    }
}