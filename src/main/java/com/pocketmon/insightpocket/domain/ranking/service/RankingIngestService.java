package com.pocketmon.insightpocket.domain.ranking.service;

import com.pocketmon.insightpocket.domain.ranking.dto.RankingSnapshotIngestRequest;
import com.pocketmon.insightpocket.domain.ranking.dto.RankingSnapshotIngestResponse;
import com.pocketmon.insightpocket.domain.ranking.entity.Category;
import com.pocketmon.insightpocket.domain.ranking.entity.RankingItem;
import com.pocketmon.insightpocket.domain.ranking.entity.RankingSnapshot;
import com.pocketmon.insightpocket.domain.ranking.repository.CategoryRepository;
import com.pocketmon.insightpocket.domain.ranking.repository.RankingItemRepository;
import com.pocketmon.insightpocket.domain.ranking.repository.RankingSnapshotRepository;
import com.pocketmon.insightpocket.global.exception.CustomException;
import com.pocketmon.insightpocket.global.response.ErrorCode;
import com.pocketmon.insightpocket.global.util.SnapshotTimeParser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingIngestService {

    private final CategoryRepository categoryRepository;
    private final RankingSnapshotRepository snapshotRepository;
    private final RankingItemRepository itemRepository;
    private final SnapshotTimeParser snapshotTimeParser;

    /**
     * 스냅샷 1세트(카테고리 5개)를 한 번에 적재
     * - 요청 배열 비어있으면 400-1
     * - snapshot_time 형식 오류면 400-2
     * - snapshot_time 불일치면 400-3
     * - items 비어있으면 400-4
     * - category 없음 404
     * - 그 외 처리 실패 500
     */
    @Transactional
    public List<RankingSnapshotIngestResponse> ingestBatch(List<RankingSnapshotIngestRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new CustomException(ErrorCode.INGEST_EMPTY_PAYLOAD);
        }

        // snapshot_time 동일성 + 형식 체크
        String snapshotTimeStr = requests.get(0).getSnapshotTime();
        if (snapshotTimeStr == null || snapshotTimeStr.isBlank()) {
            throw new CustomException(ErrorCode.INGEST_INVALID_SNAPSHOT_TIME);
        }

        LocalDateTime snapshotTime = snapshotTimeParser.parse(snapshotTimeStr);

        // 공통 검증
        for (RankingSnapshotIngestRequest r : requests) {
            if (r == null) throw new CustomException(ErrorCode.INGEST_BAD_REQUEST);

            if (r.getSnapshotTime() == null || r.getSnapshotTime().isBlank()) {
                throw new CustomException(ErrorCode.INGEST_INVALID_SNAPSHOT_TIME);
            }
            if (!snapshotTimeStr.equals(r.getSnapshotTime())) {
                throw new CustomException(ErrorCode.INGEST_SNAPSHOT_TIME_MISMATCH);
            }
            if (r.getItems() == null || r.getItems().isEmpty()) {
                throw new CustomException(ErrorCode.INGEST_ITEMS_EMPTY);
            }
            for (RankingSnapshotIngestRequest.Item it : r.getItems()) {
                if (it == null) throw new CustomException(ErrorCode.INGEST_BAD_REQUEST);
            }
        }

        List<RankingSnapshotIngestResponse> responses = new ArrayList<>();
        for (RankingSnapshotIngestRequest req : requests) {
            responses.add(ingestSingle(req, snapshotTime));
        }

        return responses;
    }

    /**
     * 단일 카테고리 1개 ingest (기존 로직 그대로 분리)
     * - (category_id, snapshot_time) 유니크 기반 멱등
     * - (snapshot_id, rank) 기준 upsert
     */
    private RankingSnapshotIngestResponse ingestSingle(
            RankingSnapshotIngestRequest req,
            LocalDateTime snapshotTime
    ) {
        Category category = categoryRepository.findByCode(req.getCategory())
                .orElseThrow(() -> new CustomException(ErrorCode.INGEST_TARGET_NOT_FOUND));

        // 있으면 재사용, 없으면 생성
        RankingSnapshot snapshot = snapshotRepository
                .findByCategoryAndSnapshotTime(category, snapshotTime)
                .orElseGet(() ->
                        snapshotRepository.save(RankingSnapshot.create(snapshotTime, category))
                );

        int inserted = 0;
        int updated = 0;

        for (RankingSnapshotIngestRequest.Item it : req.getItems()) {
            RankingItem item =
                    itemRepository.findBySnapshotAndRank(snapshot, it.getRank()).orElse(null);

            if (item == null) {
                itemRepository.save(
                        RankingItem.create(
                                snapshot,
                                it.getRank(),
                                it.getProductName(),
                                it.getPrice(),
                                it.getIsLaneige()
                        )
                );
                inserted++;
            } else {
                item.update(it.getProductName(), it.getPrice(), it.getIsLaneige());
                updated++;
            }
        }

        return new RankingSnapshotIngestResponse(
                snapshot.getId(),
                inserted,
                updated
        );
    }
}