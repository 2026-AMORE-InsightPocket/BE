package com.pocketmon.insightpocket.domain.dashboard.controller;

import com.pocketmon.insightpocket.domain.dashboard.dto.BestsellerTop1Response;
import com.pocketmon.insightpocket.domain.dashboard.dto.BestsellerTop5Response;
import com.pocketmon.insightpocket.domain.dashboard.dto.RisingProductResponse;
import com.pocketmon.insightpocket.domain.dashboard.service.BestsellerTop1Service;
import com.pocketmon.insightpocket.domain.dashboard.service.BestsellerTop5Service;
import com.pocketmon.insightpocket.domain.dashboard.service.RisingProductService;
import com.pocketmon.insightpocket.global.response.ApiResponse;
import com.pocketmon.insightpocket.global.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final RisingProductService risingProductService;
    private final BestsellerTop5Service bestsellerTop5Service;
    private final BestsellerTop1Service bestsellerTop1Service;

    @GetMapping("/rising")
    public ApiResponse<RisingProductResponse> getRisingProducts() {
        return ApiResponse.onSuccess(
                risingProductService.getRisingProducts(),
                SuccessCode.OK
        );
    }

    @GetMapping("/bestsellers/top5")
    public ApiResponse<BestsellerTop5Response> top5(
            @RequestParam String month
    ) {
        return ApiResponse.onSuccess(
                bestsellerTop5Service.getLaneigeTop5(month),
                SuccessCode.OK
        );
    }

    @GetMapping("/bestsellers/top1")
    public ApiResponse<BestsellerTop1Response> top1(
            @RequestParam String month
    ) {
        return ApiResponse.onSuccess(
                bestsellerTop1Service.getLastMonthWinner(month),
                SuccessCode.OK
        );
    }
}