package com.pocketmon.insightpocket.domain.laneige.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.pocketmon.insightpocket.domain.laneige.enums.RankRange;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RankTrendResponse {

    private Long productId;
    private RankRange range;

    /**
     * WEEK/MONTH: bucket = yyyy-MM-dd
     * YEAR:       bucket = yyyy-MM
     */
    private List<RankTrendItem> items;

    @Getter
    @AllArgsConstructor
    public static class RankTrendItem {
        private String bucket;

        private Long rank1;
        private String rank1Category;

        private Long rank2;
        private String rank2Category;
    }
}