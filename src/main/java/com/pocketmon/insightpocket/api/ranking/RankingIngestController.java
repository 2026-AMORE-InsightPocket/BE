package com.pocketmon.insightpocket.api.ranking;

import com.pocketmon.insightpocket.domain.ranking.dto.RankingSnapshotIngestRequest;
import com.pocketmon.insightpocket.domain.ranking.dto.RankingSnapshotIngestResponse;
import com.pocketmon.insightpocket.domain.ranking.service.RankingIngestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rankings")
public class RankingIngestController {

    private final RankingIngestService rankingIngestService;

    @PostMapping("/snapshots")
    public RankingSnapshotIngestResponse ingest(@RequestBody @Valid RankingSnapshotIngestRequest req) {
        return rankingIngestService.ingest(req);
    }
}