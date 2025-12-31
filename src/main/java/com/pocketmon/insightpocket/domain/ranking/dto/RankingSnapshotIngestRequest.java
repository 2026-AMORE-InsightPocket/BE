package com.pocketmon.insightpocket.domain.ranking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class RankingSnapshotIngestRequest {

    @NotNull
    private String snapshot_time;

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
        private String product_name;

        @NotNull
        private BigDecimal price;

        @NotNull
        private Boolean is_laneige;
    }
}