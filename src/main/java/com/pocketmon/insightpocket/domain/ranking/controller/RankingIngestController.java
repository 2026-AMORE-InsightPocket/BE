package com.pocketmon.insightpocket.domain.ranking.controller;

import com.pocketmon.insightpocket.domain.ranking.dto.RankingSnapshotIngestRequest;
import com.pocketmon.insightpocket.domain.ranking.dto.RankingSnapshotIngestResponse;
import com.pocketmon.insightpocket.domain.ranking.service.RankingIngestService;
import com.pocketmon.insightpocket.global.response.ApiResponse;
import com.pocketmon.insightpocket.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "베스트셀러 랭킹 크롤링 데이터 적재 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rankings")
public class RankingIngestController {

    private final RankingIngestService rankingIngestService;

    @Operation(
            summary = "랭킹 스냅샷 배치 적재",
            description = """
                    크롤러가 수집한 랭킹 스냅샷 데이터를 배치로 DB에 저장합니다.
                    - 요청은 배열(List) 형태로 전달합니다.
                    - 검증 실패 시 400(VALIDATION_FAILED) 응답을 반환합니다.
                    """
    )
    @PostMapping("/snapshots/ingest")
    public ApiResponse<List<RankingSnapshotIngestResponse>> ingestSnapshots(
            @RequestBody @Valid List<@Valid RankingSnapshotIngestRequest> requests
    ) {
        List<RankingSnapshotIngestResponse> result = rankingIngestService.ingestBatch(requests);
        return ApiResponse.onSuccess(result, SuccessCode.INGEST_SUCCESS);
    }
}