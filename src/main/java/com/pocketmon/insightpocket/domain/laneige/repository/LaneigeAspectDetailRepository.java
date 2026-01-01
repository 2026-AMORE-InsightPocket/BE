package com.pocketmon.insightpocket.domain.laneige.repository;

import com.pocketmon.insightpocket.domain.laneige.entity.LaneigeAspectDetail;
import com.pocketmon.insightpocket.domain.laneige.entity.LaneigeProductSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LaneigeAspectDetailRepository extends JpaRepository<LaneigeAspectDetail, Long> {
    Optional<LaneigeAspectDetail> findByProductSnapshotAndAspectName(LaneigeProductSnapshot snapshot, String aspectName);
}