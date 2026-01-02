package com.pocketmon.insightpocket.domain.laneige.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
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


    @JsonAlias("rating_5_pct")
    private Integer rating5Pct;
    @JsonAlias("rating_4_pct")
    private Integer rating4Pct;
    @JsonAlias("rating_3_pct")
    private Integer rating3Pct;
    @JsonAlias("rating_2_pct")
    private Integer rating2Pct;
    @JsonAlias("rating_1_pct")
    private Integer rating1Pct;

    private Long lastMonthSales;

    @JsonAlias("rank_1")
    private Long rank1;
    @JsonAlias("rank_1_category")
    private String rank1Category;
    @JsonAlias("rank_2")
    private Long rank2;
    @JsonAlias("rank_2_category")
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