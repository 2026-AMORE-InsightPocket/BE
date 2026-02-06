package com.pocketmon.insightpocket.domain.laneige.dto;

import java.math.BigDecimal;

public record LaneigeProductItem(
        long product_id,
        String image_url,
        String product_name,
        String style,
        BigDecimal price,
        Long rank_1,
        String rank_1_category,
        Long rank_2,
        String rank_2_category
) {}