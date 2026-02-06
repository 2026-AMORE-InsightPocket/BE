package com.pocketmon.insightpocket.domain.ranking.controller;

import com.pocketmon.insightpocket.domain.ranking.dto.RankingCurrentResponse;
import com.pocketmon.insightpocket.domain.ranking.service.RankingCurrentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Tag(
        name = "랭킹 히스토리 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rankings")
public class RankingController {

    private final RankingCurrentService rankingCurrentService;

    @Operation(
            summary = "카테고리별 현재 랭킹 조회",
            description = """
                특정 카테고리에 대해 가장 최근 스냅샷 기준의 상품 랭킹을 조회합니다.
                
                - 랭킹은 판매량, 리뷰 수 등 내부 기준에 따라 산정됩니다.
                - 최신 스냅샷이 존재하지 않는 경우 빈 결과가 반환될 수 있습니다.
                """
    )
    @GetMapping("/current")
    public RankingCurrentResponse getCurrentRanking(
            @RequestParam("category") long categoryId
    ) {
        return rankingCurrentService.getCurrentRanking(categoryId);
    }
}