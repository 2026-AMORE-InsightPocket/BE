package com.pocketmon.insightpocket.domain.ranking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class RankingSnapshotIngestRequest {

    @NotNull
    private String snapshotTime;

    @NotBlank
    private String category; // ex) "lip_care"

    @NotEmpty
    @Valid
    private List<Item> items;

    @Getter
    public static class Item {
        @NotNull
        private Integer rank; // 1~30

        @NotBlank
        private String productName;

        @NotNull
        private BigDecimal price;

        @NotNull
        private Boolean isLaneige;
    }
}