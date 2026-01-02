package com.pocketmon.insightpocket.domain.dashboard.dto;

import java.math.BigDecimal;

public record RisingProductItem(
        String image_url,
        String product_name,
        BigDecimal rating,
        Long review_count,
        Integer rank_change,
        String growth_rate   // "47% 성장", "NEW"
) {}