package com.pocketmon.insightpocket.domain.ranking.dto;

import java.math.BigDecimal;

public record LaneigeProductListItem(
        long product_id,
        String image_url,
        String product_name,
        String style,
        BigDecimal price,
        Integer rank_1,
        String rank_1_category,
        Integer rank_2,
        String rank_2_category
) {}