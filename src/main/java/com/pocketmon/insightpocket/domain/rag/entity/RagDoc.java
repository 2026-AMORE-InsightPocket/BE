package com.pocketmon.insightpocket.domain.rag.entity;

import com.pocketmon.insightpocket.global.common.CreatedEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "rag_docs")
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@Builder
public class RagDoc extends CreatedEntity {

    @Id
    @Column(name = "doc_id", length = 128)
    private String docId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doc_type_id", nullable = false)
    private RagDocType docType;

    @Column(name = "title", length = 400, nullable = false)
    private String title;

    @Lob
    @Column(name = "body_md", nullable = false)
    private String bodyMd;

    @Column(name = "report_date")
    private LocalDate reportDate;

    @OneToMany(mappedBy = "doc", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("chunkIndex ASC")
    @Builder.Default
    private List<RagDocChunk> chunks = new ArrayList<>();

    // 편의 메서드 (양방향 세팅)
    public void addChunk(RagDocChunk chunk) {
        chunks.add(chunk);
        chunk.setDoc(this);
    }
}