package com.pocketmon.insightpocket.domain.dashboard.dto;

import java.math.BigDecimal;

public record BestsellerTop5Item(
        int rank,
        long product_id,
        String image_url,
        String product_name,
        long last_month_sales,
        BigDecimal rating,
        long review_count,
        Integer prev_month_rank,
        Integer rank_change
) {}