package com.pocketmon.insightpocket.domain.laneige.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LaneigeProductIngestResponse {

    private String snapshotTime;
    private int total;
    private int newProducts;
    private int updatedProducts;
}