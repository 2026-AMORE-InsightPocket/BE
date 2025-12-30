package com.pocketmon.insightpocket.domain.laneige.entity;

import com.pocketmon.insightpocket.global.common.CreatedEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(
        name = "laneige_snapshot_runs",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_laneige_snapshot_runs_time",
                columnNames = {"snapshot_time"}
        )
)
@Getter
@NoArgsConstructor(access = PROTECTED)
public class LaneigeSnapshotRun extends CreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "snapshot_id")
    private Long snapshotId;

    @Column(name = "snapshot_time", nullable = false)
    private LocalDateTime snapshotTime;
}