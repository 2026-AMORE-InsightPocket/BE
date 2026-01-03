package com.pocketmon.insightpocket.domain.rag.controller;

import com.pocketmon.insightpocket.domain.rag.entity.RagDoc;
import com.pocketmon.insightpocket.domain.rag.service.DailyReportService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports/daily")
public class DailyReportController {

    private final DailyReportService dailyReportService;

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