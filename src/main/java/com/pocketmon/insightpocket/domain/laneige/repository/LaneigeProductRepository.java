package com.pocketmon.insightpocket.domain.laneige.repository;

import com.pocketmon.insightpocket.domain.laneige.entity.LaneigeProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface LaneigeProductRepository extends JpaRepository<LaneigeProduct, Long> {
    Optional<LaneigeProduct> findByProductUrl(String productUrl);
    List<LaneigeProduct> findAllByProductUrlIn(Collection<String> productUrls);
}