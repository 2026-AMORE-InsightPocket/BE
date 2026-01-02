package com.pocketmon.insightpocket.domain.rag.repository;

import com.pocketmon.insightpocket.domain.rag.entity.RagDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RagDocRepository extends JpaRepository<RagDoc, String> {

    @Query("""
        SELECT r
        FROM RagDoc r
        WHERE r.docTypeId = :docTypeId
          AND r.reportDate = :reportDate
        ORDER BY r.createdAt DESC
        """)
    List<RagDoc> findDailyReportsByDate(
            @Param("docTypeId") Long docTypeId,
            @Param("reportDate") LocalDate reportDate
    );
}