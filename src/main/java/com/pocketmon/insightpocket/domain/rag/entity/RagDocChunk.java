package com.pocketmon.insightpocket.domain.rag.entity;

import com.pocketmon.insightpocket.global.common.CreatedEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "rag_doc_chunks")
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class RagDocChunk extends CreatedEntity {

    @Id
    @Column(name = "chunk_id", length = 64)
    private String chunkId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doc_id", nullable = false)
    private RagDoc doc;

    @Column(name = "chunk_index", nullable = false)
    private Integer chunkIndex;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "token_count")
    private Integer tokenCount;

    // Oracle 23ai/26ai VECTOR
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(name = "embedding", columnDefinition = "VECTOR(1536, FLOAT32)")
    private float[] embedding;

    // RagDoc.addChunk()에서만 세팅하도록 최소 공개
    protected void setDoc(RagDoc doc) {
        this.doc = doc;
    }
}