package com.pocketmon.insightpocket.domain.ranking.controller;

import com.pocketmon.insightpocket.domain.ranking.dto.RankingSnapshotIngestRequest;
import com.pocketmon.insightpocket.domain.ranking.dto.RankingSnapshotIngestResponse;
import com.pocketmon.insightpocket.domain.ranking.service.RankingIngestService;
import com.pocketmon.insightpocket.global.response.ApiResponse;
import com.pocketmon.insightpocket.global.response.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rankings")
public class RankingIngestController {

    private final RankingIngestService rankingIngestService;

    @PostMapping("/snapshots/ingest")
    public ApiResponse<List<RankingSnapshotIngestResponse>> ingestSnapshots(
            @RequestBody @Valid List<@Valid RankingSnapshotIngestRequest> requests
    ) {
        List<RankingSnapshotIngestResponse> result = rankingIngestService.ingestBatch(requests);
        return ApiResponse.onSuccess(result, SuccessCode.INGEST_SUCCESS);
    }
}