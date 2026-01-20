package com.pocketmon.insightpocket.domain.laneige.service;

import com.pocketmon.insightpocket.domain.laneige.dto.LaneigeProductIngestRequest;
import com.pocketmon.insightpocket.domain.laneige.dto.LaneigeProductIngestResponse;
import com.pocketmon.insightpocket.domain.laneige.entity.LaneigeAspectDetail;
import com.pocketmon.insightpocket.domain.laneige.entity.LaneigeProduct;
import com.pocketmon.insightpocket.domain.laneige.entity.LaneigeProductSnapshot;
import com.pocketmon.insightpocket.domain.laneige.entity.LaneigeSnapshotRun;
import com.pocketmon.insightpocket.domain.laneige.repository.LaneigeAspectDetailRepository;
import com.pocketmon.insightpocket.domain.laneige.repository.LaneigeProductRepository;
import com.pocketmon.insightpocket.domain.laneige.repository.LaneigeProductSnapshotRepository;
import com.pocketmon.insightpocket.domain.laneige.repository.LaneigeSnapshotRunRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LaneigeProductIngestService {

    private static final DateTimeFormatter SNAPSHOT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final LaneigeSnapshotRunRepository snapshotRunRepository;
    private final LaneigeProductRepository productRepository;
    private final LaneigeProductSnapshotRepository productSnapshotRepository;
    private final LaneigeAspectDetailRepository aspectDetailRepository;

    @Transactional
    public LaneigeProductIngestResponse ingestBatch(List<LaneigeProductIngestRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return new LaneigeProductIngestResponse(null, 0, 0, 0);
        }

        String snapshotTimeStr = requests.get(0).getSnapshotTime();
        LocalDateTime snapshotTime = LocalDateTime.parse(snapshotTimeStr, SNAPSHOT_FORMAT);

        LaneigeSnapshotRun run = snapshotRunRepository.findBySnapshotTime(snapshotTime)
                .orElseGet(() -> snapshotRunRepository.save(LaneigeSnapshotRun.create(snapshotTime)));

        Set<String> urls = new HashSet<>();
        for (LaneigeProductIngestRequest r : requests) {
            if (r.getProductUrl() != null) urls.add(r.getProductUrl());
        }

        Map<String, LaneigeProduct> productByUrl = new HashMap<>();
        productRepository.findAllByProductUrlIn(urls)
                .forEach(p -> productByUrl.put(p.getProductUrl(), p));

        int total = requests.size();
        int newProducts = 0;
        int updatedProducts = 0;

        for (LaneigeProductIngestRequest r : requests) {
            // ---- Product upsert ----
            LaneigeProduct product = productByUrl.get(r.getProductUrl());

            boolean productChanged = false;

            if (product == null) {
                product = LaneigeProduct.create(
                        r.getProductUrl(),
                        r.getProductName(),
                        r.getImageUrl(),
                        r.getStyle(),
                        r.getCustomersSay(),
                        r.getCustomersSayHash()
                );
                productRepository.save(product);
                productByUrl.put(product.getProductUrl(), product);
                newProducts++;
            } else {
                product.updateBasicInfo(r.getProductName(), r.getImageUrl(), r.getStyle());

                String beforeHash = product.getCustomersSayHash();
                product.updateCustomersSayIfChanged(r.getCustomersSay(), r.getCustomersSayHash());
                String afterHash = product.getCustomersSayHash();

                if (!Objects.equals(beforeHash, afterHash)) productChanged = true;
                if (productChanged) updatedProducts++;
            }

            // ---- Snapshot upsert ----
            final LaneigeProduct finalProduct = product;

            LaneigeProductSnapshot ps = productSnapshotRepository
                    .findBySnapshotRunAndProduct(run, product)
                    .orElseGet(() -> productSnapshotRepository.save(
                            LaneigeProductSnapshot.create(run, finalProduct, r.getPrice())
                    ));

            // price가 있을 때만 업데이트
            if (r.getPrice() != null) {
                ps.updatePrice(r.getPrice());
            }

            // review_count null -> 0
            long reviewCount = (r.getReviewCount() == null) ? 0L : r.getReviewCount();

            // metrics 업데이트: "별점 퍼센트"는 null 섞이면 덮어쓰기 금지
            boolean hasAllRatingPcts =
                    r.getRating5Pct() != null &&
                            r.getRating4Pct() != null &&
                            r.getRating3Pct() != null &&
                            r.getRating2Pct() != null &&
                            r.getRating1Pct() != null;

            // 퍼센트가 다 있을 때만 그대로 업데이트
            Integer rating5 = hasAllRatingPcts ? r.getRating5Pct() : null;
            Integer rating4 = hasAllRatingPcts ? r.getRating4Pct() : null;
            Integer rating3 = hasAllRatingPcts ? r.getRating3Pct() : null;
            Integer rating2 = hasAllRatingPcts ? r.getRating2Pct() : null;
            Integer rating1 = hasAllRatingPcts ? r.getRating1Pct() : null;

            ps.updateMetrics(
                    reviewCount,
                    r.getRating(),          // rating은 null이면 엔티티에서 어떻게 처리하는지에 따라(그대로 둠)
                    rating5,
                    rating4,
                    rating3,
                    rating2,
                    rating1,
                    r.getLastMonthSales()   // 이것도 null이면 엔티티에서 덮어쓸 수 있음(원하면 보호 로직 추가 가능)
            );

            // rank 업데이트: rank 값이 null이면 (값+카테고리) 덮어쓰기 금지
            Long rank1 = r.getRank1();
            String rank1Category = r.getRank1Category();
            Long rank2 = r.getRank2();
            String rank2Category = r.getRank2Category();

            boolean hasRank1 = (rank1 != null);
            boolean hasRank2 = (rank2 != null);

            ps.updateRanks(
                    hasRank1 ? rank1 : null,
                    hasRank1 ? rank1Category : null,
                    hasRank2 ? rank2 : null,
                    hasRank2 ? rank2Category : null
            );

            ps.updateCustomersSay(r.getCustomersSay());

            // ---- Aspect upsert ---- (동일)
            if (r.getAspectDetails() != null) {
                for (LaneigeProductIngestRequest.AspectDetail a : r.getAspectDetails()) {
                    if (a == null) continue;

                    String aspectName = a.getAspectName();
                    if (aspectName == null || aspectName.isBlank()) continue;

                    LaneigeAspectDetail detail = aspectDetailRepository
                            .findByProductSnapshotAndAspectName(ps, aspectName)
                            .orElseGet(() -> aspectDetailRepository.save(
                                    LaneigeAspectDetail.create(ps, aspectName)
                            ));

                    detail.updateMentions(
                            a.getMentionTotal(),
                            a.getMentionPositive(),
                            a.getMentionNegative(),
                            a.getSummary()
                    );
                }
            }
        }

        return new LaneigeProductIngestResponse(snapshotTimeStr, total, newProducts, updatedProducts);
    }
}