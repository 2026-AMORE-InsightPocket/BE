package com.pocketmon.insightpocket.domain.ranking.controller;

import com.pocketmon.insightpocket.domain.ranking.dto.RankingCurrentResponse;
import com.pocketmon.insightpocket.domain.ranking.service.RankingCurrentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rankings")
public class RankingController {

    private final RankingCurrentService rankingCurrentService;

    @GetMapping("/current")
    public RankingCurrentResponse getCurrentRanking(
            @RequestParam("category") long categoryId
    ) {
        return rankingCurrentService.getCurrentRanking(categoryId);
    }
}