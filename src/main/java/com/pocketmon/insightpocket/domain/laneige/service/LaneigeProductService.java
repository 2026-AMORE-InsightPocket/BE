package com.pocketmon.insightpocket.domain.laneige.service;

import com.pocketmon.insightpocket.domain.laneige.dto.LaneigeProductsResponse;
import com.pocketmon.insightpocket.domain.laneige.repository.LaneigeProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class LaneigeProductService {

    private final LaneigeProductRepository laneigeProductRepository;

    private static final DateTimeFormatter OUT_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LaneigeProductsResponse getLaneigeProducts() {

        var result = laneigeProductRepository.findLaneigeProducts();

        String snapshotTime =
                result.snapshotTime() == null
                        ? null
                        : result.snapshotTime().format(OUT_FMT);

        return new LaneigeProductsResponse(
                snapshotTime,
                result.items()
        );
    }
}