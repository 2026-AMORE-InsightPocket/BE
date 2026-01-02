package com.pocketmon.insightpocket.domain.laneige.service;

import com.pocketmon.insightpocket.domain.laneige.dto.LaneigeProductsResponse;
import com.pocketmon.insightpocket.domain.laneige.repository.LaneigeProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LaneigeProductService {

    private final LaneigeProductRepository laneigeProductRepository;

    public LaneigeProductsResponse getLaneigeProducts() {

        var result = laneigeProductRepository.findLaneigeProducts();

        String snapshotTime =
                result.snapshotTime() == null
                        ? null
                        : result.snapshotTime().toString().replace('T', ' ');

        return new LaneigeProductsResponse(
                snapshotTime,
                result.items()
        );
    }
}