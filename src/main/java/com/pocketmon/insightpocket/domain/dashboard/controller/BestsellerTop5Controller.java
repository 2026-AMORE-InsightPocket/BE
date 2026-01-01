package com.pocketmon.insightpocket.domain.dashboard.controller;

import com.pocketmon.insightpocket.domain.dashboard.dto.BestsellerTop5Response;
import com.pocketmon.insightpocket.domain.dashboard.service.BestsellerTop5Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class BestsellerTop5Controller {

    private final BestsellerTop5Service bestsellerTop5Service;

    @GetMapping("/bestsellers/top5")
    public BestsellerTop5Response top5(@RequestParam String month) {
        return bestsellerTop5Service.getLaneigeTop5(month);
    }
}