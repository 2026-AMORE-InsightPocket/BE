package com.pocketmon.insightpocket.domain.rag.service;

import com.pocketmon.insightpocket.domain.rag.entity.RagDoc;
import com.pocketmon.insightpocket.domain.rag.repository.RagDocRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DailyReportService {

    private static final Long DAILY_REPORT_TYPE_ID = 1L;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final RagDocRepository ragDocRepository;

    private static final String DAILY_REPORT_CODE = "DAILY_REPORT";

    public RagDoc getTodayDailyReport() {
        LocalDate todayKst = LocalDate.now(KST);

        return ragDocRepository
                .findDailyReportsByCodeAndDate(DAILY_REPORT_CODE, todayKst)
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new NoSuchElementException("오늘 생성된 데일리 리포트가 없습니다.")
                );
    }
}