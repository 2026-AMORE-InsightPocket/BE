package com.pocketmon.insightpocket.domain.laneige.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LaneigeProductIngestRequest {

    @NotNull
    private String snapshotTime;

    @NotBlank
    private String productUrl;

    @NotBlank
    private String productName;

    private String imageUrl;
    private String style;

    @NotNull
    private BigDecimal price;

    private Long reviewCount;
    private BigDecimal rating;

    private Integer rating5Pct;
    private Integer rating4Pct;
    private Integer rating3Pct;
    private Integer rating2Pct;
    private Integer rating1Pct;

    private Long lastMonthSales;

    private Long rank1;
    private String rank1Category;
    private Long rank2;
    private String rank2Category;

    private String customersSay;
    private String customersSayHash;

    @Valid
    private List<AspectDetail> aspectDetails;

    @Getter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class AspectDetail {

        @NotBlank
        private String aspectName;

        private Long mentionTotal;
        private Long mentionPositive;
        private Long mentionNegative;

        private String summary;
    }
}