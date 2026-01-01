package com.pocketmon.insightpocket.domain.laneige.repository;

import com.pocketmon.insightpocket.domain.laneige.entity.LaneigeSnapshotRun;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface LaneigeSnapshotRunRepository extends JpaRepository<LaneigeSnapshotRun, Long> {
    Optional<LaneigeSnapshotRun> findBySnapshotTime(LocalDateTime snapshotTime);
}