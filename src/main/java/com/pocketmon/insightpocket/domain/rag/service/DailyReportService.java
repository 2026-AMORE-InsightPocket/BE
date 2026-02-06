package com.pocketmon.insightpocket.domain.rag.service;

import com.pocketmon.insightpocket.domain.rag.entity.RagDoc;
import com.pocketmon.insightpocket.domain.rag.repository.RagDocRepository;
import com.pocketmon.insightpocket.global.exception.CustomException;
import com.pocketmon.insightpocket.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DailyReportService {

    private final RagDocRepository ragDocRepository;

    private static final String DAILY_REPORT_CODE = "DAILY_REPORT";

    public RagDoc getLatestDailyReport() {
        return ragDocRepository
                .findTopByDocType_CodeOrderByReportDateDescCreatedAtDesc(DAILY_REPORT_CODE)
                .orElseThrow(() ->
                        new CustomException(ErrorCode.RAG_DAILY_REPORT_NOT_FOUND)
                );
    }
}