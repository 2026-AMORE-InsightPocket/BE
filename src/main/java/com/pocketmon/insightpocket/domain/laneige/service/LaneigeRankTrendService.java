package com.pocketmon.insightpocket.domain.laneige.service;

import com.pocketmon.insightpocket.domain.laneige.dto.RankTrendResponse;
import com.pocketmon.insightpocket.domain.laneige.entity.LaneigeProduct;
import com.pocketmon.insightpocket.domain.laneige.enums.RankRange;
import com.pocketmon.insightpocket.domain.laneige.repository.LaneigeProductRepository;
import com.pocketmon.insightpocket.domain.laneige.repository.LaneigeRankTrendRepository;
import com.pocketmon.insightpocket.domain.laneige.repository.LaneigeRankTrendRow;
import com.pocketmon.insightpocket.global.exception.CustomException;
import com.pocketmon.insightpocket.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LaneigeRankTrendService {

    private final LaneigeRankTrendRepository laneigeRankTrendRepository;
    private final LaneigeProductRepository laneigeProductRepository;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    public RankTrendResponse getRankTrends(Long productId, RankRange range) {
        if (range == null) {
            throw new CustomException(ErrorCode.LANEIGE_INVALID_RANGE);
        }
        if (productId == null) {
            throw new CustomException(ErrorCode.LANEIGE_PRODUCT_NOT_FOUND);
        }

        LaneigeProduct product = laneigeProductRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.LANEIGE_PRODUCT_NOT_FOUND));

        String productName = product.getProductName();
        String style = product.getStyle();

        LocalDate today = LocalDate.now(KST);

        // endTime은 "내일 00:00"으로 잡아서 오늘 전체 버킷이 항상 포함되게
        LocalDateTime endExclusive = today.plusDays(1).atStartOfDay();

        return switch (range) {
            case WEEK -> {
                LocalDate startDay = today.minusDays(6);
                LocalDateTime startTime = startDay.atStartOfDay();

                List<LaneigeRankTrendRow> rows =
                        laneigeRankTrendRepository.findRankTrends(productId, startTime, endExclusive);

                List<RankTrendResponse.RankTrendItem> items =
                        buildDailyItems(startDay, today, rows);

                yield new RankTrendResponse(productId, productName, style, range, items);
            }

            case MONTH -> {
                LocalDate startDay = today.minusDays(29);
                LocalDateTime startTime = startDay.atStartOfDay();

                List<LaneigeRankTrendRow> rows =
                        laneigeRankTrendRepository.findRankTrends(productId, startTime, endExclusive);

                List<RankTrendResponse.RankTrendItem> items =
                        buildDailyItems(startDay, today, rows);

                yield new RankTrendResponse(productId, productName, style, range, items);
            }

            case YEAR -> {
                YearMonth thisMonth = YearMonth.from(today);
                YearMonth startMonth = thisMonth.minusMonths(11);

                LocalDateTime startTime = startMonth.atDay(1).atStartOfDay();
                LocalDateTime endMonthExclusive = thisMonth.plusMonths(1).atDay(1).atStartOfDay();

                List<LaneigeRankTrendRow> rows =
                        laneigeRankTrendRepository.findRankTrends(productId, startTime, endMonthExclusive);

                List<RankTrendResponse.RankTrendItem> items =
                        buildMonthlyItems(startMonth, thisMonth, rows);

                yield new RankTrendResponse(productId, productName, style, range, items);
            }
        };
    }

    private List<RankTrendResponse.RankTrendItem> buildDailyItems(
            LocalDate startDay,
            LocalDate endDay,
            List<LaneigeRankTrendRow> rows
    ) {
        Map<LocalDate, LaneigeRankTrendRow> lastRowByDay = new HashMap<>();
        for (LaneigeRankTrendRow row : rows) {
            if (row.getSnapshotTime() == null) continue;
            LocalDate day = row.getSnapshotTime().toLocalDate();
            lastRowByDay.put(day, row);
        }

        List<RankTrendResponse.RankTrendItem> result = new ArrayList<>();
        for (LocalDate cur = startDay; !cur.isAfter(endDay); cur = cur.plusDays(1)) {
            LaneigeRankTrendRow picked = lastRowByDay.get(cur);
            result.add(new RankTrendResponse.RankTrendItem(
                    cur.format(DAY_FMT),
                    picked == null ? null : picked.getRank1(),
                    picked == null ? null : picked.getRank1Category(),
                    picked == null ? null : picked.getRank2(),
                    picked == null ? null : picked.getRank2Category()
            ));
        }
        return result;
    }

    private List<RankTrendResponse.RankTrendItem> buildMonthlyItems(
            YearMonth startMonth,
            YearMonth endMonth,
            List<LaneigeRankTrendRow> rows
    ) {
        Map<YearMonth, LaneigeRankTrendRow> lastRowByMonth = new HashMap<>();
        for (LaneigeRankTrendRow row : rows) {
            if (row.getSnapshotTime() == null) continue;
            YearMonth ym = YearMonth.from(row.getSnapshotTime().toLocalDate());
            lastRowByMonth.put(ym, row);
        }

        List<RankTrendResponse.RankTrendItem> result = new ArrayList<>();
        for (YearMonth cur = startMonth; !cur.isAfter(endMonth); cur = cur.plusMonths(1)) {
            LaneigeRankTrendRow picked = lastRowByMonth.get(cur);
            result.add(new RankTrendResponse.RankTrendItem(
                    cur.format(MONTH_FMT),
                    picked == null ? null : picked.getRank1(),
                    picked == null ? null : picked.getRank1Category(),
                    picked == null ? null : picked.getRank2(),
                    picked == null ? null : picked.getRank2Category()
            ));
        }
        return result;
    }
}