package com.pocketmon.insightpocket.domain.laneige.controller;

import com.pocketmon.insightpocket.domain.laneige.dto.LaneigeProductsResponse;
import com.pocketmon.insightpocket.domain.laneige.dto.RankTrendResponse;
import com.pocketmon.insightpocket.domain.laneige.enums.RankRange;
import com.pocketmon.insightpocket.domain.laneige.service.LaneigeProductService;
import com.pocketmon.insightpocket.domain.laneige.service.LaneigeRankTrendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/laneige/products")
public class LaneigeProductController {

    private final LaneigeProductService laneigeProductService;
    private final LaneigeRankTrendService rankTrendService;

    @GetMapping("")
    public LaneigeProductsResponse getLaneigeProducts() {
        return laneigeProductService.getLaneigeProducts();
    }

    @GetMapping("/{id}/rank-trends")
    public RankTrendResponse getRankTrends(
            @PathVariable("id") Long productId,
            @RequestParam RankRange range
    ) {
        return rankTrendService.getRankTrends(productId, range);
    }
}