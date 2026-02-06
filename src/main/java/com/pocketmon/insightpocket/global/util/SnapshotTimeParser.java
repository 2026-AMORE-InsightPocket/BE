package com.pocketmon.insightpocket.global.util;

import com.pocketmon.insightpocket.global.exception.CustomException;
import com.pocketmon.insightpocket.global.response.ErrorCode;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class SnapshotTimeParser {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private SnapshotTimeParser() {
    }

    public static LocalDateTime parse(String snapshotTimeStr) {
        if (snapshotTimeStr == null || snapshotTimeStr.isBlank()) {
            throw new CustomException(ErrorCode.INGEST_INVALID_SNAPSHOT_TIME);
        }

        try {
            return LocalDateTime.parse(snapshotTimeStr, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new CustomException(ErrorCode.INGEST_INVALID_SNAPSHOT_TIME);
        }
    }
}