package com.pocketmon.insightpocket.domain.laneige.repository;

import com.pocketmon.insightpocket.domain.laneige.entity.LaneigeProduct;
import com.pocketmon.insightpocket.domain.laneige.entity.LaneigeProductSnapshot;
import com.pocketmon.insightpocket.domain.laneige.entity.LaneigeSnapshotRun;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LaneigeProductSnapshotRepository extends JpaRepository<LaneigeProductSnapshot, Long> {
    Optional<LaneigeProductSnapshot> findBySnapshotRunAndProduct(LaneigeSnapshotRun run, LaneigeProduct product);
}