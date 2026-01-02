package com.pocketmon.insightpocket.domain.laneige.service;

import com.pocketmon.insightpocket.domain.laneige.dto.RankTrendResponse;
import com.pocketmon.insightpocket.domain.laneige.enums.RankRange;
import com.pocketmon.insightpocket.domain.laneige.repository.LaneigeProductRepository;
import com.pocketmon.insightpocket.domain.laneige.repository.LaneigeRankTrendRepository;
import com.pocketmon.insightpocket.domain.laneige.repository.LaneigeRankTrendRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * - WEEK/MONTH: 최근 N일(오늘 포함) 버킷(yyyy-MM-dd) 생성
 *   각 버킷은 "해당 날짜의 마지막 스냅샷" 값 사용 (없으면 null)
 *
 * - YEAR: 최근 12개월(이번 달 포함) 버킷(yyyy-MM) 생성
 *   각 버킷은 "해당 월의 마지막 스냅샷" 값 사용 (없으면 null)
 *
 * rank_1, rank_2 + rank_1_category, rank_2_category 반환
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LaneigeRankTrendService {

    private final LaneigeRankTrendRepository laneigeRankTrendRepository;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    public RankTrendResponse getRankTrends(Long productId, RankRange range) {
        // 기준 시각(서버가 UTC여도 KST 기준으로 버킷 끊기 위해 ZonedDateTime 사용)
        ZonedDateTime nowKst = ZonedDateTime.now(KST);
        LocalDate today = nowKst.toLocalDate();

        if (range == RankRange.WEEK) {
            LocalDate startDay = today.minusDays(6); // 오늘 포함 7일
            LocalDateTime startTime = startDay.atStartOfDay();
            LocalDateTime endTime = nowKst.toLocalDateTime();

            List<LaneigeRankTrendRow> rows =
                    laneigeRankTrendRepository.findRankTrends(productId, startTime, endTime);

            List<RankTrendResponse.RankTrendItem> items = buildDailyItems(startDay, today, rows);
            return new RankTrendResponse(productId, range, items);
        }

        if (range == RankRange.MONTH) {
            LocalDate startDay = today.minusDays(29); // 오늘 포함 30일
            LocalDateTime startTime = startDay.atStartOfDay();
            LocalDateTime endTime = nowKst.toLocalDateTime();

            List<LaneigeRankTrendRow> rows =
                    laneigeRankTrendRepository.findRankTrends(productId, startTime, endTime);

            List<RankTrendResponse.RankTrendItem> items = buildDailyItems(startDay, today, rows);
            return new RankTrendResponse(productId, range, items);
        }

        // YEAR
        YearMonth thisMonth = YearMonth.from(today);
        YearMonth startMonth = thisMonth.minusMonths(11); // 이번 달 포함 12개월

        LocalDateTime startTime = startMonth.atDay(1).atStartOfDay();
        LocalDateTime endTime = nowKst.toLocalDateTime();

        List<LaneigeRankTrendRow> rows =
                laneigeRankTrendRepository.findRankTrends(productId, startTime, endTime);

        List<RankTrendResponse.RankTrendItem> items = buildMonthlyItems(startMonth, thisMonth, rows);
        return new RankTrendResponse(productId, range, items);
    }

    /**
     * 날짜별(yyyy-MM-dd) 버킷에 대해 "그 날짜의 마지막 스냅샷"을 선택
     */
    private List<RankTrendResponse.RankTrendItem> buildDailyItems(
            LocalDate startDay,
            LocalDate endDay,
            List<LaneigeRankTrendRow> rows
    ) {
        // rows는 snapshot_time 오름차순이라고 가정(쿼리에서 ORDER BY)
        // 같은 날짜에 여러 행이 있으면 마지막 행이 덮어씌워지며 최종적으로 "그날의 마지막 스냅샷"이 된다.
        Map<LocalDate, LaneigeRankTrendRow> lastRowByDay = new HashMap<>();
        for (LaneigeRankTrendRow row : rows) {
            if (row.getSnapshotTime() == null) continue;
            LocalDate day = row.getSnapshotTime().atZone(KST).toLocalDate();
            lastRowByDay.put(day, row);
        }

        List<RankTrendResponse.RankTrendItem> result = new ArrayList<>();
        LocalDate cur = startDay;
        while (!cur.isAfter(endDay)) {
            LaneigeRankTrendRow picked = lastRowByDay.get(cur);
            result.add(new RankTrendResponse.RankTrendItem(
                    cur.format(DAY_FMT),
                    picked == null ? null : picked.getRank1(),
                    picked == null ? null : picked.getRank1Category(),
                    picked == null ? null : picked.getRank2(),
                    picked == null ? null : picked.getRank2Category()
            ));
            cur = cur.plusDays(1);
        }
        return result;
    }

    /**
     * 월별(yyyy-MM) 버킷에 대해 "그 월의 마지막 스냅샷"을 선택
     */
    private List<RankTrendResponse.RankTrendItem> buildMonthlyItems(
            YearMonth startMonth,
            YearMonth endMonth,
            List<LaneigeRankTrendRow> rows
    ) {
        Map<YearMonth, LaneigeRankTrendRow> lastRowByMonth = new HashMap<>();
        for (LaneigeRankTrendRow row : rows) {
            if (row.getSnapshotTime() == null) continue;
            YearMonth ym = YearMonth.from(row.getSnapshotTime().atZone(KST).toLocalDate());
            lastRowByMonth.put(ym, row);
        }

        List<RankTrendResponse.RankTrendItem> result = new ArrayList<>();
        YearMonth cur = startMonth;
        while (!cur.isAfter(endMonth)) {
            LaneigeRankTrendRow picked = lastRowByMonth.get(cur);
            result.add(new RankTrendResponse.RankTrendItem(
                    cur.format(MONTH_FMT),
                    picked == null ? null : picked.getRank1(),
                    picked == null ? null : picked.getRank1Category(),
                    picked == null ? null : picked.getRank2(),
                    picked == null ? null : picked.getRank2Category()
            ));
            cur = cur.plusMonths(1);
        }
        return result;
    }
}