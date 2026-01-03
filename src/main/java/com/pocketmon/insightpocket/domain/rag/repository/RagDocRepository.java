package com.pocketmon.insightpocket.domain.rag.repository;

import com.pocketmon.insightpocket.domain.rag.entity.RagDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RagDocRepository extends JpaRepository<RagDoc, String> {

    @Query("""
        SELECT r
        FROM RagDoc r
        WHERE r.docType.code = :code
        ORDER BY r.reportDate DESC NULLS LAST, r.createdAt DESC
""")
    Optional<RagDoc> findLatestOneByDocTypeCode(
            @Param("code") String code
    );
}