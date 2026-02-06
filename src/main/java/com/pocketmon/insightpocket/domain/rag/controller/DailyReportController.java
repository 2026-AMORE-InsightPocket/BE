package com.pocketmon.insightpocket.domain.rag.controller;

import com.pocketmon.insightpocket.domain.rag.entity.RagDoc;
import com.pocketmon.insightpocket.domain.rag.service.DailyReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@Tag(
        name = "데일리 리포트 다운로드 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports/daily")
public class DailyReportController {

    private final DailyReportService dailyReportService;

    @Operation(
            summary = "오늘의 Daily Report 다운로드",
            description = "오늘 생성된 최신 Daily Report를 Markdown(.md) 파일로 다운로드합니다."
    )
    @GetMapping("/today/download")
    public ResponseEntity<Resource> downloadToday() {
        RagDoc doc = dailyReportService.getLatestDailyReport();

        String date = doc.getCreatedAt().toLocalDate().toString();
        String filename = "daily_report_" + date + ".md";
        byte[] content = doc.getBodyMd().getBytes(StandardCharsets.UTF_8);

        ByteArrayResource resource = new ByteArrayResource(content);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/markdown; charset=UTF-8"))
                .contentLength(content.length)
                .body(resource);
    }
}