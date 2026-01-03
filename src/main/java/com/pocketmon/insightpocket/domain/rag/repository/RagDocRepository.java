package com.pocketmon.insightpocket.domain.rag.repository;

import com.pocketmon.insightpocket.domain.rag.entity.RagDoc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RagDocRepository extends JpaRepository<RagDoc, String> {

    Optional<RagDoc> findTopByDocType_CodeOrderByReportDateDescCreatedAtDesc(String code);
}