package com.pocketmon.insightpocket.domain.dashboard.service;

import com.pocketmon.insightpocket.domain.dashboard.dto.BestsellerTop1Response;
import com.pocketmon.insightpocket.domain.dashboard.dto.BestsellerTop5Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BestsellerTop1Service {

    private final BestsellerTop5Service top5Service;

    public BestsellerTop1Response getLastMonthWinner(String month) {

        BestsellerTop5Response top5 = top5Service.getLaneigeTop5(month);

        if (top5.items().isEmpty()) {
            return new BestsellerTop1Response(month, top5.snapshot_time(), null);
        }

        return new BestsellerTop1Response(
                month,
                top5.snapshot_time(),
                top5.items().get(0) // 1위
        );
    }
}