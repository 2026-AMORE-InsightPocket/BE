package com.pocketmon.insightpocket.domain.ranking.dto;

public record RankingCurrentItem(
        int rank,
        String product_name,
        boolean is_laneige,
        Integer prev_rank,
        Integer rank_change
) {}