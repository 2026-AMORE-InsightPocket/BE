package com.pocketmon.insightpocket.domain.ranking.service;

import com.pocketmon.insightpocket.domain.ranking.dto.RankingSnapshotIngestRequest;
import com.pocketmon.insightpocket.domain.ranking.dto.RankingSnapshotIngestResponse;
import com.pocketmon.insightpocket.domain.ranking.entity.Category;
import com.pocketmon.insightpocket.domain.ranking.entity.RankingItem;
import com.pocketmon.insightpocket.domain.ranking.entity.RankingSnapshot;
import com.pocketmon.insightpocket.domain.ranking.repository.CategoryRepository;
import com.pocketmon.insightpocket.domain.ranking.repository.RankingItemRepository;
import com.pocketmon.insightpocket.domain.ranking.repository.RankingSnapshotRepository;
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

    private static final DateTimeFormatter SNAPSHOT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final CategoryRepository categoryRepository;
    private final RankingSnapshotRepository snapshotRepository;
    private final RankingItemRepository itemRepository;

    /**
     * 스냅샷 1세트(카테고리 5개)를 한 번에 ingest.
     * - 요청 배열이 비어있으면 400
     * - snapshot_time이 서로 다르면 400
     * - 전체를 하나의 트랜잭션으로 처리 (중간에 하나라도 실패하면 롤백)
     */
    @Transactional
    public List<RankingSnapshotIngestResponse> ingestBatch(List<RankingSnapshotIngestRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("empty snapshot payload");
        }

        String snapshotTimeStr = requests.get(0).getSnapshotTime();
        for (RankingSnapshotIngestRequest r : requests) {
            if (r.getSnapshotTime() == null || !snapshotTimeStr.equals(r.getSnapshotTime())) {
                throw new IllegalArgumentException("snapshot_time mismatch");
            }
        }

        List<RankingSnapshotIngestResponse> responses = new ArrayList<>();
        for (RankingSnapshotIngestRequest req : requests) {
            responses.add(ingestSingle(req));
        }
        return responses;
    }

    /**
     * 단일 카테고리 1개 ingest (기존 로직 그대로 분리)
     * - (category_id, snapshot_time) 유니크 기반 멱등
     * - (snapshot_id, rank) 기준 upsert
     */
    private RankingSnapshotIngestResponse ingestSingle(RankingSnapshotIngestRequest req) {
        LocalDateTime snapshotTime = LocalDateTime.parse(req.getSnapshotTime(), SNAPSHOT_FORMAT);

        Category category = categoryRepository.findByCode(req.getCategory())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 category: " + req.getCategory()));

        RankingSnapshot snapshot = snapshotRepository
                .findByCategoryAndSnapshotTime(category, snapshotTime)
                .orElseGet(() -> snapshotRepository.save(
                        RankingSnapshot.create(snapshotTime, category)
                ));

        int inserted = 0;
        int updated = 0;

        for (RankingSnapshotIngestRequest.Item it : req.getItems()) {
            RankingItem item = itemRepository.findBySnapshotAndRank(snapshot, it.getRank()).orElse(null);

            if (item == null) {
                RankingItem newItem = RankingItem.create(
                        snapshot,
                        it.getRank(),
                        it.getProductName(),
                        it.getPrice(),
                        it.getIsLaneige()
                );
                itemRepository.save(newItem);
                inserted++;
            } else {
                item.update(it.getProductName(), it.getPrice(), it.getIsLaneige());
                updated++;
            }
        }

        return new RankingSnapshotIngestResponse(snapshot.getId(), inserted, updated);
    }
}