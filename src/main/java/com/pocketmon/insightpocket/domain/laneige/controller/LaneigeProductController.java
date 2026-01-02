package com.pocketmon.insightpocket.domain.laneige.controller;

import com.pocketmon.insightpocket.domain.laneige.dto.LaneigeProductsResponse;
import com.pocketmon.insightpocket.domain.laneige.service.LaneigeProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/laneige")
public class LaneigeProductController {

    private final LaneigeProductService laneigeProductService;

    @GetMapping("/products")
    public LaneigeProductsResponse getLaneigeProducts() {
        return laneigeProductService.getLaneigeProducts();
    }
}