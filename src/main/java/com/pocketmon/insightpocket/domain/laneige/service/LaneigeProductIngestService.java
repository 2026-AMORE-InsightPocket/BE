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
import com.pocketmon.insightpocket.global.exception.CustomException;
import com.pocketmon.insightpocket.global.response.ErrorCode;
import com.pocketmon.insightpocket.global.util.SnapshotTimeParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LaneigeProductIngestService {

    private final LaneigeSnapshotRunRepository snapshotRunRepository;
    private final LaneigeProductRepository productRepository;
    private final LaneigeProductSnapshotRepository productSnapshotRepository;
    private final LaneigeAspectDetailRepository aspectDetailRepository;

    @Transactional
    public LaneigeProductIngestResponse ingestBatch(List<LaneigeProductIngestRequest> requests) {

        if (requests == null || requests.isEmpty()) {
            throw new CustomException(ErrorCode.INGEST_EMPTY_PAYLOAD);
        }

        String snapshotTimeStr = requests.get(0).getSnapshotTime();
        if (snapshotTimeStr == null || snapshotTimeStr.isBlank()) {
            throw new CustomException(ErrorCode.INGEST_INVALID_SNAPSHOT_TIME);
        }

        LocalDateTime snapshotTime = SnapshotTimeParser.parse(snapshotTimeStr);

        // snapshot_time 동일성 검증
        for (LaneigeProductIngestRequest r : requests) {
            if (r == null) throw new CustomException(ErrorCode.INGEST_BAD_REQUEST);

            if (r.getSnapshotTime() == null || r.getSnapshotTime().isBlank()) {
                throw new CustomException(ErrorCode.INGEST_INVALID_SNAPSHOT_TIME);
            }
            if (!snapshotTimeStr.equals(r.getSnapshotTime())) {
                throw new CustomException(ErrorCode.INGEST_SNAPSHOT_TIME_MISMATCH);
            }
        }

        // run 멱등
        LaneigeSnapshotRun run = snapshotRunRepository.findBySnapshotTime(snapshotTime).orElse(null);
        if (run == null) {
            run = snapshotRunRepository.save(LaneigeSnapshotRun.create(snapshotTime));
        }

        // URL 모아서 기존 제품 선조회
        Set<String> urls = new HashSet<>();
        for (LaneigeProductIngestRequest r : requests) {
            if (r.getProductUrl() != null && !r.getProductUrl().isBlank()) {
                urls.add(r.getProductUrl());
            }
        }

        Map<String, LaneigeProduct> productByUrl = new HashMap<>();
        if (!urls.isEmpty()) {
            productRepository.findAllByProductUrlIn(urls)
                    .forEach(p -> productByUrl.put(p.getProductUrl(), p));
        }

        int total = requests.size();
        int newProducts = 0;
        int updatedProducts = 0;

        for (LaneigeProductIngestRequest r : requests) {

            // (요청 DTO에 @NotBlank 있지만, 혹시라도 방어)
            if (r.getProductUrl() == null || r.getProductUrl().isBlank()) {
                throw new CustomException(ErrorCode.INGEST_BAD_REQUEST);
            }
            if (r.getProductName() == null || r.getProductName().isBlank()) {
                throw new CustomException(ErrorCode.INGEST_BAD_REQUEST);
            }

            // ---- Product upsert ----
            LaneigeProduct product = productByUrl.get(r.getProductUrl());

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
                String beforeHash = product.getCustomersSayHash();

                // 엔티티 리턴값으로 변경 여부 판단
                boolean basicChanged =
                        product.updateBasicInfo(r.getProductName(), r.getImageUrl(), r.getStyle());

                product.updateCustomersSayIfChanged(r.getCustomersSay(), r.getCustomersSayHash());

                String afterHash = product.getCustomersSayHash();

                if (basicChanged || !Objects.equals(beforeHash, afterHash)) {
                    updatedProducts++;
                }
            }

            // ---- Snapshot upsert (run+product 멱등) ----
            LaneigeProductSnapshot ps =
                    productSnapshotRepository.findBySnapshotRunAndProduct(run, product).orElse(null);

            if (ps == null) {
                ps = productSnapshotRepository.save(
                        LaneigeProductSnapshot.create(run, product, r.getPrice())
                );
            }

            // price는 있을 때만
            if (r.getPrice() != null) {
                ps.updatePrice(r.getPrice());
            }

            Long reviewCount = r.getReviewCount();
            BigDecimal rating = (r.getRating() == null) ? ps.getRating() : r.getRating();

            boolean hasAllRatingPcts =
                    r.getRating5Pct() != null &&
                            r.getRating4Pct() != null &&
                            r.getRating3Pct() != null &&
                            r.getRating2Pct() != null &&
                            r.getRating1Pct() != null;

            Integer rating5 = hasAllRatingPcts ? r.getRating5Pct() : ps.getRating5Pct();
            Integer rating4 = hasAllRatingPcts ? r.getRating4Pct() : ps.getRating4Pct();
            Integer rating3 = hasAllRatingPcts ? r.getRating3Pct() : ps.getRating3Pct();
            Integer rating2 = hasAllRatingPcts ? r.getRating2Pct() : ps.getRating2Pct();
            Integer rating1 = hasAllRatingPcts ? r.getRating1Pct() : ps.getRating1Pct();

            Long lastMonthSales = (r.getLastMonthSales() == null) ? ps.getLastMonthSales() : r.getLastMonthSales();

            ps.updateMetrics(
                    reviewCount,
                    rating,
                    rating5, rating4, rating3, rating2, rating1,
                    lastMonthSales
            );

            // ranks null 덮어쓰기 방지
            Long rank1 = (r.getRank1() == null) ? ps.getRank1() : r.getRank1();
            String rank1Category = (r.getRank1() == null) ? ps.getRank1Category() : r.getRank1Category();

            Long rank2 = (r.getRank2() == null) ? ps.getRank2() : r.getRank2();
            String rank2Category = (r.getRank2() == null) ? ps.getRank2Category() : r.getRank2Category();

            ps.updateRanks(rank1, rank1Category, rank2, rank2Category);

            // customersSay highlight: null이면 덮어쓰기 방지
            if (r.getCustomersSay() != null) {
                ps.updateCustomersSay(r.getCustomersSay());
            }

            // ---- Aspect upsert ----
            if (r.getAspectDetails() != null) {
                for (LaneigeProductIngestRequest.AspectDetail a : r.getAspectDetails()) {
                    if (a == null) continue;
                    if (a.getAspectName() == null || a.getAspectName().isBlank()) continue;

                    LaneigeAspectDetail detail =
                            aspectDetailRepository.findByProductSnapshotAndAspectName(ps, a.getAspectName())
                                    .orElse(null);

                    if (detail == null) {
                        detail = aspectDetailRepository.save(
                                LaneigeAspectDetail.create(ps, a.getAspectName())
                        );
                    }

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