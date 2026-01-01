package com.pocketmon.insightpocket.domain.ranking.controller;

import com.pocketmon.insightpocket.domain.ranking.dto.RankingSnapshotIngestRequest;
import com.pocketmon.insightpocket.domain.ranking.dto.RankingSnapshotIngestResponse;
import com.pocketmon.insightpocket.domain.ranking.service.RankingIngestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rankings")
public class RankingIngestController {

    private final RankingIngestService rankingIngestService;

    @PostMapping("/snapshots")
    public ResponseEntity<List<RankingSnapshotIngestResponse>> ingestSnapshots(
            @RequestBody @Valid List<@Valid RankingSnapshotIngestRequest> requests
    ) {
        List<RankingSnapshotIngestResponse> result = rankingIngestService.ingestBatch(requests);
        return ResponseEntity.ok(result);
    }
}