package com.pocketmon.insightpocket.domain.dashboard.dto;

public record BestsellerTop5Item(
        int rank,
        long product_id,
        String product_name,
        long last_month_sales,
        Integer prev_month_rank,
        Integer rank_change
) {}