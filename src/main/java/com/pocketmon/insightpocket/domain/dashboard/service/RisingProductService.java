package com.pocketmon.insightpocket.domain.dashboard.service;

import com.pocketmon.insightpocket.domain.dashboard.dto.RisingProductResponse;
import com.pocketmon.insightpocket.domain.dashboard.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RisingProductService {

    private final DashboardRepository dashboardRepository;

    public RisingProductResponse getRisingProducts() {

        var result = dashboardRepository.findRisingProducts();

        String snapshotTimeStr =
                result.snapshotTime() == null
                        ? null
                        : result.snapshotTime().toString().replace('T', ' ');

        return new RisingProductResponse(
                snapshotTimeStr,
                result.items()
        );
    }
}