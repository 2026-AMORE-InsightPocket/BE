package com.pocketmon.insightpocket.domain.dashboard.service;

import com.pocketmon.insightpocket.domain.dashboard.dto.BestsellerTop5Response;
import com.pocketmon.insightpocket.domain.dashboard.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class BestsellerTop5Service {

    private final DashboardRepository dashboardRepository;

    public BestsellerTop5Response getLaneigeTop5(String month) {
        YearMonth ym = YearMonth.parse(month); // "2025-12"

        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.plusMonths(1).atDay(1).atStartOfDay();

        var result = dashboardRepository.findLaneigeBestsellerTop5(start, end);

        String snapshotTimeStr = (result.snapshotTime() == null)
                ? null
                : result.snapshotTime().toString().replace('T', ' ');

        return new BestsellerTop5Response(month, snapshotTimeStr, result.items());
    }
}