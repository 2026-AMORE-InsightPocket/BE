package com.pocketmon.insightpocket.domain.rag.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "rag_doc_types")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class RagDocType {

    @Id
    @Column(name = "id")
    private Short id; // NUMBER(2)

    @Column(name = "code", length = 32, nullable = false, unique = true)
    private String code;
}