package com.pocketmon.insightpocket.domain.laneige.controller;

import com.pocketmon.insightpocket.domain.laneige.dto.LaneigeProductIngestRequest;
import com.pocketmon.insightpocket.domain.laneige.dto.LaneigeProductIngestResponse;
import com.pocketmon.insightpocket.domain.laneige.service.LaneigeProductIngestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/laneige")
public class LaneigeProductIngestController {

    private final LaneigeProductIngestService laneigeProductIngestService;

    @PostMapping("/products/ingest")
    public ResponseEntity<LaneigeProductIngestResponse> ingestProducts(
            @RequestBody @Valid List<@Valid LaneigeProductIngestRequest> requests
    ) {
        LaneigeProductIngestResponse res = laneigeProductIngestService.ingestBatch(requests);
        return ResponseEntity.ok(res);
    }
}