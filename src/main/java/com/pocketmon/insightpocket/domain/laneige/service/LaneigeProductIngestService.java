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

        // 1) snapshot run upsert
        String snapshotTimeStr = requests.get(0).getSnapshotTime();
        LocalDateTime snapshotTime = LocalDateTime.parse(snapshotTimeStr, SNAPSHOT_FORMAT);

        LaneigeSnapshotRun run = snapshotRunRepository.findBySnapshotTime(snapshotTime)
                .orElseGet(() -> snapshotRunRepository.save(LaneigeSnapshotRun.create(snapshotTime)));

        // 2) product_url 일괄 조회로 캐싱
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
                // 기본정보는 항상 최신으로
                // (null 들어오면 기존값 날아갈 수 있으니, 크롤러가 null 보내는 케이스 있으면 여기서 방어 추가하자)
                product.updateBasicInfo(r.getProductName(), r.getImageUrl(), r.getStyle());

                // customersSay는 hash 바뀔 때만 갱신
                String beforeHash = product.getCustomersSayHash();
                product.updateCustomersSayIfChanged(r.getCustomersSay(), r.getCustomersSayHash());
                String afterHash = product.getCustomersSayHash();

                // updatedProducts 집계 기준(원하면 더 엄격하게 바꿔도 됨)
                if (!Objects.equals(beforeHash, afterHash)) productChanged = true;

                // productName/style/imageUrl 변경까지 집계하고 싶으면 비교 로직 추가하면 됨
                // 지금은 "hash 변경"을 핵심 변경으로 본다
                if (productChanged) updatedProducts++;
            }

            // ---- Snapshot upsert ----
            final LaneigeProduct finalProduct = product;

            LaneigeProductSnapshot ps = productSnapshotRepository
                    .findBySnapshotRunAndProduct(run, product)
                    .orElseGet(() -> productSnapshotRepository.save(
                            LaneigeProductSnapshot.create(run, finalProduct, r.getPrice())
                    ));

            // price NOT NULL: 항상 업데이트
            ps.updatePrice(r.getPrice());

            // review_count null -> 0 처리
            long reviewCount = (r.getReviewCount() == null) ? 0L : r.getReviewCount();

            ps.updateMetrics(
                    reviewCount,
                    r.getRating(),
                    r.getRating5Pct(),
                    r.getRating4Pct(),
                    r.getRating3Pct(),
                    r.getRating2Pct(),
                    r.getRating1Pct(),
                    r.getLastMonthSales()
            );

            ps.updateRanks(
                    r.getRank1(),
                    r.getRank1Category(),
                    r.getRank2(),
                    r.getRank2Category()
            );

            ps.updateCustomersSay(r.getCustomersSay());

            // ---- Aspect upsert ----
            if (r.getAspectDetails() != null) {
                for (LaneigeProductIngestRequest.AspectDetail a : r.getAspectDetails()) {
                    if (a == null) continue;

                    String aspectName = a.getAspectName();
                    if (aspectName == null || aspectName.isBlank()) {
                        continue;
                    }

                    LaneigeAspectDetail detail = aspectDetailRepository
                            .findByProductSnapshotAndAspectName(ps, aspectName)
                            .orElseGet(() -> {
                                LaneigeAspectDetail created = LaneigeAspectDetail.create(ps, aspectName);
                                return aspectDetailRepository.save(created);
                            });

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