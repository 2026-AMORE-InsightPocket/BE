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

@Service
@RequiredArgsConstructor
public class RankingIngestService {

    private static final DateTimeFormatter SNAPSHOT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final CategoryRepository categoryRepository;
    private final RankingSnapshotRepository snapshotRepository;
    private final RankingItemRepository itemRepository;

    @Transactional
    public RankingSnapshotIngestResponse ingest(RankingSnapshotIngestRequest req) {

        LocalDateTime snapshotTime =
                LocalDateTime.parse(req.getSnapshotTime(), SNAPSHOT_FORMAT);

        Category category = categoryRepository.findByCode(req.getCategory())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 category: " + req.getCategory()));

        // (category_id, snapshot_time) 유니크 기반으로 멱등 처리
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